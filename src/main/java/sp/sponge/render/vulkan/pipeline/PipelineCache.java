package sp.sponge.render.vulkan.pipeline;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineCacheCreateInfo;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;

public class PipelineCache {
    private final long vkPipelineCache;

    public PipelineCache(LogicalDevice logicalDevice) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPipelineCacheCreateInfo createInfo = VkPipelineCacheCreateInfo.calloc(stack).sType$Default();

            LongBuffer lb = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreatePipelineCache(logicalDevice.getVkDevice(), createInfo, null, lb),
                    "Failed to create pipeline cache"
            );

            this.vkPipelineCache = lb.get(0);
        }
    }

    public long getVkPipelineCache() {
        return vkPipelineCache;
    }

    public void close(LogicalDevice logicalDevice) {
        VK10.vkDestroyPipelineCache(logicalDevice.getVkDevice(), this.vkPipelineCache, null);
    }

}
