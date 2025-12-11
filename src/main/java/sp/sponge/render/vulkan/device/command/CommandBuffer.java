package sp.sponge.render.vulkan.device.command;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.Sponge;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.device.Queue;
import sp.sponge.render.vulkan.sync.Fence;

import java.nio.IntBuffer;

public class CommandBuffer {
    private final boolean oneTimeSubmit;
    private final boolean primary;
    private final VkCommandBuffer vkCommandBuffer;

    public CommandBuffer(VulkanCtx ctx, CommandPool commandPool, boolean primary, boolean oneTimeSubmit) {
        this.oneTimeSubmit = oneTimeSubmit;
        this.primary = primary;
        VkDevice device = ctx.getLogicalDevice().getVkDevice();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo allocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType$Default()
                    .commandPool(commandPool.getCommandPoolHandle())
                    .level(primary ? VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY : VK10.VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                    .commandBufferCount(1);


            PointerBuffer commandBufferPtr = stack.mallocPointer(1);
            VulkanUtils.check(
                    VK10.vkAllocateCommandBuffers(device, allocateInfo, commandBufferPtr),
                    "Failed to allocate command buffer"
            );
            this.vkCommandBuffer = new VkCommandBuffer(commandBufferPtr.get(0), device);
        }
    }


    public void beginRecordingPrimary() {
        this.beginRecording(null);
    }


    public void beginRecording(@Nullable InheritanceInfo info) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferBeginInfo commandBufferBeginInfo = VkCommandBufferBeginInfo.calloc(stack).sType$Default();
            if (this.oneTimeSubmit) {
                commandBufferBeginInfo.flags(VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            }

            if (!this.primary) {
                if (info == null) {
                    throw new RuntimeException("Secondary buffers must declare Inheritance Info");
                }
                int numOfColorFormats = info.colorFormats.length;
                IntBuffer colorFormats = stack.callocInt(numOfColorFormats);
                for (int i = 0; i < numOfColorFormats; i++) {
                    colorFormats.put(0, info.colorFormats[i]);
                }
                VkCommandBufferInheritanceRenderingInfo inheritanceRenderingInfo = VkCommandBufferInheritanceRenderingInfo.calloc(stack)
                        .sType$Default()
                        .depthAttachmentFormat(info.depthFormat)
                        .pColorAttachmentFormats(colorFormats)
                        .rasterizationSamples(info.rasterizationSamples);
                VkCommandBufferInheritanceInfo vkInheritanceInfo = VkCommandBufferInheritanceInfo.calloc(stack)
                        .sType$Default()
                        .pNext(inheritanceRenderingInfo);
                commandBufferBeginInfo.pInheritanceInfo(vkInheritanceInfo);
            }

            VulkanUtils.check(
                    VK10.vkBeginCommandBuffer(this.vkCommandBuffer, commandBufferBeginInfo),
                    "Failed to begin command buffer"
            );
        }
    }

    public void submitAndWait(VulkanCtx ctx, Queue queue) {
        Fence fence = new Fence(ctx, true);
        fence.reset(ctx);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferSubmitInfo.Buffer submitInfo = VkCommandBufferSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .commandBuffer(this.vkCommandBuffer);
            queue.submit(submitInfo, null, null, fence);
        }
        fence.waitForFence(ctx);
        fence.close(ctx);
    }

    public VkCommandBuffer getVkCommandBuffer() {
        return vkCommandBuffer;
    }

    public void endRecording() {
        VulkanUtils.check(
                VK10.vkEndCommandBuffer(this.vkCommandBuffer),
                "Failed to end Command Buffer"
        );
    }

    public void reset() {
        VK10.vkResetCommandBuffer(this.vkCommandBuffer, VK10.VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);
    }

    public void close(VulkanCtx ctx, CommandPool pool) {
        VK10.vkFreeCommandBuffers(ctx.getLogicalDevice().getVkDevice(), pool.getCommandPoolHandle(), this.vkCommandBuffer);
    }


    public record InheritanceInfo(int depthFormat, int[] colorFormats, int rasterizationSamples) {}
}
