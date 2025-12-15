package sp.sponge.render.vulkan.device;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.lwjgl.vulkan.VK10.VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU;

public class PhysicalDevice implements AutoCloseable {
    private static PhysicalDevice mainPhysicalDevice;
    private final VkPhysicalDevice vkPhysicalDevice;

    //A buffer of the supported extensions
    private final VkExtensionProperties.Buffer deviceExtensions;
    //Information about the different memory heaps the device supports
    private final VkPhysicalDeviceMemoryProperties deviceMemoryProperties;
    //Fine-grained features supported by the device
    private final VkPhysicalDeviceFeatures deviceFeatures;
    //Weird name. The actual device information like name and vendor
    private final VkPhysicalDeviceProperties2 deviceProperties;
    //What queue families are supported
    private final VkQueueFamilyProperties.Buffer deviceQueFamilyProperties;


    private static final List<String> DESIRED_DEVICE_EXTENSIONS = List.of(
            KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME,
            KHRAccelerationStructure.VK_KHR_ACCELERATION_STRUCTURE_EXTENSION_NAME,
            KHRRayTracingPipeline.VK_KHR_RAY_TRACING_PIPELINE_EXTENSION_NAME,
            KHRDeferredHostOperations.VK_KHR_DEFERRED_HOST_OPERATIONS_EXTENSION_NAME
    );

    private static final List<Integer> REQUIRED_QUEUE_FAMILIES = List.of(
            VK10.VK_QUEUE_GRAPHICS_BIT
    );


