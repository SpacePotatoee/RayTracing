package sp.sponge.render.vulkan.buffer.descriptors;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.render.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;
import java.util.List;

public class DescriptorPool {
    private final long vkDescriptorPool;
    private final List<DescriptorTypeCount> descriptorTypeCounts;

    public DescriptorPool(LogicalDevice device, List<DescriptorTypeCount> descriptorTypeCounts) {
        this.descriptorTypeCounts = descriptorTypeCounts;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int maxSets = 0;
            int numOfTypes = descriptorTypeCounts.size();
            VkDescriptorPoolSize.Buffer poolSizes = VkDescriptorPoolSize.calloc(numOfTypes, stack);
            for (int i = 0; i < numOfTypes; i++) {
                DescriptorTypeCount descriptorTypeCount = descriptorTypeCounts.get(i);
                maxSets += descriptorTypeCount.count;
                poolSizes.get(i)
                        .type(descriptorTypeCount.descriptorType)
                        .descriptorCount(descriptorTypeCount.count);
            }


            VkDescriptorPoolCreateInfo createInfo = VkDescriptorPoolCreateInfo.calloc(stack)
                    .sType$Default()
                    .flags(VK13.VK_DESCRIPTOR_POOL_CREATE_FREE_DESCRIPTOR_SET_BIT)
                    .pPoolSizes(poolSizes)
                    .maxSets(maxSets);

            LongBuffer longBuffer = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreateDescriptorPool(device.getVkDevice(), createInfo, null, longBuffer),
                    "Failed to create descriptor pool"
            );
            this.vkDescriptorPool = longBuffer.get(0);
        }
    }

    public void freeDescriptorSet(LogicalDevice device, long vkDescriptorSet) {
        VulkanUtils.check(
                VK10.vkFreeDescriptorSets(device.getVkDevice(), this.vkDescriptorPool, vkDescriptorSet),
                "Failed to free descriptor set"
        );
    }

    public long getVkDescriptorPool() {
        return vkDescriptorPool;
    }

    public List<DescriptorTypeCount> getDescriptorTypeCounts() {
        return descriptorTypeCounts;
    }

    public void free(LogicalDevice device) {
        VK10.vkDestroyDescriptorPool(device.getVkDevice(), this.vkDescriptorPool, null);
    }

    public record DescriptorTypeCount(int descriptorType, int count) {}
}
