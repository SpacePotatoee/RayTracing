package sp.sponge.render.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.Sponge;
import sp.sponge.util.VulkanUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VulkanInstance implements AutoCloseable {
    private final VkInstance instance;
    private long vkDebugHandle;
    private VkDebugUtilsMessengerCreateInfoEXT debugCallback;

    private final List<String> desiredLayers = List.of(
            "VK_LAYER_KHRONOS_validation"
    );
    private final List<String> desiredExtensions = List.of(
            EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME,
            KHRGetSurfaceCapabilities2.VK_KHR_GET_SURFACE_CAPABILITIES_2_EXTENSION_NAME
    );

    private boolean mac;

    public VulkanInstance(boolean debug) {
        Logger logger = Sponge.getInstance().getLogger();
        logger.info("Creating Vulkan Instance");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer name = stack.UTF8("Sponge");
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                    .sType$Default()
                    .pApplicationName(name)
                    .applicationVersion(1)
                    .pEngineName(name)
                    .engineVersion(0)
                    .apiVersion(VK14.VK_API_VERSION_1_3);

            VkInstanceCreateInfo info;
            if (debug) {
                //First get all the desired layers
                PointerBuffer supportedLayersPtr = getSupportedLayers(logger, stack);

                //Then get all the extensions
                PointerBuffer extensionsPtr = getSupportedExtensions(stack);

                debugCallback = VulkanUtils.createDebugCallBack(logger);
                long debugCallbackPtr = debugCallback.address();

                info = VkInstanceCreateInfo.calloc(stack)
                        .sType$Default()
                        .pNext(debugCallbackPtr)
                        .ppEnabledLayerNames(supportedLayersPtr)
                        .ppEnabledExtensionNames(extensionsPtr)
                        .pApplicationInfo(appInfo);

                if (mac) {
                    info.flags(KHRPortabilityEnumeration.VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR);
                }

            } else {
                info = VkInstanceCreateInfo.calloc(stack)
                        .sType$Default()
                        .pNext(VK14.VK_NULL_HANDLE)
                        .pApplicationInfo(appInfo);
            }

            PointerBuffer pointer = stack.mallocPointer(1);
            VulkanUtils.check(
                    VK10.vkCreateInstance(info, null, pointer),
                    "Failed to create VKInstance"
            );
            this.instance = new VkInstance(pointer.get(0), info);

            this.vkDebugHandle = VK14.VK_NULL_HANDLE;
            if (debug) {
                LongBuffer longBuffer = stack.mallocLong(1);
                VulkanUtils.check(
                        EXTDebugUtils.vkCreateDebugUtilsMessengerEXT(this.instance, debugCallback, null, longBuffer),
                        "Failed to create Debug Callback"
                );
                this.vkDebugHandle = longBuffer.get(0);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private PointerBuffer getSupportedLayers(Logger logger, MemoryStack stack) {
        List<String> supportedLayers = getSupportedValidationLayers(logger);
        int numOfSuppLayers = supportedLayers.size();

        if (numOfSuppLayers == 0) {
            logger.severe("Debug enabled yet no supported layers were found");
        }

        PointerBuffer supportedLayersPtr = stack.mallocPointer(numOfSuppLayers);
        for (int i = 0; i < numOfSuppLayers; i++) {
            String layer = supportedLayers.get(i);
            logger.info("Added the " + layer + " layer");
            supportedLayersPtr.put(i, stack.ASCII(layer));
        }

        return supportedLayersPtr;
    }

    private PointerBuffer getSupportedExtensions(MemoryStack stack) {
        List<String> supportedExtensions = getSupportedExtensionsLayers();
        mac = supportedExtensions.contains(KHRPortabilityEnumeration.VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME) &&
                VulkanUtils.getOS() == VulkanUtils.OSType.MACOS;

        PointerBuffer glfwExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
        if (glfwExtensions == null) {
            throw new RuntimeException("Failed to find Vulkan GLFW extensions");
        }

        List<ByteBuffer> additionalExtensions = new ArrayList<>();
        for (String extension : desiredExtensions) {
            additionalExtensions.add(stack.UTF8(extension));
        }

        if (mac) {
            additionalExtensions.add(stack.UTF8(KHRPortabilityEnumeration.VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME));
        }

        int numOfAdditionalExtensions = additionalExtensions.size();

        PointerBuffer supportedExtensionsPtr = stack.mallocPointer(glfwExtensions.capacity() + numOfAdditionalExtensions);
        supportedExtensionsPtr.put(glfwExtensions);
        for (int i = 0; i < numOfAdditionalExtensions; i++) {
            supportedExtensionsPtr.put(additionalExtensions.get(i));
        }
        supportedExtensionsPtr.flip();

        return supportedExtensionsPtr;
    }

    private List<String> getSupportedValidationLayers(Logger logger) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            List<String> supportedLayers = new ArrayList<>();
            IntBuffer numberOfSuppLayersArray = stack.callocInt(1);
            VK10.vkEnumerateInstanceLayerProperties(numberOfSuppLayersArray, null);
            int numberOfSuppLayers = numberOfSuppLayersArray.get(0);


            VkLayerProperties.Buffer propertiesBuffer = VkLayerProperties.calloc(numberOfSuppLayers, stack);
            VK10.vkEnumerateInstanceLayerProperties(numberOfSuppLayersArray, propertiesBuffer);
            for (int i = 0; i < numberOfSuppLayers; i++) {
                VkLayerProperties properties = propertiesBuffer.get(i);
                String layerName = properties.layerNameString();

                supportedLayers.add(layerName);
            }

            List<String> supportedDesiredLayers = new ArrayList<>();
            for (String layer : desiredLayers) {
                if (!supportedLayers.contains(layer)) {
                    logger.severe(layer + " is not supported");
                    continue;
                }

                supportedDesiredLayers.add(layer);
            }

            return supportedDesiredLayers;
        }
    }

    private List<String> getSupportedExtensionsLayers() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            List<String> supportedExtensions = new ArrayList<>();
            IntBuffer numberOfExtArray = stack.callocInt(1);
            VK10.vkEnumerateInstanceExtensionProperties((String) null, numberOfExtArray, null);
            int numberOfExtensions = numberOfExtArray.get(0);


            VkExtensionProperties.Buffer propertiesBuffer = VkExtensionProperties.calloc(numberOfExtensions, stack);
            VK10.vkEnumerateInstanceExtensionProperties((String) null, numberOfExtArray, propertiesBuffer);
            for (int i = 0; i < numberOfExtensions; i++) {
                VkExtensionProperties properties = propertiesBuffer.get(i);
                String layerName = properties.extensionNameString();

                supportedExtensions.add(layerName);
            }

            return supportedExtensions;
        }
    }

    public VkInstance getInstance() {
        return instance;
    }

    @Override
    public void close() {
        if (this.vkDebugHandle != VK14.VK_NULL_HANDLE) {
            EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT(this.instance, this.vkDebugHandle, null);
        }
        if (debugCallback != null) {
            debugCallback.pfnUserCallback().free();
            debugCallback.free();
        }
        VK10.vkDestroyInstance(this.instance, null);
    }
}