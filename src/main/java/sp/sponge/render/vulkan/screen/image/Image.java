package sp.sponge.render.vulkan.screen.image;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;

public class Image {
    private final int format;
    private final int mipLevels;
    private final long vkImage;
    private final long vkMemory;

    private Image(VulkanCtx ctx, ImageBuilder builder) {
        this.format = builder.format;
        this.mipLevels = builder.mipLevels;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            LogicalDevice logicalDevice = ctx.getLogicalDevice();
            LongBuffer longBuffer = stack.mallocLong(1);

            //Create the image
            VkImageCreateInfo createInfo = VkImageCreateInfo.calloc(stack)
                    .sType$Default()
                    .imageType(VK10.VK_IMAGE_TYPE_2D)
                    .format(this.format)
                    .mipLevels(this.mipLevels)
                    .extent(vkExtent3D -> vkExtent3D
                            .width(builder.width)
                            .height(builder.height)
                            .depth(1))
                    .arrayLayers(builder.arrayLayers)
                    .samples(builder.sampleCount)
                    .initialLayout(VK10.VK_IMAGE_LAYOUT_UNDEFINED)
                    .sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE)
                    .tiling(VK10.VK_IMAGE_TILING_OPTIMAL)
                    .usage(builder.usage);

            VulkanUtils.check(
                    VK10.vkCreateImage(logicalDevice.getVkDevice(), createInfo, null, longBuffer),
                    "Failed to create image for attachment"
            );
            this.vkImage = longBuffer.get(0);


            //Allocate memory for the image
            VkMemoryRequirements memoryRequirements = VkMemoryRequirements.calloc(stack);
            VK10.vkGetImageMemoryRequirements(logicalDevice.getVkDevice(), this.vkImage, memoryRequirements);

            VkMemoryAllocateInfo allocateInfo = VkMemoryAllocateInfo.calloc(stack)
                    .sType$Default()
                    .allocationSize(memoryRequirements.size())
                    .memoryTypeIndex(VulkanUtils.getMemoryType(ctx, memoryRequirements.memoryTypeBits(), 0));

            VulkanUtils.check(
                    VK10.vkAllocateMemory(logicalDevice.getVkDevice(), allocateInfo, null, longBuffer),
                    "Failed to allocate memory for image attachment"
            );
            this.vkMemory = longBuffer.get(0);


            //Bind the image to the memory
            VulkanUtils.check(
                    VK10.vkBindImageMemory(logicalDevice.getVkDevice(), this.vkImage, this.vkMemory, 0),
                    "Failed to bind image to memory"
            );
        }
    }

    public long getVkImage() {
        return vkImage;
    }

    public int getMipLevels() {
        return mipLevels;
    }

    public int getFormat() {
        return format;
    }

    public void free(VulkanCtx ctx) {
        VkDevice vkDevice = ctx.getLogicalDevice().getVkDevice();
        VK10.vkDestroyImage(vkDevice, this.vkImage, null);
        VK10.vkFreeMemory(vkDevice, this.vkMemory, null);
    }


    public static class ImageBuilder {
        private int arrayLayers;
        private int format;
        private int width;
        private int height;
        private int mipLevels;
        private int sampleCount;
        private int usage;

        public ImageBuilder() {
            this.format = VK10.VK_FORMAT_R8G8B8A8_SRGB;
            mipLevels = 1;
            sampleCount = 1;
            arrayLayers = 1;
        }

        public ImageBuilder arrayLayers(int arrayLayers) {
            this.arrayLayers = arrayLayers;
            return this;
        }

        public ImageBuilder format(int format) {
            this.format = format;
            return this;
        }

        public ImageBuilder width(int width) {
            this.width = width;
            return this;
        }

        public ImageBuilder height(int height) {
            this.height = height;
            return this;
        }

        public ImageBuilder mipLevels(int mipLevels) {
            this.mipLevels = mipLevels;
            return this;
        }

        public ImageBuilder sampleCount(int sampleCount) {
            this.sampleCount = sampleCount;
            return this;
        }

        public ImageBuilder usage(int usage) {
            this.usage = usage;
            return this;
        }

        public Image build(VulkanCtx ctx) {
            return new Image(ctx, this);
        }

    }
}
