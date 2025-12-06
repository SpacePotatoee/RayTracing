package sp.sponge.render.vulkan.screen.image;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import sp.sponge.render.vulkan.device.LogicalDevice;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.LongBuffer;

public class ImageView implements AutoCloseable {
    private final LogicalDevice logicalDevice;
    private final int aspectMask;
    private final int mipLevels;
    private final long imageHandle;
    private final long vkImageViewHandle;
    private VkImageSubresourceRange subresourceRange;

    private ImageView(LogicalDevice logicalDevice, long imageHandle, int aspectMask, int mipLevels, int viewType, int format, int baseArrayLayer, int layerCount) {
        this.logicalDevice = logicalDevice;
        this.aspectMask = aspectMask;
        this.mipLevels = mipLevels;
        this.imageHandle = imageHandle;

        try (MemoryStack stack = MemoryStack.stackPush()) {

            VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.calloc(stack)
                    .sType$Default()
                    .image(imageHandle)
                    .viewType(viewType)
                    .format(format)
                    .subresourceRange(vkImageSubresourceRange -> subresourceRange = vkImageSubresourceRange
                            .aspectMask(aspectMask)
                            .baseMipLevel(0)
                            .levelCount(mipLevels)
                            .baseArrayLayer(baseArrayLayer)
                            .layerCount(layerCount)
                    );

            LongBuffer imageViewHandlePtr = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreateImageView(logicalDevice.getVkDevice(), createInfo, null, imageViewHandlePtr),
                    "Failed to create Image View"
            );
            this.vkImageViewHandle = imageViewHandlePtr.get(0);
        }
    }

    public VkImageSubresourceRange getSubresourceRange() {
        return subresourceRange;
    }

    public int getAspectMask() {
        return aspectMask;
    }

    public int getMipLevels() {
        return mipLevels;
    }

    public long getVkImageViewHandle() {
        return vkImageViewHandle;
    }

    public long getImageHandle() {
        return imageHandle;
    }

    @Override
    public void close() {
        VK10.vkDestroyImageView(this.logicalDevice.getVkDevice(), this.vkImageViewHandle, null);
    }

    public static class ImageViewBuilder {
        int aspectMask;
        int mipLevels;
        int viewType;
        int format;
        int baseArrayLayer;
        int layerCount;

        public ImageViewBuilder() {
            this.aspectMask = VK10.VK_IMAGE_ASPECT_COLOR_BIT;
            this.baseArrayLayer = 0;
            this.layerCount = 1;
            this.mipLevels = 1;
            this.viewType = VK10.VK_IMAGE_VIEW_TYPE_2D;
        }

        public ImageViewBuilder setLayerCount(int layerCount) {
            this.layerCount = layerCount;
            return this;
        }

        public ImageViewBuilder setBaseArrayLayer(int baseArrayLayer) {
            this.baseArrayLayer = baseArrayLayer;
            return this;
        }

        public ImageViewBuilder setFormat(int format) {
            this.format = format;
            return this;
        }

        public ImageViewBuilder setViewType(int viewType) {
            this.viewType = viewType;
            return this;
        }

        public ImageViewBuilder setMipLevels(int mipLevels) {
            this.mipLevels = mipLevels;
            return this;
        }

        public ImageViewBuilder setAspectMask(int aspectMask) {
            this.aspectMask = aspectMask;
            return this;
        }

        public ImageView build(LogicalDevice logicalDevice, long imageHandle) {
            return new ImageView(logicalDevice, imageHandle, this.aspectMask, this.mipLevels, this.viewType, this.format, this.baseArrayLayer, this.layerCount);
        }
    }
}
