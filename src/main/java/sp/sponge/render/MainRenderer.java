package sp.sponge.render;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.DescriptorSets;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.buffer.TriangleBuffers;
import sp.sponge.render.vulkan.buffer.descriptors.DescriptorAllocator;
import sp.sponge.render.vulkan.buffer.descriptors.DescriptorSet;
import sp.sponge.render.vulkan.buffer.descriptors.DescriptorSetLayout;
import sp.sponge.render.vulkan.device.command.CommandBuffer;
import sp.sponge.render.vulkan.device.command.CommandPool;
import sp.sponge.render.vulkan.device.Queue;
import sp.sponge.render.vulkan.buffer.VkBuffer;
import sp.sponge.render.vulkan.pipeline.shaders.ShaderCompiler;
import sp.sponge.render.vulkan.pipeline.shaders.ShaderModule;
import sp.sponge.render.vulkan.raytracing.RayTracingPipeline;
import sp.sponge.render.vulkan.raytracing.accelstruct.BLAS;
import sp.sponge.render.vulkan.raytracing.accelstruct.TLAS;
import sp.sponge.render.vulkan.raytracing.shader.ShaderBindingTable;
import sp.sponge.render.vulkan.raytracing.shader.ShaderGroup;
import sp.sponge.render.vulkan.image.ImageView;
import sp.sponge.render.vulkan.sync.Fence;
import sp.sponge.render.vulkan.sync.Semaphore;
import sp.sponge.scene.SceneManager;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.objects.custom.obj.Bunny;
import sp.sponge.scene.objects.custom.obj.Cube;
import sp.sponge.scene.objects.custom.obj.Dragon;
import sp.sponge.scene.objects.custom.obj.Sponza;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.vulkan.KHRRayTracingPipeline.*;

public class MainRenderer {
    private static final String RGEN_SHADER_FILE_GLSL = "shaders/raytracing/rgen.glsl";
    private static final String RGEN_SHADER_FILE_SPV = RGEN_SHADER_FILE_GLSL + ".spv";
    private static final String MISS_SHADER_FILE_GLSL = "shaders/raytracing/miss.glsl";
    private static final String MISS_SHADER_FILE_SPV = MISS_SHADER_FILE_GLSL + ".spv";
    private static final String CHIT_SHADER_FILE_GLSL = "shaders/raytracing/chit.glsl";
    private static final String CHIT_SHADER_FILE_SPV = CHIT_SHADER_FILE_GLSL + ".spv";
    private static final String AHIT_SHADER_FILE_GLSL = "shaders/raytracing/ahit.glsl";
    private static final String AHIT_SHADER_FILE_SPV = AHIT_SHADER_FILE_GLSL + ".spv";

    private static final String VERTEX_DESC_SET = "vert_desc_set";
    private static final String TLAS_DESC_SET = "tlas_desc_set";

    private static final String[] IMG_DESC_SET = new String[] {
            "img_desc_set1",
            "img_desc_set2",
            "img_desc_set3"
    };

    private final Camera camera;
    private final VulkanCtx vulkanCtx;
    private final Queue.GraphicsQueue graphicsQueue;
    private final Queue.PresentQueue presentQueue;
    private final CommandPool commandPool;
    private final List<CommandBuffer> commandBuffers = new ArrayList<>();

    private final Fence fences;
    private Semaphore renderSemaphores;
    private Semaphore presentSemaphores;

    private final VkBuffer vertexUniformBuffer;
    private final DescriptorSets descriptorSets;
    private final TriangleBuffers triangleBuffers;
    private final RayTracingPipeline pipeline;
    private final ShaderBindingTable shaderBindingTable;

    private BLAS blas;
    private TLAS tlas;

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

        this.descriptorSets = new DescriptorSets();
        this.initScene();

        DescriptorSets.Group group = this.descriptorSets.addDescriptorGroup(
                this.vulkanCtx,
                VERTEX_DESC_SET,
                VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
                0,
                1,
                VK_SHADER_STAGE_RAYGEN_BIT_KHR | VK_SHADER_STAGE_ANY_HIT_BIT_KHR
        );

