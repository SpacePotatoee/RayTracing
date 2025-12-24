package sp.sponge.render.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.VK13;
import sp.sponge.render.vulkan.device.LogicalDevice;
import sp.sponge.render.vulkan.device.PhysicalDevice;

public class MemoryAllocator {
    private final long vmaHandle;

    public MemoryAllocator(VulkanInstance instance, PhysicalDevice physicalDevice, LogicalDevice logicalDevice) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            VmaVulkanFunctions vulkanFunctions = VmaVulkanFunctions.calloc(stack)
                    .set(instance.getInstance(), logicalDevice.getVkDevice());

            VmaAllocatorCreateInfo createInfo = VmaAllocatorCreateInfo.calloc(stack)
                    .flags(Vma.VMA_ALLOCATOR_CREATE_BUFFER_DEVICE_ADDRESS_BIT)
                    .instance(instance.getInstance())
                    .vulkanApiVersion(VK13.VK_API_VERSION_1_3)
                    .physicalDevice(physicalDevice.getVkPhysicalDevice())
                    .device(logicalDevice.getVkDevice())
                    .pVulkanFunctions(vulkanFunctions);

            PointerBuffer pointer = stack.mallocPointer(1);
            VulkanUtils.check(
                    Vma.vmaCreateAllocator(createInfo, pointer),
                    "Failed to create VMA"
            );

            this.vmaHandle = pointer.get(0);
        }
    }

    public long getVmaHandle() {
        return vmaHandle;
    }

    public void free() {
        Vma.vmaDestroyAllocator(this.vmaHandle);
    }
}
