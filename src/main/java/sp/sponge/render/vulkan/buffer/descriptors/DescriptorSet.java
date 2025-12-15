package sp.sponge.render.vulkan.buffer.descriptors;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.device.LogicalDevice;
import sp.sponge.render.vulkan.buffer.VkBuffer;

import java.nio.LongBuffer;

public class DescriptorSet {
    private final long vkDescriptorSet;

    public DescriptorSet(LogicalDevice device, DescriptorPool pool, DescriptorSetLayout layout) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer longBuffer = stack.mallocLong(1);
            longBuffer.put(0, layout.getVkDescriptorSetLayout());
            VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.calloc(stack)
                    .sType$Default()
                    .descriptorPool(pool.getVkDescriptorPool())
                    .pSetLayouts(longBuffer);

            VulkanUtils.check(
                    VK13.vkAllocateDescriptorSets(device.getVkDevice(), allocateInfo, longBuffer),
                    "Failed to allocate descriptor sets"
            );
            this.vkDescriptorSet = longBuffer.get(0);
        }
    }

    public void setBuffer(LogicalDevice device, VkBuffer buffer, long range, int binding, int type) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc(1, stack)
                    .buffer(buffer.getBufferPtr())
                    .offset(0)
                    .range(range);

            VkWriteDescriptorSet.Buffer descriptorSetBuffer = VkWriteDescriptorSet.calloc(1, stack);

            descriptorSetBuffer.get(0)
                    .sType$Default()
                    .dstSet(this.vkDescriptorSet)
                    .dstBinding(binding)
                    .descriptorType(type)
                    .descriptorCount(1)
                    .pBufferInfo(bufferInfo);

            VK10.vkUpdateDescriptorSets(device.getVkDevice(), descriptorSetBuffer, null);
        }
    }

    public long getVkDescriptorSet() {
        return vkDescriptorSet;
    }
}