    private PhysicalDevice(VkPhysicalDevice device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.vkPhysicalDevice = device;

            IntBuffer buffer = stack.callocInt(1);


            this.deviceProperties = VkPhysicalDeviceProperties2.calloc().sType$Default();
            VK14.vkGetPhysicalDeviceProperties2(device, this.deviceProperties);


            VulkanUtils.check(
                    VK10.vkEnumerateDeviceExtensionProperties(device, (String) null, buffer, null),
                    "Failed to get num of physical device extensions"
            );
            this.deviceExtensions = VkExtensionProperties.calloc(buffer.get(0));
            VulkanUtils.check(
                    VK10.vkEnumerateDeviceExtensionProperties(device, (String) null, buffer, this.deviceExtensions),
                    "Failed to get physical device extensions"
            );


            VK10.vkGetPhysicalDeviceQueueFamilyProperties(device, buffer, null);
            this.deviceQueFamilyProperties = VkQueueFamilyProperties.calloc(buffer.get(0));
            VK10.vkGetPhysicalDeviceQueueFamilyProperties(device, buffer, this.deviceQueFamilyProperties);


            this.deviceFeatures = VkPhysicalDeviceFeatures.calloc();
            VK10.vkGetPhysicalDeviceFeatures(device, this.deviceFeatures);


            this.deviceMemoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
            VK10.vkGetPhysicalDeviceMemoryProperties(device, this.deviceMemoryProperties);
        }
    }

    public static PhysicalDevice getMainPhysicalDevice(VkInstance instance, @Nullable String prefDeviceName, Logger logger) {
        if (mainPhysicalDevice != null) {
            return mainPhysicalDevice;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer physDevicesPtr = getPhysicalDevices(instance, stack);
            int numOfDevices = physDevicesPtr.capacity();

            List<PhysicalDevice> devices = new ArrayList<>();
            for (int i = 0; i < numOfDevices; i++) {
                VkPhysicalDevice vkDevice = new VkPhysicalDevice(physDevicesPtr.get(i), instance);
                PhysicalDevice physicalDevice = new PhysicalDevice(vkDevice);

                String deviceName = physicalDevice.getDeviceName();
                if (!physicalDevice.hasRequiredQueueFamilies()) {
                    logger.info("Device " + deviceName + " does not support Queue Families");
                    physicalDevice.close();
                    continue;
                }

                if (!physicalDevice.supportsExtensions(DESIRED_DEVICE_EXTENSIONS, logger)) {
                    logger.info("Device " + deviceName + " does not support the required extensions");
                    physicalDevice.close();
                    continue;
                }

                if (prefDeviceName != null && prefDeviceName.equals(deviceName)) {
                    mainPhysicalDevice = physicalDevice;
                    break;
                }
                if (physicalDevice.deviceProperties.properties().deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
                    devices.addFirst(physicalDevice);
                } else {
                    devices.add(physicalDevice);
                }
            }
            mainPhysicalDevice = mainPhysicalDevice == null && !devices.isEmpty() ? devices.removeFirst() : mainPhysicalDevice;
            devices.forEach(PhysicalDevice::close);

            if (mainPhysicalDevice == null) {
                throw new RuntimeException("No Graphics devices could be found");
            }

        }
        return mainPhysicalDevice;

    }

    private static PointerBuffer getPhysicalDevices(VkInstance instance, MemoryStack stack) {
        IntBuffer numOfDevicesPtr = stack.callocInt(1);
        VulkanUtils.check(
                VK10.vkEnumeratePhysicalDevices(instance, numOfDevicesPtr, null),
                "Failed to get the number of Physical Devices"
        );
        int numOfDevices = numOfDevicesPtr.get(0);

        if (numOfDevices == 0) {
            throw new RuntimeException("No devices were found that supports Vulkan");
        }

        PointerBuffer physDevicesPtr = stack.callocPointer(numOfDevices);
        VulkanUtils.check(
                VK10.vkEnumeratePhysicalDevices(instance, numOfDevicesPtr, physDevicesPtr),
                "Failed to get Physical Devices"
        );

        return physDevicesPtr;
    }

    private boolean supportsExtensions(List<String> extensions, Logger logger) {
        int numOfSupportedExtensions = this.deviceExtensions != null ? this.deviceExtensions.capacity() : 0;
        List<String> extensionsCopy = new ArrayList<>(extensions);

        for (int i = 0; i < numOfSupportedExtensions; i++) {
            String supportedExtension = this.deviceExtensions.get(i).extensionNameString();
            extensionsCopy.remove(supportedExtension);
        }

        boolean result = extensionsCopy.isEmpty();
        if (!result) {
            logger.severe("At least " + extensions.size() + " extensions are not supported by " + this.getDeviceName());
        }

        return result;
    }

    private boolean hasRequiredQueueFamilies() {
        int numOfQueueFamilies = this.deviceQueFamilyProperties != null ? this.deviceQueFamilyProperties.capacity() : 0;

        int numOfAllowedQueues = 0;
        for (int requiredQueFamily : REQUIRED_QUEUE_FAMILIES) {
            for (int i = 0; i < numOfQueueFamilies; i++) {
                VkQueueFamilyProperties familyProperties = this.deviceQueFamilyProperties.get(i);
                if ((familyProperties.queueFlags() & requiredQueFamily) != 0) {
                    numOfAllowedQueues++;
                    break;
                }
            }
        }



        return numOfAllowedQueues == REQUIRED_QUEUE_FAMILIES.size();
    }

    public VkQueueFamilyProperties.Buffer getDeviceQueFamilyProperties() {
        return deviceQueFamilyProperties;
    }

    public VkPhysicalDeviceProperties2 getDeviceProperties() {
        return deviceProperties;
    }

    public VkPhysicalDeviceFeatures getDeviceFeatures() {
        return deviceFeatures;
    }

    public VkPhysicalDeviceMemoryProperties getDeviceMemoryProperties() {
        return deviceMemoryProperties;
    }

    public VkPhysicalDevice getVkPhysicalDevice() {
        return vkPhysicalDevice;
    }

    public VkExtensionProperties.Buffer getDeviceExtensions() {
        return deviceExtensions;
    }

    private String getDeviceName() {
        return this.deviceProperties.properties().deviceNameString();
    }

    @Override
    public void close() {
        this.deviceExtensions.free();
        this.deviceMemoryProperties.free();
        this.deviceFeatures.free();
        this.deviceProperties.free();
        this.deviceQueFamilyProperties.free();
    }
}
