package sp.sponge.render.vulkan.buffer.descriptors;

import org.lwjgl.vulkan.KHRAccelerationStructure;
import org.lwjgl.vulkan.VK10;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.device.LogicalDevice;
import sp.sponge.render.vulkan.device.PhysicalDevice;

import java.util.*;

public class DescriptorAllocator {
    private static final List<Integer> desiredDescriptorTypes = List.of(
            VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
            VK10.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER,
            VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
            KHRAccelerationStructure.VK_DESCRIPTOR_TYPE_ACCELERATION_STRUCTURE_KHR
    );

    private final HashMap<Integer, Integer> limitMap = new HashMap<>();
    private final HashMap<Integer, Integer> currentAmount = new HashMap<>();
    private final HashMap<String, DescriptorSet> descriptorSets = new HashMap<>();
    private final DescriptorPool descriptorPool;

    public DescriptorAllocator(PhysicalDevice physicalDevice, LogicalDevice logicalDevice) {
        this.createLimits(physicalDevice);
        this.initializeAmounts();

        List<DescriptorPool.DescriptorTypeCount> descriptorTypeCounts = new ArrayList<>();
        limitMap.forEach((type, limit) -> descriptorTypeCounts.add(new DescriptorPool.DescriptorTypeCount(type, limit)));
        this.descriptorPool = new DescriptorPool(logicalDevice, descriptorTypeCounts);
    }

    private void initializeAmounts() {
        for (Integer i : desiredDescriptorTypes) {
            this.currentAmount.put(i, 0);
        }
    }

    private void createLimits(PhysicalDevice physicalDevice) {
        for (Integer i : desiredDescriptorTypes) {
            this.limitMap.put(i, 72);
        }
    }

    public DescriptorSet createDescriptorSet(VulkanCtx ctx, String id, DescriptorSetLayout layout) {
        for (DescriptorSetLayout.LayoutInfo layoutInfo : layout.getLayoutInfos()) {
            int type = layoutInfo.descriptorType();
            if (!currentAmount.containsKey(type)) throw new RuntimeException("Descriptor type of " + type + " not recognized");
            int current = currentAmount.get(type);
            int limit = limitMap.get(type);
            if (current + 1 > limit) {
                throw new RuntimeException("Cannot create " + (current + 1) + " descriptor sets of type " + type);
            }

            currentAmount.put(type, current + 1);
        }

        DescriptorSet descriptorSet = new DescriptorSet(ctx.getLogicalDevice(), this.descriptorPool, layout);
        descriptorSets.put(id, descriptorSet);
        return descriptorSet;
    }

    public DescriptorSet getDescriptorSet(String id) {
        return descriptorSets.get(id);
    }

    public void close(LogicalDevice logicalDevice) {
        this.descriptorPool.free(logicalDevice);
    }

}
