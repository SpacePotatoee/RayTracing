package sp.sponge.render.vulkan.screen;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;
import sp.sponge.render.Window;
import sp.sponge.render.vulkan.device.LogicalDevice;
import sp.sponge.render.vulkan.device.Queue;
import sp.sponge.render.vulkan.screen.image.ImageView;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.sync.Semaphore;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public class SwapChain implements AutoCloseable {
    private final LogicalDevice logicalDevice;
    private final long swapChainHandle;
    private final ImageView[] imageViews;
    private final int numOfImages;
    private VkExtent2D extent2D;

    public SwapChain(Window window, LogicalDevice logicalDevice, Surface surface, int numOfImages, boolean vsync) {
        this.logicalDevice = logicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSurfaceCapabilitiesKHR surfaceCapabilities = surface.getSurfaceCapabilities();

            int minImageCount = calcNumOfImages(surfaceCapabilities, numOfImages);
            this.extent2D = calcExtent(window, surfaceCapabilities, stack);

            Surface.SurfaceFormat surfaceFormat = surface.getSurfaceFormat();
            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack)
                    .sType$Default()
                    .surface(surface.getSurfaceHandle())
                    .minImageCount(minImageCount)
                    .imageFormat(surfaceFormat.imageFormat())
                    .imageColorSpace(surfaceFormat.colorSpace())
                    .imageExtent(this.extent2D)
                    .imageArrayLayers(1)
                    .imageUsage(VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                    .imageSharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE)
                    .preTransform(surfaceCapabilities.currentTransform())
                    .compositeAlpha(KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                    .clipped(true)
                    .presentMode(vsync ? KHRSurface.VK_PRESENT_MODE_FIFO_KHR : KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR);

            LongBuffer swapChainPtr = stack.mallocLong(1);
            VulkanUtils.check(
                    KHRSwapchain.vkCreateSwapchainKHR(logicalDevice.getVkDevice(), createInfo, null, swapChainPtr),
                    "Failed to create Swap Chain"
            );

            this.swapChainHandle = swapChainPtr.get(0);
            this.imageViews = createImageViews(stack, logicalDevice, surfaceFormat.imageFormat());
            this.numOfImages = this.imageViews.length;

            this.extent2D = VkExtent2D.calloc(stack).set(this.extent2D);
        }


    }

    public void presentImage(Queue.PresentQueue presentQueue, Semaphore semaphore, int imageIndex) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack)
                    .sType$Default()
                    .pWaitSemaphores(stack.longs(semaphore.getVkSemaphore()))
                    .swapchainCount(1)
                    .pSwapchains(stack.longs(this.swapChainHandle))
                    .pImageIndices(stack.ints(imageIndex));

            KHRSwapchain.vkQueuePresentKHR(presentQueue.getQueue(), presentInfo);
        }
    }

    public int getNextImage(LogicalDevice logicalDevice, Semaphore semaphore) {
        int imageIndex;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer imagePtr = stack.mallocInt(1);
            int error = KHRSwapchain.vkAcquireNextImageKHR(
                    logicalDevice.getVkDevice(),
                    this.swapChainHandle,
                    ~0L,
                    semaphore.getVkSemaphore(),
                    MemoryUtil.NULL,
                    imagePtr
            );

            switch (error) {
                case KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR -> {
                    return -1;
                }
                case KHRSwapchain.VK_SUBOPTIMAL_KHR, VK10.VK_SUCCESS -> imageIndex = imagePtr.get(0);
                default -> throw new RuntimeException("Failed to get next image from the Swap Chain");
            }

            return imageIndex;
        }
    }

    private ImageView[] createImageViews(MemoryStack stack, LogicalDevice logicalDevice, int format) {
        IntBuffer numOfImagesBuf = stack.mallocInt(1);
        VulkanUtils.check(
                KHRSwapchain.vkGetSwapchainImagesKHR(logicalDevice.getVkDevice(), this.swapChainHandle, numOfImagesBuf, null),
                "Failed to get the number of Swap Chain Images"
        );
        int numOfImages = numOfImagesBuf.get(0);

        LongBuffer imagePtrBuffer = stack.mallocLong(numOfImages);
        VulkanUtils.check(
                KHRSwapchain.vkGetSwapchainImagesKHR(logicalDevice.getVkDevice(), this.swapChainHandle, numOfImagesBuf, imagePtrBuffer),
                "Failed to get Swap Chain Images"
        );

        ImageView[] result = new ImageView[numOfImages];
        ImageView.ImageViewBuilder builder = new ImageView.ImageViewBuilder().setFormat(format).setAspectMask(VK10.VK_IMAGE_ASPECT_COLOR_BIT);
        for (int i = 0; i < numOfImages; i++) {
            result[i] = builder.build(logicalDevice, imagePtrBuffer.get(i));
        }

        return result;
    }

    private int calcNumOfImages(VkSurfaceCapabilitiesKHR surfaceCapabilities, int numOfImages) {
        int result;
        System.out.println(surfaceCapabilities.minImageCount() + "======");
        if (surfaceCapabilities.maxImageCount() == 0) { //No limit
            result = Math.max(numOfImages, surfaceCapabilities.minImageCount());
        } else {
            result = Math.clamp(numOfImages, surfaceCapabilities.minImageCount(), surfaceCapabilities.maxImageCount());
        }

        return result;
    }


    private static VkExtent2D calcExtent(Window window, VkSurfaceCapabilitiesKHR surfaceCapabilities, MemoryStack stack) {
        VkExtent2D result = VkExtent2D.calloc(stack);

        if (surfaceCapabilities.currentExtent().width() == 0xFFFFFFFF) {
            //No extent was set
            VkExtent2D minExtent = surfaceCapabilities.minImageExtent();
            VkExtent2D maxExtent = surfaceCapabilities.maxImageExtent();

            int width = Math.clamp(window.getWidth(), minExtent.width(), maxExtent.width());
            int height = Math.clamp(window.getHeight(), minExtent.height(), maxExtent.height());

            result.set(width, height);
        } else {
            result.set(surfaceCapabilities.currentExtent());
        }

        return result;
    }


    public long getSwapChainHandle() {
        return swapChainHandle;
    }

    public ImageView[] getImageViews() {
        return imageViews;
    }

    public int getNumOfImages() {
        return numOfImages;
    }

    public VkExtent2D getExtent2D() {
        return this.extent2D;
    }

    @Override
    public void close() {
        this.extent2D.free();
        Arrays.asList(imageViews).forEach(ImageView::close);
        KHRSwapchain.vkDestroySwapchainKHR(this.logicalDevice.getVkDevice(), this.swapChainHandle, null);
    }
}
