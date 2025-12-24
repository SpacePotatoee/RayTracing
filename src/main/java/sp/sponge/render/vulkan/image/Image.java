package sp.sponge.render.vulkan.image;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;

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

            //TODO: Just make one instead of buffer
            VmaAllocationCreateInfo vmaCreateInfo = VmaAllocationCreateInfo.calloc(1, stack)
                    .get(0)
                    .priority(1.0f)
                    .usage(Vma.VMA_MEMORY_USAGE_AUTO)
                    .flags(Vma.VMA_ALLOCATION_CREATE_DEDICATED_MEMORY_BIT);

            LongBuffer longBuffer = stack.mallocLong(1);
            PointerBuffer memPtr = stack.callocPointer(1);
            VulkanUtils.check(
                    Vma.vmaCreateImage(ctx.getMemoryAllocator().getVmaHandle(), createInfo, vmaCreateInfo, longBuffer, memPtr, null),
                    "Failed to create image"
            );

            this.vkImage = longBuffer.get(0);
            this.vkMemory = memPtr.get(0);
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
