package sp.sponge.render.vulkan.raytracing;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.buffer.descriptors.DescriptorSetLayout;
import sp.sponge.render.vulkan.pipeline.shaders.ShaderModule;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class RayTracingPipeline {
    private final long vkRtPipeline;
    private final long vkPipelineLayout;

    public RayTracingPipeline(VulkanCtx ctx, Builder buildInfo) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer longBuffer = stack.mallocLong(1);

            ByteBuffer main = stack.UTF8("main");

            int numOfStages = buildInfo.shaderModules.length;
            VkPipelineShaderStageCreateInfo.Buffer shaderStageCreateInfo = VkPipelineShaderStageCreateInfo.calloc(numOfStages, stack);

            //Shader stages
            for (int i = 0; i < numOfStages; i++) {
                ShaderModule module = buildInfo.shaderModules[i];
                shaderStageCreateInfo.get(i)
                        .sType$Default()
                        .stage(module.getShaderStage())
                        .module(module.getShaderHandle())
                        .pName(main);
            }


            //Pipeline Layout
            DescriptorSetLayout[] descriptorSetLayouts = buildInfo.descriptorSetLayouts;
            int numOfLayouts = descriptorSetLayouts != null ? descriptorSetLayouts.length : 0;
            LongBuffer layoutsBuffer = stack.mallocLong(numOfLayouts);
            for (int i = 0; i < numOfLayouts; i++) {
                layoutsBuffer.put(i, descriptorSetLayouts[i].getVkDescriptorSetLayout());
            }

            VkPipelineLayoutCreateInfo layoutCreateInfo = VkPipelineLayoutCreateInfo.calloc(stack)
                    .sType$Default()
                    .pSetLayouts(layoutsBuffer);

            VulkanUtils.check(
                    VK10.vkCreatePipelineLayout(ctx.getLogicalDevice().getVkDevice(), layoutCreateInfo, null, longBuffer),
                    "Failed to create pipeline layout"
            );
            this.vkPipelineLayout = longBuffer.get(0);


            //Create the pipeline
            VkRayTracingPipelineCreateInfoKHR.Buffer rtPipelineCreateInfo = VkRayTracingPipelineCreateInfoKHR.calloc(1, stack)
                    .sType$Default()
                    .pStages(shaderStageCreateInfo)
                    .maxPipelineRayRecursionDepth(1)
                    .pGroups(buildInfo.shaderGroupBuffer)
                    .layout(this.vkPipelineLayout);

            VulkanUtils.check(
                    KHRRayTracingPipeline.vkCreateRayTracingPipelinesKHR(
                            ctx.getLogicalDevice().getVkDevice(),
                            VK10.VK_NULL_HANDLE,
                            ctx.getPipelineCache().getVkPipelineCache(),
                            rtPipelineCreateInfo,
                            null,
                            longBuffer
                    ),
                    "Failed to create ray tracing pipeline"
            );
            this.vkRtPipeline = longBuffer.get(0);

        }
    }

    public long getVkPipeline() {
        return vkRtPipeline;
    }

    public long getVkPipelineLayout() {
        return vkPipelineLayout;
    }

    public void free(VulkanCtx ctx) {
        VkDevice device = ctx.getLogicalDevice().getVkDevice();
        VK10.vkDestroyPipelineLayout(device, this.vkPipelineLayout, null);
        VK10.vkDestroyPipeline(device, this.vkRtPipeline, null);
    }

    public static class Builder {
        private final ShaderModule[] shaderModules;
        private final VkRayTracingShaderGroupCreateInfoKHR.Buffer shaderGroupBuffer;
        private DescriptorSetLayout[] descriptorSetLayouts;

        public Builder(ShaderModule[] shaderModules, VkRayTracingShaderGroupCreateInfoKHR.Buffer shaderGroupBuffer) {
            this.shaderModules = shaderModules;
            this.shaderGroupBuffer = shaderGroupBuffer;
        }

        public Builder setDescriptorSetLayouts(DescriptorSetLayout[] descriptorSetLayouts) {
            this.descriptorSetLayouts = descriptorSetLayouts;
            return this;
        }

        public RayTracingPipeline build(VulkanCtx ctx) {
            return new RayTracingPipeline(ctx, this);
        }

    }
}
