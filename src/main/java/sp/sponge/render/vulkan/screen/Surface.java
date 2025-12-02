package sp.sponge.render.vulkan.screen;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.Window;
import sp.sponge.render.vulkan.VulkanInstance;
import sp.sponge.render.vulkan.device.PhysicalDevice;
import sp.sponge.util.VulkanUtils;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class Surface implements AutoCloseable {
    private final VkInstance vkInstance;
    private final VkSurfaceCapabilitiesKHR surfaceCapabilities;
    private final SurfaceFormat surfaceFormat;
    private final long surfaceHandle;

    public Surface(VulkanInstance instance, PhysicalDevice device, Window window) {
        this.vkInstance = instance.getInstance();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer surfacePointer = stack.mallocLong(1);
            GLFWVulkan.glfwCreateWindowSurface(instance.getInstance(), window.getHandle(), null, surfacePointer);
            this.surfaceHandle = surfacePointer.get(0);

            this.surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc();
            VulkanUtils.check(
                    KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device.getVkPhysicalDevice(), this.surfaceHandle, this.surfaceCapabilities),
                    "Failed to get surface capabilities"
            );
            this.surfaceFormat = getSurfaceFormat(device, stack);
        }
    }

    public SurfaceFormat getSurfaceFormat(PhysicalDevice device, MemoryStack stack) {
        int imageFormat;
        int colorSpace;
        IntBuffer formatCount = stack.mallocInt(1);
        VulkanUtils.check(
                KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(device.getVkPhysicalDevice(), this.surfaceHandle, formatCount, null),
                "Failed tp get number surface formats"
        );
        int numOfFormats = formatCount.get(0);

        VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.calloc(numOfFormats, stack);
        VulkanUtils.check(
                KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(device.getVkPhysicalDevice(), this.surfaceHandle, formatCount, surfaceFormats),
                "Failed tp get surface formats"
        );

        imageFormat = VK10.VK_FORMAT_B8G8R8A8_SRGB;
        colorSpace = surfaceFormats.get(0).colorSpace();
        for (int i = 0; i < numOfFormats; i++) {
            VkSurfaceFormatKHR surfaceFormat = surfaceFormats.get(0);
            if (surfaceFormat.format() == VK10.VK_FORMAT_B8G8R8A8_SRGB && surfaceFormat.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
                imageFormat = surfaceFormat.format();
                colorSpace = surfaceFormats.colorSpace();
                break;
            }
        }

        return new SurfaceFormat(imageFormat, colorSpace);
    }

    @Override
    public void close() {
        this.surfaceCapabilities.free();
        KHRSurface.vkDestroySurfaceKHR(this.vkInstance, this.surfaceHandle, null);
    }

    public SurfaceFormat getSurfaceFormat() {
        return surfaceFormat;
    }

    public VkSurfaceCapabilitiesKHR getSurfaceCapabilities() {
        return surfaceCapabilities;
    }

    public long getSurfaceHandle() {
        return surfaceHandle;
    }

    public record SurfaceFormat(int imageFormat, int colorSpace){}
}
