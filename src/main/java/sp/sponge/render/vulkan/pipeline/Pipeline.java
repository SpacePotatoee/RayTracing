package sp.sponge.render.vulkan.pipeline;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.pipeline.shaders.ShaderModule;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class Pipeline {
    private final long vkPipeline;
    private final long vkPipelineLayout;

    public Pipeline(VulkanCtx ctx, ShaderModule[] shaderModules, VkPipelineVertexInputStateCreateInfo vertexInputState, int colorFormat) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer longBuffer = stack.mallocLong(1);

            ByteBuffer main = stack.UTF8("main");

            int numOfShaderModules = shaderModules.length;
            VkPipelineShaderStageCreateInfo.Buffer stagesCreateInfo = VkPipelineShaderStageCreateInfo.calloc(numOfShaderModules, stack);

            for (int i = 0; i < numOfShaderModules; i++) {
                ShaderModule shaderModule = shaderModules[i];
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
                    .cullMode(VK10.VK_CULL_MODE_NONE)
                    .frontFace(VK10.VK_FRONT_FACE_CLOCKWISE)
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

            IntBuffer colorFormats = stack.mallocInt(1);
            colorFormats.put(0, colorFormat);
            VkPipelineRenderingCreateInfo renderingCreateInfo = VkPipelineRenderingCreateInfo.calloc(stack)
                    .sType$Default()
                    .colorAttachmentCount(1)
                    .pColorAttachmentFormats(colorFormats);

            //PipelineLayout (binding point for things like uniforms)
            VkPipelineLayoutCreateInfo pipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.calloc(stack)
                    .sType$Default();

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
                    .pVertexInputState(vertexInputState)
                    .pInputAssemblyState(assemblyCreateInfo)
                    .pViewportState(viewportCreateInfo)
                    .pRasterizationState(rasterizationCreateInfo)
                    .pMultisampleState(multisampleCreateInfo)
                    .pDynamicState(dynamicStateCreateInfo)
                    .pColorBlendState(colorBlendStateCreateInfo)
                    .pNext(renderingCreateInfo)
                    .layout(this.vkPipelineLayout);

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
}
