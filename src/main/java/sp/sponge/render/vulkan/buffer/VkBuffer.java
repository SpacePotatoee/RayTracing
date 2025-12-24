package sp.sponge.render.vulkan.buffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.*;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class VkBuffer {
    private final long bufferPtr;
    private final long allocation;
    private final PointerBuffer pointerBuffer;
    private final long requestedSize;

    private long mappedMemory;

    public VkBuffer(VulkanCtx ctx, long size, int usage, int vmaUsage, int vmaFlags, int reqMask) {
        this.requestedSize = size;
        this.mappedMemory = MemoryUtil.NULL;
        this.pointerBuffer = MemoryUtil.memAllocPointer(1);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            //Create the Buffer
            VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc(stack)
                    .sType$Default()
                    .size(size)
                    .usage(usage)
                    .sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);

            //Allocate Memory
            VmaAllocationCreateInfo allocationCreateInfo = VmaAllocationCreateInfo.calloc(stack)
                    .usage(vmaUsage)
                    .flags(vmaFlags)
                    .requiredFlags(reqMask);

            PointerBuffer memPtrBuffer = stack.callocPointer(1);
            LongBuffer longBuffer = stack.mallocLong(1);
            VulkanUtils.check(
                    Vma.vmaCreateBuffer(ctx.getMemoryAllocator().getVmaHandle(), bufferCreateInfo, allocationCreateInfo, longBuffer, memPtrBuffer, null),
                    "Failed to create Buffer"
            );
            this.bufferPtr = longBuffer.get(0);
            this.allocation = memPtrBuffer.get(0);
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
        this.unmap(ctx);
        Vma.vmaDestroyBuffer(ctx.getMemoryAllocator().getVmaHandle(), this.bufferPtr, this.allocation);
    }

    public ByteBuffer map(VulkanCtx ctx) {
        if (this.mappedMemory == MemoryUtil.NULL) {
            VulkanUtils.check(
                    Vma.vmaMapMemory(ctx.getMemoryAllocator().getVmaHandle(), this.allocation, pointerBuffer),
                    "Failed to map Buffer"
            );
            this.mappedMemory = pointerBuffer.get(0);
        }

        return MemoryUtil.memByteBuffer(this.mappedMemory, (int) this.requestedSize);
    }

    public void unmap(VulkanCtx ctx) {
        if (this.mappedMemory != MemoryUtil.NULL) {
            Vma.vmaUnmapMemory(ctx.getMemoryAllocator().getVmaHandle(), this.allocation);
            this.mappedMemory = MemoryUtil.NULL;
        }
    }

}
