package sp.sponge.render.vulkan.model;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

public class VertexBufferStruct {
    private final int numOfAttributes = 2;

    private final int POSITION_COMPONENTS = 4;
    private final int NORMAL_COMPONENTS = 4;

    private final int POSITION_STRIDE = POSITION_COMPONENTS * Float.BYTES;
    private final int NORMAL_STRIDE = NORMAL_COMPONENTS * Short.BYTES;
    private final int STRIDE = POSITION_STRIDE + NORMAL_STRIDE;

    private final VkVertexInputAttributeDescription.Buffer vertexAttributesDescriptions;
    private final VkVertexInputBindingDescription.Buffer vertexDescription;
    private final VkPipelineVertexInputStateCreateInfo createInfo;

    public VertexBufferStruct() {
        vertexAttributesDescriptions = VkVertexInputAttributeDescription.calloc(numOfAttributes);
        vertexDescription = VkVertexInputBindingDescription.calloc(1);
        createInfo = VkPipelineVertexInputStateCreateInfo.calloc();

        int offset = 0;
        //Position
        vertexAttributesDescriptions.get(0)
                .binding(0)
                .location(0)
                .format(VK10.VK_FORMAT_R32G32B32_SFLOAT)
                .offset(offset);
        offset += POSITION_STRIDE;


        //NORMAL
        vertexAttributesDescriptions.get(1)
                .binding(0)
                .location(1)
                .format(VK10.VK_FORMAT_R16G16B16_SFLOAT)
                .offset(offset);


        vertexDescription.get(0)
                .binding(0)
                .stride(STRIDE)
                .inputRate(VK10.VK_VERTEX_INPUT_RATE_VERTEX);

        createInfo
                .sType$Default()
                .pVertexBindingDescriptions(vertexDescription)
                .pVertexAttributeDescriptions(vertexAttributesDescriptions);
    }

    public void free() {
        vertexAttributesDescriptions.free();
        vertexDescription.free();
    }

    public VkPipelineVertexInputStateCreateInfo getCreateInfo() {
        return createInfo;
    }
}
