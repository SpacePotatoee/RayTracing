package sp.sponge.render.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.sync.Fence;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.IntBuffer;

public abstract class Queue {
    private final int queueFamilyIndex;
    private final VkQueue queue;

    public Queue(VulkanCtx ctx, int queueIndex) {
        this.queueFamilyIndex = initQueueFamilyIndex(ctx);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer queuePtr = stack.mallocPointer(1);
            VK10.vkGetDeviceQueue(ctx.getLogicalDevice().getVkDevice(), this.queueFamilyIndex, queueIndex, queuePtr);
            long queue = queuePtr.get(0);
            this.queue = new VkQueue(queue, ctx.getLogicalDevice().getVkDevice());
        }
    }

    public void submit(VkCommandBufferSubmitInfo.Buffer commandBuffers, VkSemaphoreSubmitInfo.Buffer waitForSemaphores,
                       VkSemaphoreSubmitInfo.Buffer signalSemaphores, Fence fence) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSubmitInfo2.Buffer submitInfo = VkSubmitInfo2.calloc(1, stack)
                    .sType$Default()
                    .pCommandBufferInfos(commandBuffers)
                    .pSignalSemaphoreInfos(signalSemaphores);

            if (waitForSemaphores != null) {
                submitInfo.pWaitSemaphoreInfos(waitForSemaphores);
            }

            long fenceHandle = fence != null ? fence.getVkFence() : VK10.VK_NULL_HANDLE;

            VulkanUtils.check(
                    VK13.vkQueueSubmit2(this.queue, submitInfo, fenceHandle),
                    "Failed to submit Queue"
            );
        }
    }

    abstract int initQueueFamilyIndex(VulkanCtx ctx);

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
        public int initQueueFamilyIndex(VulkanCtx ctx) {
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
}
