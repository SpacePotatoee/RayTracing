package sp.sponge.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.device.command.CommandBuffer;
import sp.sponge.render.vulkan.device.command.CommandPool;
import sp.sponge.render.vulkan.device.Queue;
import sp.sponge.render.vulkan.screen.image.ImageView;
import sp.sponge.render.vulkan.sync.Fence;
import sp.sponge.render.vulkan.sync.Semaphore;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainRenderer {
    private static final float [] clearColor = new float[] {1.0f, 0.0f, 0.0f, 1.0f};
    private final VulkanCtx vulkanCtx;
    private final Queue.GraphicsQueue graphicsQueue;
    private final Queue.PresentQueue presentQueue;
    private final CommandPool commandPool;
    private final List<CommandBuffer> commandBuffers = new ArrayList<>();
    private final VkRenderingInfo[] renderingInfo;

    private final Fence fences;
    private final Semaphore renderSemaphores;
    private final Semaphore presentSemaphores;

//    private final Fence[] fences;
//    private final Semaphore[] renderSemaphores;
//    private final Semaphore[] presentSemaphores;

    private static int currentFrame = 0;

    public MainRenderer() {
        this.vulkanCtx = new VulkanCtx();
        this.graphicsQueue = new Queue.GraphicsQueue(this.vulkanCtx, 1);
        this.presentQueue = new Queue.PresentQueue(this.vulkanCtx, 1);
        this.commandPool = new CommandPool(this.vulkanCtx, this.graphicsQueue.getQueueFamilyIndex(), true);

        int numOfImages = this.vulkanCtx.getSwapChain().getNumOfImages();
        this.createCommandBuffers(numOfImages);

        this.fences = new Fence(this.vulkanCtx, true);
        this.renderSemaphores = new Semaphore(this.vulkanCtx);
        this.presentSemaphores = new Semaphore(this.vulkanCtx);

//        this.fences = new Fence[numOfImages];
//        this.renderSemaphores = new Semaphore[numOfImages];
//        this.presentSemaphores = new Semaphore[numOfImages];
//        for (int i = 0; i < numOfImages; i++) {
//            this.fences[i] = new Fence(this.vulkanCtx, true);
//            this.renderSemaphores[i] = new Semaphore(this.vulkanCtx);
//            this.presentSemaphores[i] = new Semaphore(this.vulkanCtx);
//        }

        this.renderingInfo = setupRenderInfo(numOfImages);
    }

    public VkRenderingInfo[] setupRenderInfo(int numOfImages) {
        VkRenderingInfo[] result = new VkRenderingInfo[numOfImages];

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkRect2D renderArea = VkRect2D.calloc(stack).extent(this.vulkanCtx.getSwapChain().getExtent2D());
            System.out.println(renderArea.extent().width());

            FloatBuffer floatBuffer = stack.callocFloat(4);
            floatBuffer.put(clearColor);
            floatBuffer.flip();

            VkClearColorValue colorValue = VkClearColorValue.calloc(stack).float32(floatBuffer);

            VkClearValue clearValue = VkClearValue.calloc(stack).color(colorValue);

            for (int i = 0; i < numOfImages; i++) {
                ImageView imageView = this.vulkanCtx.getSwapChain().getImageViews()[i];

                VkRenderingAttachmentInfo.Buffer colorAttachmentBuffer = VkRenderingAttachmentInfo.calloc(1)
                        .sType$Default()
                        .imageView(imageView.getVkImageViewHandle())
                        .imageLayout(KHRSynchronization2.VK_IMAGE_LAYOUT_ATTACHMENT_OPTIMAL_KHR)
                        .loadOp(VK10.VK_ATTACHMENT_LOAD_OP_CLEAR)
                        .storeOp(VK10.VK_ATTACHMENT_STORE_OP_STORE)
                        .clearValue(clearValue);

                VkRenderingInfo renderingInfo = VkRenderingInfo.calloc()
                        .sType$Default()
                        .renderArea(renderArea)
                        .layerCount(1)
                        .pColorAttachments(colorAttachmentBuffer);

                result[i] = renderingInfo;
            }

            return result;
        }
    }

    public void render() {
        CommandBuffer buffer = commandBuffers.get(currentFrame);

        this.fences.waitForFence(this.vulkanCtx);
//        this.fences[currentFrame].waitForFence(this.vulkanCtx);

        buffer.reset();
        buffer.beginRecordingPrimary();

        int imageIndex = this.vulkanCtx.getSwapChain().getNextImage(this.vulkanCtx.getLogicalDevice(), this.presentSemaphores);
//        int imageIndex = this.vulkanCtx.getSwapChain().getNextImage(this.vulkanCtx.getLogicalDevice(), this.presentSemaphores[currentFrame]);
        this.renderScene(buffer.getVkCommandBuffer(), imageIndex);

        buffer.endRecording();

        this.submit(buffer);
        this.vulkanCtx.getSwapChain().presentImage(this.presentQueue, this.renderSemaphores, imageIndex);
//        this.vulkanCtx.getSwapChain().presentImage(this.presentQueue, this.renderSemaphores[currentFrame], imageIndex);

        currentFrame = (currentFrame + 1) % (VulkanUtils.MAX_FRAMES_IN_FLIGHT + 1);
    }

    public void renderScene(VkCommandBuffer buffer, int imageIndex) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ImageView imageView = this.vulkanCtx.getSwapChain().getImageViews()[imageIndex];
            long swapChainImage = imageView.getImageHandle();

            VulkanUtils.imageBarrier(stack, buffer, swapChainImage,
                    VK10.VK_IMAGE_LAYOUT_UNDEFINED,
                    VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
                    VK13.VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT,
                    VK13.VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT,
                    VK13.VK_ACCESS_2_NONE,
                    VK13.VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT,
                    VK10.VK_IMAGE_ASPECT_COLOR_BIT
            );

            VK13.vkCmdBeginRendering(buffer, this.renderingInfo[currentFrame]);
            VK13.vkCmdEndRendering(buffer);

            VulkanUtils.imageBarrier(stack, buffer, swapChainImage,
                    VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
                    KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR,
                    VK13.VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT,
                    VK13.VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT,
                    VK13.VK_ACCESS_2_COLOR_ATTACHMENT_READ_BIT | VK13.VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT,
                    VK13.VK_PIPELINE_STAGE_2_NONE,
                    VK10.VK_IMAGE_ASPECT_COLOR_BIT
            );
        }
    }

    private void submit(CommandBuffer buffer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.fences.reset(this.vulkanCtx);
//            this.fences[currentFrame].reset(this.vulkanCtx);
            VkCommandBufferSubmitInfo.Buffer cmdSubmitBuffer = VkCommandBufferSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .commandBuffer(buffer.getVkCommandBuffer());

            VkSemaphoreSubmitInfo.Buffer waitSemaphores = VkSemaphoreSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .stageMask(VK13.VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .semaphore(this.presentSemaphores.getVkSemaphore());
//                    .semaphore(this.presentSemaphores[currentFrame].getVkSemaphore());

            VkSemaphoreSubmitInfo.Buffer signalSemaphores = VkSemaphoreSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .stageMask(VK13.VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT)
                    .semaphore(this.renderSemaphores.getVkSemaphore());
//                    .semaphore(this.renderSemaphores[currentFrame].getVkSemaphore());

            graphicsQueue.submit(cmdSubmitBuffer, waitSemaphores, signalSemaphores, this.fences);
//            graphicsQueue.submit(cmdSubmitBuffer, waitSemaphores, signalSemaphores, this.fences[currentFrame]);
        }
    }

    private void createCommandBuffers(int num) {
        for (int i = 0; i < num; i++) {
            commandBuffers.add(new CommandBuffer(this.vulkanCtx, this.commandPool, true, true));
        }
    }

    public VulkanCtx getVulkanCtx() {
        return vulkanCtx;
    }

    public void close() {
        this.vulkanCtx.free();
    }
}
