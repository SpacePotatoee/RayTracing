package sp.sponge.render.vulkan.device.command;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import sp.sponge.Sponge;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.util.VulkanUtils;

import java.nio.LongBuffer;

public class CommandPool implements AutoCloseable {
    private final long commandPoolHandle;

    public CommandPool(VulkanCtx ctx, int queueFamilyIndex, boolean supportReset) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo createInfo = VkCommandPoolCreateInfo.calloc(stack)
                    .sType$Default()
                    .queueFamilyIndex(queueFamilyIndex);
            if (supportReset) {
                createInfo.flags(VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
            }

            LongBuffer commandPoolPtr = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreateCommandPool(ctx.getLogicalDevice().getVkDevice(), createInfo, null, commandPoolPtr),
                    "Failed to create Command Pool"
            );
            this.commandPoolHandle = commandPoolPtr.get(0);
        }
    }

    public long getCommandPoolHandle() {
        return commandPoolHandle;
    }

    public void reset() {
        VK10.vkResetCommandPool(Sponge.getInstance().getMainRenderer().getVulkanCtx().getLogicalDevice().getVkDevice(), this.commandPoolHandle, 0);
    }

    @Override
    public void close() {
        VK10.vkDestroyCommandPool(Sponge.getInstance().getMainRenderer().getVulkanCtx().getLogicalDevice().getVkDevice(), this.commandPoolHandle, null);
    }
}
