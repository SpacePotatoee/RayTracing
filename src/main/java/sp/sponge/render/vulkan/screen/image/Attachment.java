package sp.sponge.render.vulkan.screen.image;

import org.lwjgl.vulkan.VK10;
import sp.sponge.render.vulkan.VulkanCtx;

public class Attachment {
    private final Image image;
    private final ImageView imageView;
    private boolean hasDepth;

    public Attachment(VulkanCtx ctx, int width, int height, int format, int usage) {
        this.image = new Image.ImageBuilder()
                .width(width)
                .height(height)
                .format(format)
                .usage(usage | VK10.VK_IMAGE_USAGE_SAMPLED_BIT)
                .build(ctx);

        int aspectMask = 0;
        if ((usage & VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT) > 0) {
            aspectMask = VK10.VK_IMAGE_ASPECT_COLOR_BIT;
            hasDepth = false;
        }
        if ((usage & VK10.VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT) > 0) {
            aspectMask = VK10.VK_IMAGE_ASPECT_DEPTH_BIT;
            hasDepth = true;
        }

        this.imageView = new ImageView.ImageViewBuilder()
                .setFormat(this.image.getFormat())
                .setAspectMask(aspectMask)
                .build(ctx.getLogicalDevice(), this.image.getVkImage());
    }

    public boolean isHasDepth() {
        return hasDepth;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Image getImage() {
        return image;
    }

    public void free(VulkanCtx ctx) {
        this.image.free(ctx);
        this.imageView.free();
    }

}
