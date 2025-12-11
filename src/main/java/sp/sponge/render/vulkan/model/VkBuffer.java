package sp.sponge.render.vulkan.model;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.LongBuffer;

public class VkBuffer {
    private final long allocationSize;
    private final long bufferPtr;
    private final long memory;
    private final PointerBuffer pointerBuffer;
    private final long requestedSize;

    private long mappedMemory;

    public VkBuffer(VulkanCtx ctx, long size, int usage, int reqMask) {
        this.requestedSize = size;
        this.mappedMemory = MemoryUtil.NULL;
        this.pointerBuffer = MemoryUtil.memAllocPointer(1);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDevice vkDevice = ctx.getLogicalDevice().getVkDevice();

            //Create the Buffer
            VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc(stack)
                    .sType$Default()
                    .size(size)
                    .usage(usage)
                    .sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer longBuffer = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreateBuffer(vkDevice, bufferCreateInfo, null, longBuffer),
                    "Failed to create buffer"
            );
            this.bufferPtr = longBuffer.get(0);


            //Allocate Memory
            VkMemoryRequirements memoryRequirements = VkMemoryRequirements.calloc(stack);
            VK10.vkGetBufferMemoryRequirements(vkDevice, this.bufferPtr, memoryRequirements);

            VkMemoryAllocateInfo allocateInfo = VkMemoryAllocateInfo.calloc(stack)
                    .sType$Default()
                    .allocationSize(memoryRequirements.size())
                    .memoryTypeIndex(VulkanUtils.getMemoryType(ctx, memoryRequirements.memoryTypeBits(), reqMask));

            VulkanUtils.check(
                    VK10.vkAllocateMemory(vkDevice, allocateInfo, null, longBuffer),
                    "Failed to allocate memory"
            );
            this.allocationSize = allocateInfo.allocationSize();
            this.memory = longBuffer.get(0);


            //Bind to memory
            VulkanUtils.check(
                    VK10.vkBindBufferMemory(vkDevice, this.bufferPtr, this.memory, 0),
                    "Failed to bind memory Buffer"
            );
        }
    }

    public long getBufferPtr() {
        return bufferPtr;
    }

    public long getRequestedSize() {
        return requestedSize;
    }

    public void free(VulkanCtx ctx) {
        MemoryUtil.memFree(pointerBuffer);
        VkDevice vkDevice = ctx.getLogicalDevice().getVkDevice();
        VK10.vkDestroyBuffer(vkDevice, this.bufferPtr, null);
        VK10.vkFreeMemory(vkDevice, this.memory, null);
    }

    public long map(VulkanCtx ctx) {
        if (this.mappedMemory == MemoryUtil.NULL) {
            VulkanUtils.check(
                    VK10.vkMapMemory(ctx.getLogicalDevice().getVkDevice(), this.memory, 0, this.allocationSize, 0, pointerBuffer),
                    "Failed to map Buffer"
            );
            this.mappedMemory = pointerBuffer.get(0);
        }

        return this.mappedMemory;
    }

    public void unmap(VulkanCtx ctx) {
        if (this.mappedMemory != MemoryUtil.NULL) {
            VK10.vkUnmapMemory(ctx.getLogicalDevice().getVkDevice(), this.memory);
            this.mappedMemory = MemoryUtil.NULL;
        }
    }

}
