package sp.sponge.render.vulkan.sync;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.LongBuffer;

public class Fence {
    private final long vkFence;

    public Fence(VulkanCtx ctx, boolean signaled) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFenceCreateInfo createInfo = VkFenceCreateInfo.calloc(stack)
                    .sType$Default()
                    .flags(signaled ? VK10.VK_FENCE_CREATE_SIGNALED_BIT : 0);

            LongBuffer fencePtr = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreateFence(ctx.getLogicalDevice().getVkDevice(), createInfo, null, fencePtr),
                    "Failed to create fence"
            );
            this.vkFence = fencePtr.get(0);
        }
    }

    public void cleanUp(VulkanCtx ctx) {
        VK10.vkDestroyFence(ctx.getLogicalDevice().getVkDevice(), this.vkFence, null);
    }

    public void waitForFence(VulkanCtx ctx) {
        VK10.vkWaitForFences(ctx.getLogicalDevice().getVkDevice(), this.vkFence, true, Long.MAX_VALUE);
    }

    public void reset(VulkanCtx ctx) {
        VK10.vkResetFences(ctx.getLogicalDevice().getVkDevice(), this.vkFence);
    }

    public long getVkFence() {
        return vkFence;
    }
}
