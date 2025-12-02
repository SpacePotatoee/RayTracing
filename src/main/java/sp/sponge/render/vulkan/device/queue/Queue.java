package sp.sponge.render.vulkan.device.queue;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import sp.sponge.render.vulkan.VulkanCtx;

import java.nio.IntBuffer;

public abstract class Queue {
    private final int queueFamilyIndex;
    private final VkQueue queue;

    public Queue(VulkanCtx ctx, int queueIndex) {
        this.queueFamilyIndex = getQueueFamilyIndex(ctx);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer queuePtr = stack.mallocPointer(1);
            VK10.vkGetDeviceQueue(ctx.getLogicalDevice().getVkDevice(), this.queueFamilyIndex, queueIndex, queuePtr);
            long queue = queuePtr.get(0);
            this.queue = new VkQueue(queue, ctx.getLogicalDevice().getVkDevice());
        }
    }

    abstract int getQueueFamilyIndex(VulkanCtx ctx);

    public int getQueueFamilyIndex() {
        return queueFamilyIndex;
    }

    public VkQueue getQueue() {
        return queue;
    }

    public void waitIdle() {
        VK10.vkQueueWaitIdle(this.queue);
    }

    public static class GraphicsQueue extends Queue {
        public GraphicsQueue(VulkanCtx ctx, int queueIndex) {
            super(ctx, queueIndex);
        }

        @Override
        public int getQueueFamilyIndex(VulkanCtx ctx) {
            int index = -1;
            VkQueueFamilyProperties.Buffer propertiesBuffer = ctx.getPhysicalDevice().getDeviceQueFamilyProperties();
            int numOfQueueFamilies = propertiesBuffer.capacity();
            for (int i = 0; i < numOfQueueFamilies; i++) {
                VkQueueFamilyProperties properties = propertiesBuffer.get(i);
                if ((properties.queueFlags() & VK10.VK_QUEUE_GRAPHICS_BIT) != 0) {
                    index = i;
                    break;
                }
            }

            if (index < 0) {
                throw new RuntimeException("Failed to get graphics Queue Family index");
            }

            return index;
        }
    }

    public static class PresentQueue extends Queue {
        public PresentQueue(VulkanCtx ctx, int queueIndex) {
            super(ctx, queueIndex);
        }

        @Override
        public int getQueueFamilyIndex(VulkanCtx ctx) {
            int index = -1;
            try (MemoryStack stack = MemoryStack.stackPush()) {
                VkQueueFamilyProperties.Buffer propertiesBuffer = ctx.getPhysicalDevice().getDeviceQueFamilyProperties();
                int numOfQueueFamilies = propertiesBuffer.capacity();
                IntBuffer intBuffer = stack.callocInt(1);
                for (int i = 0; i < numOfQueueFamilies; i++) {

                    KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(
                            ctx.getPhysicalDevice().getVkPhysicalDevice(),
                            i,
                            ctx.getSurface().getSurfaceHandle(),
                            intBuffer
                    );

                    if (intBuffer.get() == VK10.VK_TRUE) {
                        index = i;
                        break;
                    }
                }
            }

            if (index < 0) {
                throw new RuntimeException("Failed to get present Queue Family index");
            }

            return index;
        }
    }
}