        DescriptorSet vertUniformDescSet = group.descriptorSet();
        DescriptorSetLayout vertexUnifDescLayout = group.layout();

        vertexUniformBuffer = VulkanUtils.createCpuBuffer(this.vulkanCtx, 280, VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT);
        vertUniformDescSet.setBuffer(
                this.vulkanCtx,
                vertexUniformBuffer,
                vertexUniformBuffer.getRequestedSize(),
                vertexUnifDescLayout.getLayoutInfo().binding(),
                vertexUnifDescLayout.getLayoutInfo().descriptorType()
        );
        this.setVertexUniformsUniforms(vertexUniformBuffer);

        DescriptorSetLayout imageLayout = this.createImageDescSet(this.vulkanCtx.getSwapChain().getNumOfImages());

        ShaderModule[] shaderModules = createRTShaderModules();
        VkRayTracingShaderGroupCreateInfoKHR.Buffer groups = createShaderGroups();
        this.pipeline = this.createRTPipeline(shaderModules, groups, new DescriptorSetLayout[]{vertexUnifDescLayout, this.descriptorSets.getLayout(TLAS_DESC_SET), imageLayout});
        this.shaderBindingTable = createShaderBindingTable(groups.remaining());

        Arrays.asList(shaderModules).forEach(shaderModule -> shaderModule.free(this.vulkanCtx));
        groups.free();
    }

    private void initScene() {
        this.camera.updateCamera();
        SceneManager.addObject(new Dragon(false));
        this.updateObjects(this.vulkanCtx, this.commandPool, this.graphicsQueue);

        this.blas = new BLAS(this.vulkanCtx, this.triangleBuffers, this.commandPool, this.graphicsQueue);
        this.tlas = new TLAS(this.vulkanCtx, new BLAS[]{this.blas}, this.commandPool, this.graphicsQueue);

        DescriptorSets.Group group = this.descriptorSets.addDescriptorGroup(
                this.vulkanCtx,
                TLAS_DESC_SET,
                KHRAccelerationStructure.VK_DESCRIPTOR_TYPE_ACCELERATION_STRUCTURE_KHR,
                0,
                1,
                VK_SHADER_STAGE_RAYGEN_BIT_KHR
        );

        DescriptorSetLayout.LayoutInfo layoutInfo = group.layout().getLayoutInfo();
        group.descriptorSet().setTLAS(this.vulkanCtx, layoutInfo.binding(), layoutInfo.descriptorType(), this.tlas);
    }

    private DescriptorSetLayout createImageDescSet(int numOfImages) {
        DescriptorSetLayout layout = new DescriptorSetLayout(
                this.vulkanCtx,
                new DescriptorSetLayout.LayoutInfo(
                        VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
                        0,
                        1,
                        VK_SHADER_STAGE_RAYGEN_BIT_KHR
                )
        );

        for (int i = 0; i < numOfImages; i++) {
            DescriptorSets.Group group = this.descriptorSets.addDescriptorGroup(this.vulkanCtx, IMG_DESC_SET[i], layout);
            group.descriptorSet().setImage(this.vulkanCtx, this.vulkanCtx.getSwapChain().getImageViews()[i], 0);
        }

        return layout;
    }

    private RayTracingPipeline createRTPipeline(ShaderModule[] shaderModules, VkRayTracingShaderGroupCreateInfoKHR.Buffer groups, DescriptorSetLayout[] layouts) {
        return new RayTracingPipeline.Builder(shaderModules, groups)
                .setDescriptorSetLayouts(layouts)
                .build(this.vulkanCtx);
    }

    private ShaderModule[] createRTShaderModules() {
        ShaderCompiler.compiledShaderIfChanged(RGEN_SHADER_FILE_GLSL, Shaderc.shaderc_glsl_raygen_shader);
        ShaderCompiler.compiledShaderIfChanged(MISS_SHADER_FILE_GLSL, Shaderc.shaderc_glsl_miss_shader);
        ShaderCompiler.compiledShaderIfChanged(CHIT_SHADER_FILE_GLSL, Shaderc.shaderc_glsl_closesthit_shader);
        ShaderCompiler.compiledShaderIfChanged(AHIT_SHADER_FILE_GLSL, Shaderc.shaderc_glsl_anyhit_shader);

        return new ShaderModule[] {
                new ShaderModule(this.vulkanCtx, VK_SHADER_STAGE_RAYGEN_BIT_KHR, RGEN_SHADER_FILE_SPV),
                new ShaderModule(this.vulkanCtx, VK_SHADER_STAGE_MISS_BIT_KHR, MISS_SHADER_FILE_SPV),
                new ShaderModule(this.vulkanCtx, VK_SHADER_STAGE_CLOSEST_HIT_BIT_KHR, CHIT_SHADER_FILE_SPV),
                new ShaderModule(this.vulkanCtx, VK_SHADER_STAGE_ANY_HIT_BIT_KHR, AHIT_SHADER_FILE_SPV)
        };
    }

    private VkRayTracingShaderGroupCreateInfoKHR.Buffer createShaderGroups() {
        VkRayTracingShaderGroupCreateInfoKHR.Buffer result = VkRayTracingShaderGroupCreateInfoKHR.calloc(3);

        //Should be the same order that they were put into the shader modules
        //Ray Gen
        result.get(0)
                .sType$Default()
                .type(VK_RAY_TRACING_SHADER_GROUP_TYPE_GENERAL_KHR)
                .generalShader(0)
                .closestHitShader(VK_SHADER_UNUSED_KHR)
                .anyHitShader(VK_SHADER_UNUSED_KHR)
                .intersectionShader(VK_SHADER_UNUSED_KHR);

        //Miss
        result.get(1)
                .sType$Default()
                .type(VK_RAY_TRACING_SHADER_GROUP_TYPE_GENERAL_KHR)
                .generalShader(1)
                .closestHitShader(VK_SHADER_UNUSED_KHR)
                .anyHitShader(VK_SHADER_UNUSED_KHR)
                .intersectionShader(VK_SHADER_UNUSED_KHR);

        //Hit groups
        result.get(2)
                .sType$Default()
                .type(VK_RAY_TRACING_SHADER_GROUP_TYPE_TRIANGLES_HIT_GROUP_KHR)
                .generalShader(VK_SHADER_UNUSED_KHR)
                .closestHitShader(2)
                .anyHitShader(3)
                .intersectionShader(VK_SHADER_UNUSED_KHR);

        return result;
    }

    private ShaderBindingTable createShaderBindingTable(int numOfGroups) {
        ShaderBindingTable result;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPhysicalDeviceRayTracingPipelinePropertiesKHR properties = this.vulkanCtx.getPhysicalDevice().getRayTracingProperties();
            int groupSize = properties.shaderGroupHandleSize();
            int alignment = properties.shaderGroupHandleAlignment();
            int alignedHandle = VulkanUtils.alignSize(groupSize, alignment);
            int size = numOfGroups * alignedHandle;

            ByteBuffer byteBuffer = stack.calloc(size);

            KHRRayTracingPipeline.vkGetRayTracingShaderGroupHandlesKHR(
                    this.vulkanCtx.getLogicalDevice().getVkDevice(),
                    this.pipeline.getVkPipeline(),
                    0,
                    numOfGroups,
                    byteBuffer
            );

            result = new ShaderBindingTable(
                    new ShaderGroup(this.vulkanCtx),
                    new ShaderGroup(this.vulkanCtx),
                    new ShaderGroup(this.vulkanCtx)
            );

            int offset = 0;
            VulkanUtils.copyByteBufferToVkBuffer(this.vulkanCtx, byteBuffer, offset, result.rayGen().getBuffer(), 0, groupSize);
            offset += alignedHandle;
            VulkanUtils.copyByteBufferToVkBuffer(this.vulkanCtx, byteBuffer, offset, result.miss().getBuffer(), 0, groupSize);
            offset += alignedHandle;
            VulkanUtils.copyByteBufferToVkBuffer(this.vulkanCtx, byteBuffer, offset, result.hit().getBuffer(), 0, groupSize);
        }
        return result;
    }

    public void render() {
        if (this.needsToResize) {
            this.resize();
        }
        this.camera.updateCamera();
        this.setVertexUniformsUniforms(vertexUniformBuffer);
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
            VkExtent2D extent2D = this.vulkanCtx.getSwapChain().getExtent2D();

            VulkanUtils.imageBarrier(stack, buffer, swapChainImage,
                    VK10.VK_IMAGE_LAYOUT_UNDEFINED,
                    VK10.VK_IMAGE_LAYOUT_GENERAL,
                    VK13.VK_PIPELINE_STAGE_2_ALL_COMMANDS_BIT,
                    VK_PIPELINE_STAGE_RAY_TRACING_SHADER_BIT_KHR,
                    VK13.VK_ACCESS_2_NONE,
                    VK13.VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT,
                    VK10.VK_IMAGE_ASPECT_COLOR_BIT
            );

            VK10.vkCmdBindPipeline(buffer, VK_PIPELINE_BIND_POINT_RAY_TRACING_KHR, this.pipeline.getVkPipeline());

            DescriptorAllocator allocator = this.vulkanCtx.getDescriptorAllocator();
            LongBuffer descriptorSets = stack.mallocLong(3)
                    .put(0, allocator.getDescriptorSet(VERTEX_DESC_SET).getVkDescriptorSet())
                    .put(1, allocator.getDescriptorSet(TLAS_DESC_SET).getVkDescriptorSet())
                    .put(2, allocator.getDescriptorSet(IMG_DESC_SET[imageIndex]).getVkDescriptorSet());
            VK10.vkCmdBindDescriptorSets(
                    buffer,
                    VK_PIPELINE_BIND_POINT_RAY_TRACING_KHR,
                    this.pipeline.getVkPipelineLayout(),
                    0,
                    descriptorSets,
                    null
            );

            vkCmdTraceRaysKHR(
                    buffer,
                    shaderBindingTable.rayGen().getDeviceAddressRegion(),
                    shaderBindingTable.miss().getDeviceAddressRegion(),
                    shaderBindingTable.hit().getDeviceAddressRegion(),
                    VkStridedDeviceAddressRegionKHR.calloc(stack),
                    extent2D.width(),
                    extent2D.height(),
                    1
            );

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

    private void setVertexUniformsUniforms(VkBuffer vkBuffer) {
        ByteBuffer buffer = vkBuffer.map(this.vulkanCtx);

        this.camera.getProjectionMatrix().get(buffer);
        this.camera.getModelViewMatrix().get(64, buffer);

        Vector3f pos = this.camera.getPosition();
        buffer.putFloat(128, pos.x);
        buffer.putFloat(132, pos.y);
        buffer.putFloat(136, pos.z);
        buffer.putFloat(140, 0);

        this.camera.getInvProjectionMatrix().get(144, buffer);
        this.camera.getInvModelViewMatrix().get(208, buffer);

        buffer.putLong(272, this.triangleBuffers.getGpuAddress(this.vulkanCtx));
        //280

        vkBuffer.unmap(this.vulkanCtx);
    }

    private void resize() {
        this.needsToResize = false;
        this.vulkanCtx.getLogicalDevice().waitIdle();

        this.vulkanCtx.resize();

        this.renderSemaphores.free(this.vulkanCtx);
        this.presentSemaphores.free(this.vulkanCtx);

        this.renderSemaphores = new Semaphore(this.vulkanCtx);
        this.presentSemaphores = new Semaphore(this.vulkanCtx);

        int numOfImages = this.vulkanCtx.getSwapChain().getNumOfImages();
    }

    public VulkanCtx getVulkanCtx() {
        return vulkanCtx;
    }

    public void close() {
        this.shaderBindingTable.free(this.vulkanCtx);
        this.vulkanCtx.getLogicalDevice().waitIdle();
        this.pipeline.free(this.vulkanCtx);
        this.triangleBuffers.free();
        this.presentSemaphores.free(this.vulkanCtx);
        this.renderSemaphores.free(this.vulkanCtx);
        this.fences.close(this.vulkanCtx);
        this.commandPool.close();
        this.vulkanCtx.free();
    }
}
