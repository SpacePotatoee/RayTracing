package sp.sponge.render.vulkan.buffer.descriptors;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.LongBuffer;

public class DescriptorSetLayout {
    private final long vkDescriptorSetLayout;
    private final LayoutInfo[] layoutInfos;

    public DescriptorSetLayout(VulkanCtx ctx, LayoutInfo layoutInfo) {
        this(ctx, new LayoutInfo[]{layoutInfo});
    }

    public DescriptorSetLayout(VulkanCtx ctx, LayoutInfo[] layoutInfos) {
        this.layoutInfos = layoutInfos;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int numOfLayouts = layoutInfos.length;
            VkDescriptorSetLayoutBinding.Buffer descriptorSetLayoutBindings = VkDescriptorSetLayoutBinding.calloc(numOfLayouts, stack);
            for (int i = 0; i < numOfLayouts; i++) {
                LayoutInfo layoutInfo = layoutInfos[i];
                descriptorSetLayoutBindings.get(i)
                        .binding(layoutInfo.binding)
                        .descriptorType(layoutInfo.descriptorType)
                        .descriptorCount(layoutInfo.descriptorCount)
                        .stageFlags(layoutInfo.stage);
            }

            VkDescriptorSetLayoutCreateInfo createInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack)
                    .sType$Default()
                    .pBindings(descriptorSetLayoutBindings);

            LongBuffer longBuffer = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreateDescriptorSetLayout(ctx.getLogicalDevice().getVkDevice(), createInfo, null, longBuffer),
                    "Failed to create the descriptor set layout"
            );
            this.vkDescriptorSetLayout = longBuffer.get(0);
        }
    }

    public long getVkDescriptorSetLayout() {
        return vkDescriptorSetLayout;
    }

    public LayoutInfo getLayoutInfo() {
        return this.layoutInfos[0];
    }

    public LayoutInfo[] getLayoutInfos() {
        return layoutInfos;
    }

    public void free(VulkanCtx ctx) {
        VK10.vkDestroyDescriptorSetLayout(ctx.getLogicalDevice().getVkDevice(), this.vkDescriptorSetLayout, null);
    }

    public record LayoutInfo(int descriptorType, int binding, int descriptorCount, int stage) {}
}
