package sp.sponge.render.vulkan.pipeline;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.buffer.descriptors.DescriptorSetLayout;
import sp.sponge.render.vulkan.pipeline.shaders.ShaderModule;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class Pipeline {
    private final long vkPipeline;
    private final long vkPipelineLayout;

    public Pipeline(VulkanCtx ctx, Builder builder) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer longBuffer = stack.mallocLong(1);

            ByteBuffer main = stack.UTF8("main");

            int numOfShaderModules = builder.shaderModules.length;
            VkPipelineShaderStageCreateInfo.Buffer stagesCreateInfo = VkPipelineShaderStageCreateInfo.calloc(numOfShaderModules, stack);

            for (int i = 0; i < numOfShaderModules; i++) {
                ShaderModule shaderModule = builder.shaderModules[i];
                stagesCreateInfo.get(i)
                        .sType$Default()
                        .stage(shaderModule.getShaderStage())
                        .module(shaderModule.getShaderHandle())
                        .pName(main);
            }

            //Input Assembly -> The type of primitives to be used
            VkPipelineInputAssemblyStateCreateInfo assemblyCreateInfo = VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
                    .sType$Default()
                    .topology(VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);

            //How many viewports and scissors we need
            VkPipelineViewportStateCreateInfo viewportCreateInfo = VkPipelineViewportStateCreateInfo.calloc(stack)
                    .sType$Default()
                    .viewportCount(1)
                    .scissorCount(1);

            //Configure rasterization
            VkPipelineRasterizationStateCreateInfo rasterizationCreateInfo = VkPipelineRasterizationStateCreateInfo.calloc(stack)
                    .sType$Default()
                    .polygonMode(VK10.VK_POLYGON_MODE_FILL)
                    .cullMode(VK10.VK_CULL_MODE_BACK_BIT)
                    .frontFace(VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE)
                    .lineWidth(1.0f);

            //Multisampling (Off is 1 sample)
            VkPipelineMultisampleStateCreateInfo multisampleCreateInfo = VkPipelineMultisampleStateCreateInfo.calloc(stack)
                    .sType$Default()
                    .rasterizationSamples(VK10.VK_SAMPLE_COUNT_1_BIT);

            //Dynamic states (anything in the pipeline that may change like the viewport width and height)
            VkPipelineDynamicStateCreateInfo dynamicStateCreateInfo = VkPipelineDynamicStateCreateInfo.calloc(stack)
                    .sType$Default()
                    .pDynamicStates(stack.ints(
                            VK10.VK_DYNAMIC_STATE_VIEWPORT,
                            VK10.VK_DYNAMIC_STATE_SCISSOR
                    ));

            //Color Blending
            VkPipelineColorBlendAttachmentState.Buffer colorBlendAttachmentState = VkPipelineColorBlendAttachmentState.calloc(1, stack)
                    .colorWriteMask(VK10.VK_COLOR_COMPONENT_R_BIT | VK10.VK_COLOR_COMPONENT_G_BIT | VK10.VK_COLOR_COMPONENT_B_BIT | VK10.VK_COLOR_COMPONENT_A_BIT)
                    .blendEnable(false);

            VkPipelineColorBlendStateCreateInfo colorBlendStateCreateInfo = VkPipelineColorBlendStateCreateInfo.calloc(stack)
                    .sType$Default()
                    .pAttachments(colorBlendAttachmentState);

            //Depth
            VkPipelineDepthStencilStateCreateInfo depthCreateInfo = null;
            if (builder.depthFormat != VK10.VK_FORMAT_UNDEFINED) {
                depthCreateInfo = VkPipelineDepthStencilStateCreateInfo.calloc(stack)
                        .sType$Default()
                        .depthTestEnable(true)
                        .depthWriteEnable(true)
                        .depthCompareOp(VK10.VK_COMPARE_OP_LESS_OR_EQUAL)
                        .depthBoundsTestEnable(false)
                        .stencilTestEnable(false);
            }

            IntBuffer colorFormats = stack.mallocInt(1);
            colorFormats.put(0, builder.colorFormat);
            VkPipelineRenderingCreateInfo renderingCreateInfo = VkPipelineRenderingCreateInfo.calloc(stack)
                    .sType$Default()
                    .colorAttachmentCount(1)
                    .pColorAttachmentFormats(colorFormats);

            if (depthCreateInfo != null) {
                renderingCreateInfo.depthAttachmentFormat(builder.depthFormat);
            }

            //Pipeline Layout (binding point for things like uniforms)
            VkPushConstantRange.Buffer pushConstantRangeBuffer = null;
            PushConstRange[] pushConstRanges = builder.pushConstRanges;
            if (pushConstRanges != null && pushConstRanges.length > 0) {
                int numOfPCRs = pushConstRanges.length;
                pushConstantRangeBuffer = VkPushConstantRange.calloc(numOfPCRs, stack);
                for (int i = 0; i < numOfPCRs; i++) {
                    PushConstRange pushConstRange = pushConstRanges[i];
                    pushConstantRangeBuffer.get(i)
                            .stageFlags(pushConstRange.stage)
                            .offset(pushConstRange.offset)
                            .size(pushConstRange.size);
                }
            }

            //Descriptor Set Layouts
            DescriptorSetLayout[] descriptorSetLayouts = builder.descriptorSetLayouts;
            int numOfLayouts = descriptorSetLayouts != null ? descriptorSetLayouts.length : 0;
            LongBuffer layoutsBuffer = stack.mallocLong(numOfLayouts);
            for (int i = 0; i < numOfLayouts; i++) {
                layoutsBuffer.put(i, descriptorSetLayouts[i].getVkDescriptorSetLayout());
            }

            VkPipelineLayoutCreateInfo pipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.calloc(stack)
                    .sType$Default()
                    .pSetLayouts(layoutsBuffer)
                    .pPushConstantRanges(pushConstantRangeBuffer);

            VulkanUtils.check(
                    VK10.vkCreatePipelineLayout(ctx.getLogicalDevice().getVkDevice(), pipelineLayoutCreateInfo, null, longBuffer),
                    "Failed to create pipeline layout"
            );
            this.vkPipelineLayout = longBuffer.get(0);


            //Create the pipeline
            VkGraphicsPipelineCreateInfo.Buffer createInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack)
                    .sType$Default()
                    .renderPass(VK10.VK_NULL_HANDLE)
                    .pStages(stagesCreateInfo)
                    .pVertexInputState(builder.vertexInputState)
                    .pInputAssemblyState(assemblyCreateInfo)
                    .pViewportState(viewportCreateInfo)
                    .pRasterizationState(rasterizationCreateInfo)
                    .pMultisampleState(multisampleCreateInfo)
                    .pDynamicState(dynamicStateCreateInfo)
                    .pColorBlendState(colorBlendStateCreateInfo)
                    .pNext(renderingCreateInfo)
                    .layout(this.vkPipelineLayout);

            if (depthCreateInfo != null) {
                createInfo.pDepthStencilState(depthCreateInfo);
            }

            VulkanUtils.check(
                    VK10.vkCreateGraphicsPipelines(ctx.getLogicalDevice().getVkDevice(), ctx.getPipelineCache().getVkPipelineCache(), createInfo, null, longBuffer),
                    "Failed to create pipeline"
            );

            this.vkPipeline = longBuffer.get(0);
        }
    }

    public long getVkPipeline() {
        return vkPipeline;
    }

    public long getVkPipelineLayout() {
        return vkPipelineLayout;
    }

    public void free(VulkanCtx ctx) {
        VkDevice device = ctx.getLogicalDevice().getVkDevice();
        VK10.vkDestroyPipelineLayout(device, this.vkPipelineLayout, null);
        VK10.vkDestroyPipeline(device, this.vkPipeline, null);
    }

    public static class Builder {
        private final ShaderModule[] shaderModules;
        private final VkPipelineVertexInputStateCreateInfo vertexInputState;
        private final int colorFormat;
        private int depthFormat;
        private PushConstRange[] pushConstRanges;
        private DescriptorSetLayout[] descriptorSetLayouts;

        public Builder(ShaderModule[] shaderModules, VkPipelineVertexInputStateCreateInfo vertexInputState, int colorFormat) {
            this.shaderModules = shaderModules;
            this.vertexInputState = vertexInputState;
            this.colorFormat = colorFormat;
            this.depthFormat = VK10.VK_FORMAT_UNDEFINED;
        }

        public Builder setDepthFormat(int depthFormat) {
            this.depthFormat = depthFormat;
            return this;
        }

        public Builder setPushConstRanges(PushConstRange[] pushConstRanges) {
            this.pushConstRanges = pushConstRanges;
            return this;
        }

        public Builder setDescriptorSetLayouts(DescriptorSetLayout[] descriptorSetLayouts) {
            this.descriptorSetLayouts = descriptorSetLayouts;
            return this;
        }

        public Pipeline build(VulkanCtx ctx) {
            return new Pipeline(ctx, this);
        }
    }

    public record PushConstRange(int stage, int offset, int size){}
}
