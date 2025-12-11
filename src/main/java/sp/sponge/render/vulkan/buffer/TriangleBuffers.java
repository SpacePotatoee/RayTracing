package sp.sponge.render.vulkan.buffer;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCopy;
import sp.sponge.render.vulkan.model.Mesh;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.device.command.CommandBuffer;
import sp.sponge.render.vulkan.model.VkBuffer;

import java.nio.FloatBuffer;

public class TriangleBuffers {
    private static final int SIZE = 100000;
    private final VulkanCtx vulkanCtx;
    private final BufferPair vertexBuffers;
    private int numOfTriangles;
    private FloatBuffer buffer;

    public TriangleBuffers(VulkanCtx ctx) {
        this.vulkanCtx = ctx;
        this.vertexBuffers = createBufferPair(ctx, false);
    }

    public void putMesh(Matrix4f transform, Mesh mesh) {
        if (buffer == null) return;

        for (Mesh.Face face : mesh.getFaces()) {
            Mesh.Vertex v1 = face.v1();
//            Vector3f pointA = transform.transformPosition(v1.x(), v1.y(), v1.z(), new Vector3f());
            buffer.put(v1.x());
            buffer.put(v1.y());
            buffer.put(v1.z());

            Mesh.Vertex v2 = face.v2();
//            Vector3f pointB = transform.transformPosition(v2.x(), v2.y(), v2.z(), new Vector3f());
            buffer.put(v2.x());
            buffer.put(v2.y());
            buffer.put(v2.z());

            Mesh.Vertex v3 = face.v3();
//            Vector3f pointC = transform.transformPosition(v3.x(), v3.y(), v3.z(), new Vector3f());
            buffer.put(v3.x());
            buffer.put(v3.y());
            buffer.put(v3.z());
            this.numOfTriangles++;
        }
    }

    public void startMapping() {
        long mappedMemory = this.vertexBuffers.cpuBuffer().map(this.vulkanCtx);
        this.buffer = MemoryUtil.memFloatBuffer(mappedMemory, (int) this.vertexBuffers.cpuBuffer().getRequestedSize());
    }

    public void stopMapping() {
        this.vertexBuffers.cpuBuffer().unmap(this.vulkanCtx);
        this.buffer = null;
    }

    public void sendVerticesToGpu(CommandBuffer commandBuffer) {
        this.sendDataToGpu(commandBuffer, this.vertexBuffers);
    }

    private void sendDataToGpu(CommandBuffer commandBuffer, BufferPair pair) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCopy.Buffer copyRegion = VkBufferCopy.calloc(1, stack)
                    .srcOffset(0).dstOffset(0).size(pair.cpuBuffer().getRequestedSize());
            VK10.vkCmdCopyBuffer(commandBuffer.getVkCommandBuffer(), pair.cpuBuffer().getBufferPtr(), pair.gpuBuffer().getBufferPtr(), copyRegion);
        }
    }

    private BufferPair createBufferPair(VulkanCtx ctx, boolean index) {
        int usage = index ? VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT : VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;

        VkBuffer cpuBuffer = new VkBuffer(ctx, SIZE,
                VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
        );

        VkBuffer gpuBuffer = new VkBuffer(ctx, SIZE,
                VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT | usage,
                VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT
        );

        return new BufferPair(cpuBuffer, gpuBuffer);
    }

    public int getNumOfTriangles() {
        return numOfTriangles;
    }

    public void free() {
        this.vertexBuffers.cpuBuffer.free(this.vulkanCtx);
        this.vertexBuffers.gpuBuffer.free(this.vulkanCtx);
    }

    public long getGpuBuffer() {
        return this.vertexBuffers.gpuBuffer.getBufferPtr();
    }

    private record BufferPair(VkBuffer cpuBuffer, VkBuffer gpuBuffer){}
}
