package sp.sponge.render.vulkan.image;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSamplerCreateInfo;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.LongBuffer;

public class TextureSampler {
    private final long vkSampler;

    private TextureSampler(VulkanCtx ctx, TextureSamplerBuilder builder) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSamplerCreateInfo createInfo = VkSamplerCreateInfo.calloc(stack)
                    .sType$Default()
                    .magFilter(builder.filter)
                    .minFilter(builder.filter)
                    .addressModeU(builder.wrap)
                    .addressModeV(builder.wrap)
                    .addressModeW(builder.wrap)
                    .borderColor(builder.borderColor)
                    .unnormalizedCoordinates(false)
                    .compareOp(VK10.VK_COMPARE_OP_NEVER)
                    .mipmapMode(VK10.VK_SAMPLER_MIPMAP_MODE_NEAREST)
                    .minLod(0.0f)
                    .maxLod(builder.mipMapLevels)
                    .mipLodBias(0.0f);

            LongBuffer longBuffer = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreateSampler(ctx.getLogicalDevice().getVkDevice(), createInfo, null, longBuffer),
                    "Failed to create texture sampler"
            );
            this.vkSampler = longBuffer.get(0);
        }
    }

    public long getVkSampler() {
        return vkSampler;
    }

    public void free(VulkanCtx ctx) {
        VK10.vkDestroySampler(ctx.getLogicalDevice().getVkDevice(), this.vkSampler, null);
    }


    public static class TextureSamplerBuilder {
        private int filter;
        private int wrap;
        private int borderColor;
        private int mipMapLevels;

        public TextureSamplerBuilder() {
            this.filter = VK10.VK_FILTER_NEAREST;
            this.wrap = VK10.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE;
            this.borderColor = 0;
            this.mipMapLevels(1);
        }

        public void filter(int filter) {
            this.filter = filter;
        }

        public void borderColor(int borderColor) {
            this.borderColor = borderColor;
        }

        public void wrap(int wrap) {
            this.wrap = wrap;
        }

        public void mipMapLevels(int mipMapLevels) {
            this.mipMapLevels = mipMapLevels;
        }

        public TextureSampler build(VulkanCtx ctx) {
            return new TextureSampler(ctx, this);
        }
    }
}
