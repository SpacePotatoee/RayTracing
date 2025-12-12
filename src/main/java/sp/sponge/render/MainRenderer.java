package sp.sponge.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.buffer.TriangleBuffers;
import sp.sponge.render.vulkan.device.command.CommandBuffer;
import sp.sponge.render.vulkan.device.command.CommandPool;
import sp.sponge.render.vulkan.device.Queue;
import sp.sponge.render.vulkan.model.VertexBufferStruct;
import sp.sponge.render.vulkan.pipeline.Pipeline;
import sp.sponge.render.vulkan.pipeline.shaders.ShaderCompiler;
import sp.sponge.render.vulkan.pipeline.shaders.ShaderModule;
import sp.sponge.render.vulkan.screen.image.Attachment;
import sp.sponge.render.vulkan.screen.image.ImageView;
import sp.sponge.render.vulkan.sync.Fence;
import sp.sponge.render.vulkan.sync.Semaphore;
import sp.sponge.scene.SceneManager;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.objects.custom.Circle;
import sp.sponge.scene.objects.custom.Sphere;
import sp.sponge.scene.objects.custom.Square;
import sp.sponge.scene.objects.custom.obj.Bunny;
import sp.sponge.scene.objects.custom.obj.Cube;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainRenderer {
    private static final String FRAGMENT_SHADER_FILE_GLSL = "shaders/default/default_frag.glsl";
    private static final String FRAGMENT_SHADER_FILE_SPV = FRAGMENT_SHADER_FILE_GLSL + ".spv";

    private static final String VERTEX_SHADER_FILE_GLSL = "shaders/default/default_vert.glsl";
    private static final String VERTEX_SHADER_FILE_SPV = VERTEX_SHADER_FILE_GLSL + ".spv";

    private static final float [] clearColor = new float[] {0.0f, 0.0f, 0.0f, 0.0f};
    private final Camera camera;
    private final VulkanCtx vulkanCtx;
    private final Queue.GraphicsQueue graphicsQueue;
    private final Queue.PresentQueue presentQueue;
    private final CommandPool commandPool;
    private final List<CommandBuffer> commandBuffers = new ArrayList<>();
    private Attachment[] depthAttachments;
    private VkRenderingInfo[] renderingInfo;

    private final Fence fences;
    private Semaphore renderSemaphores;
    private Semaphore presentSemaphores;

    private final ByteBuffer pushConstBuff;

    private final TriangleBuffers triangleBuffers;
    private final Pipeline pipeline;

    private boolean needsToResize;

    private static int currentFrame = 0;

    public MainRenderer() {
        this.camera = new Camera();
        this.vulkanCtx = new VulkanCtx();
        this.graphicsQueue = new Queue.GraphicsQueue(this.vulkanCtx, 1);
        this.presentQueue = new Queue.PresentQueue(this.vulkanCtx, 1);
        this.commandPool = new CommandPool(this.vulkanCtx, this.graphicsQueue.getQueueFamilyIndex(), true);

        int numOfImages = this.vulkanCtx.getSwapChain().getNumOfImages();
        this.createCommandBuffers(numOfImages);

        this.fences = new Fence(this.vulkanCtx, true);
        this.renderSemaphores = new Semaphore(this.vulkanCtx);
        this.presentSemaphores = new Semaphore(this.vulkanCtx);

        this.triangleBuffers = new TriangleBuffers(this.vulkanCtx);
        ShaderModule[] shaderModules = createShaderModules();
        this.pipeline = this.createPipeline(shaderModules);
        Arrays.asList(shaderModules).forEach(shaderModule -> shaderModule.free(this.vulkanCtx));

        this.depthAttachments = createDepthAttachments(numOfImages);
        this.renderingInfo = setupRenderInfo(numOfImages);

        this.pushConstBuff = MemoryUtil.memAlloc(128);

        this.initScene();
        this.updateObjects(this.vulkanCtx, this.commandPool, this.graphicsQueue);
    }

    private void initScene() {
        SceneManager.addObject(new Bunny(false));
    }

    private Pipeline createPipeline(ShaderModule[] shaderModules) {
        VertexBufferStruct vertexBufferStruct = new VertexBufferStruct();
        Pipeline pipeline = new Pipeline.Builder(shaderModules, vertexBufferStruct.getCreateInfo(), this.vulkanCtx.getSurface().getSurfaceFormat().imageFormat())
                .setDepthFormat(VK10.VK_FORMAT_D16_UNORM)
                .setPushConstRanges(new Pipeline.PushConstRange[]{
                        new Pipeline.PushConstRange(VK10.VK_SHADER_STAGE_VERTEX_BIT, 0, 128)
                })
                .build(this.vulkanCtx);
        vertexBufferStruct.free();
        return pipeline;
    }

    private ShaderModule[] createShaderModules() {
        ShaderCompiler.compiledShaderIfChanged(VERTEX_SHADER_FILE_GLSL, Shaderc.shaderc_glsl_vertex_shader);
        ShaderCompiler.compiledShaderIfChanged(FRAGMENT_SHADER_FILE_GLSL, Shaderc.shaderc_glsl_fragment_shader);

        return new ShaderModule[] {
                new ShaderModule(this.vulkanCtx, VK10.VK_SHADER_STAGE_VERTEX_BIT, VERTEX_SHADER_FILE_SPV),
                new ShaderModule(this.vulkanCtx, VK10.VK_SHADER_STAGE_FRAGMENT_BIT, FRAGMENT_SHADER_FILE_SPV)
        };
    }

    private Attachment[] createDepthAttachments(int numOfImages) {
        Attachment[] result = new Attachment[numOfImages];
        VkExtent2D extent2D = this.vulkanCtx.getSwapChain().getExtent2D();
        for (int i = 0; i < numOfImages; i++) {
            result[i] = new Attachment(this.vulkanCtx, extent2D.width(), extent2D.height(), VK10.VK_FORMAT_D16_UNORM, VK10.VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT);
        }

        return result;
    }

    public VkRenderingInfo[] setupRenderInfo(int numOfImages) {
        VkRenderingInfo[] result = new VkRenderingInfo[numOfImages];

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkRect2D renderArea = VkRect2D.calloc(stack).extent(this.vulkanCtx.getSwapChain().getExtent2D());

            FloatBuffer floatBuffer = stack.callocFloat(4);
            floatBuffer.put(clearColor);
            floatBuffer.flip();

            VkClearColorValue colorValue = VkClearColorValue.calloc(stack).float32(floatBuffer);

            VkClearValue clearValue = VkClearValue.calloc(stack).color(colorValue);
            VkClearValue depthClearValue = VkClearValue.calloc(stack).color(vkClearColorValue -> vkClearColorValue.float32(0, 1.0f));

            for (int i = 0; i < numOfImages; i++) {
                ImageView imageView = this.vulkanCtx.getSwapChain().getImageViews()[i];

                VkRenderingAttachmentInfo.Buffer colorAttachmentBuffer = VkRenderingAttachmentInfo.calloc(1)
                        .sType$Default()
                        .imageView(imageView.getVkImageViewHandle())
                        .imageLayout(KHRSynchronization2.VK_IMAGE_LAYOUT_ATTACHMENT_OPTIMAL_KHR)
                        .loadOp(VK10.VK_ATTACHMENT_LOAD_OP_CLEAR)
                        .storeOp(VK10.VK_ATTACHMENT_STORE_OP_STORE)
                        .clearValue(clearValue);

                VkRenderingAttachmentInfo depthAttachment = VkRenderingAttachmentInfo.calloc()
                        .sType$Default()
                        .imageView(this.depthAttachments[i].getImageView().getVkImageViewHandle())
                        .imageLayout(VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                        .loadOp(VK10.VK_ATTACHMENT_LOAD_OP_CLEAR)
                        .storeOp(VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .clearValue(depthClearValue);

                VkRenderingInfo renderingInfo = VkRenderingInfo.calloc()
                        .sType$Default()
                        .renderArea(renderArea)
                        .layerCount(1)
                        .pColorAttachments(colorAttachmentBuffer)
                        .pDepthAttachment(depthAttachment);

                result[i] = renderingInfo;
            }

            return result;
        }
    }

    public void render() {
        if (this.needsToResize) {
            this.resize();
        }
        this.camera.updateCamera();
        CommandBuffer buffer = commandBuffers.get(currentFrame);

        this.fences.waitForFence(this.vulkanCtx);

        buffer.reset();
        buffer.beginRecordingPrimary();

        int imageIndex = this.vulkanCtx.getSwapChain().getNextImage(this.vulkanCtx.getLogicalDevice(), this.presentSemaphores);
        this.renderScene(buffer.getVkCommandBuffer(), imageIndex);

        buffer.endRecording();

        this.submit(buffer);
        this.needsToResize = this.vulkanCtx.getSwapChain().presentImage(this.presentQueue, this.renderSemaphores, imageIndex);

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

            VulkanUtils.imageBarrier(stack, buffer, this.depthAttachments[imageIndex].getImage().getVkImage(),
                    VK10.VK_IMAGE_LAYOUT_UNDEFINED,
                    VK12.VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL,
                    VK13.VK_PIPELINE_STAGE_2_EARLY_FRAGMENT_TESTS_BIT | VK13.VK_PIPELINE_STAGE_2_LATE_FRAGMENT_TESTS_BIT,
                    VK13.VK_PIPELINE_STAGE_2_EARLY_FRAGMENT_TESTS_BIT | VK13.VK_PIPELINE_STAGE_2_LATE_FRAGMENT_TESTS_BIT,
                    VK13.VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
                    VK13.VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK13.VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
                    VK10.VK_IMAGE_ASPECT_DEPTH_BIT
            );

            VK13.vkCmdBeginRendering(buffer, this.renderingInfo[currentFrame]);

            VK10.vkCmdBindPipeline(buffer, VK10.VK_PIPELINE_BIND_POINT_GRAPHICS, this.pipeline.getVkPipeline());

            this.setPushConstants(buffer);

            VkExtent2D extent = this.vulkanCtx.getSwapChain().getExtent2D();
            int width = extent.width();
            int height = extent.height();

            VkViewport.Buffer viewport = VkViewport.calloc(1, stack)
                    .x(0)
                    .y(height)
                    .height(-height)
                    .width(width)
                    .minDepth(0.0f)
                    .maxDepth(1.0f);
            VK10.vkCmdSetViewport(buffer, 0, viewport);

            VkRect2D.Buffer scissor = VkRect2D.calloc(1, stack)
                    .extent(vkExtent2D -> vkExtent2D.width(width).height(height))
                    .offset(vkOffset2D -> vkOffset2D.x(0).y(0));
            VK10.vkCmdSetScissor(buffer, 0, scissor);

            LongBuffer offsets = stack.mallocLong(1).put(0, 0L);
            LongBuffer vertexBuffer = stack.mallocLong(1);
            vertexBuffer.put(0, this.triangleBuffers.getGpuBuffer());

            VK10.vkCmdBindVertexBuffers(buffer, 0, vertexBuffer, offsets);
            VK10.vkCmdDraw(buffer, this.triangleBuffers.getNumOfTriangles() * 3, 1, 0, 0);

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

    private void setPushConstants(VkCommandBuffer buffer) {
        this.camera.getProjectionMatrix().get(this.pushConstBuff);
        this.camera.getModelViewMatrix().get(64, this.pushConstBuff);
        VK10.vkCmdPushConstants(buffer, this.pipeline.getVkPipelineLayout(), VK10.VK_SHADER_STAGE_VERTEX_BIT, 0, this.pushConstBuff);
    }

    public void updateObjects(VulkanCtx ctx, CommandPool commandPool, Queue queue) {
        CommandBuffer cmdBuffer = new CommandBuffer(ctx, commandPool, true, true);

        cmdBuffer.beginRecordingPrimary();

        this.triangleBuffers.startMapping();
        for (SceneObject sceneObject : SceneManager.getSceneObjects()) {
            this.triangleBuffers.putMesh(sceneObject.getTransformMatrix(), sceneObject.getMesh());
        }
        this.triangleBuffers.stopMapping();

        this.triangleBuffers.sendVerticesToGpu(cmdBuffer);

        cmdBuffer.endRecording();
        cmdBuffer.submitAndWait(ctx, queue);
        cmdBuffer.close(ctx, commandPool);
    }

    private void submit(CommandBuffer buffer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.fences.reset(this.vulkanCtx);
            VkCommandBufferSubmitInfo.Buffer cmdSubmitBuffer = VkCommandBufferSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .commandBuffer(buffer.getVkCommandBuffer());

            VkSemaphoreSubmitInfo.Buffer waitSemaphores = VkSemaphoreSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .stageMask(VK13.VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .semaphore(this.presentSemaphores.getVkSemaphore());

            VkSemaphoreSubmitInfo.Buffer signalSemaphores = VkSemaphoreSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .stageMask(VK13.VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT)
                    .semaphore(this.renderSemaphores.getVkSemaphore());

            graphicsQueue.submit(cmdSubmitBuffer, waitSemaphores, signalSemaphores, this.fences);
        }
    }

    private void createCommandBuffers(int num) {
        for (int i = 0; i < num; i++) {
            commandBuffers.add(new CommandBuffer(this.vulkanCtx, this.commandPool, true, true));
        }
    }

    private void resize() {
        this.needsToResize = false;
        this.vulkanCtx.getLogicalDevice().waitIdle();

        this.vulkanCtx.resize();

        this.renderSemaphores.free(this.vulkanCtx);
        this.presentSemaphores.free(this.vulkanCtx);
        Arrays.asList(this.renderingInfo).forEach(VkRenderingInfo::free);

        this.renderSemaphores = new Semaphore(this.vulkanCtx);
        this.presentSemaphores = new Semaphore(this.vulkanCtx);

        int numOfImages = this.vulkanCtx.getSwapChain().getNumOfImages();
        this.depthAttachments = createDepthAttachments(numOfImages);
        this.renderingInfo = setupRenderInfo(numOfImages);
    }

    public VulkanCtx getVulkanCtx() {
        return vulkanCtx;
    }

    public void close() {
        this.vulkanCtx.getLogicalDevice().waitIdle();
        Arrays.asList(this.renderingInfo).forEach(VkRenderingInfo::free);
        Arrays.asList(this.depthAttachments).forEach(attachment -> attachment.free(this.vulkanCtx));
        MemoryUtil.memFree(this.pushConstBuff);
        this.pipeline.free(this.vulkanCtx);
        this.triangleBuffers.free();
        this.presentSemaphores.free(this.vulkanCtx);
        this.renderSemaphores.free(this.vulkanCtx);
        this.fences.close(this.vulkanCtx);
        this.commandPool.close();
        this.vulkanCtx.free();
    }
}
