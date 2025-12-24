package sp.sponge.render.vulkan.pipeline.shaders;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;
import sp.sponge.resources.ResourceManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.file.Files;

public class ShaderModule {
    private final long shaderHandle;
    private final int shaderStage;

    public ShaderModule(VulkanCtx ctx, int shaderStage, String shaderSpvFile) {

        this.shaderStage = shaderStage;

        try {
            byte[] contents = Files.readAllBytes(ResourceManager.getAssetFile(shaderSpvFile).toPath());
            this.shaderHandle = createShaderModule(ctx, contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static long createShaderModule(VulkanCtx ctx, byte[] code) {
        long result;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer codeBuffer = stack.malloc(code.length).put(0, code);
            VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack)
                    .sType$Default()
                    .pCode(codeBuffer);

            LongBuffer module = stack.mallocLong(1);

            VulkanUtils.check(
                    VK10.vkCreateShaderModule(ctx.getLogicalDevice().getVkDevice(), createInfo, null, module),
                    "Failed to create Shader Module"
            );

            result = module.get(0);
        }

        return result;
    }

    public long getShaderHandle() {
        return shaderHandle;
    }

    public int getShaderStage() {
        return shaderStage;
    }

    public void free(VulkanCtx ctx) {
        VK10.vkDestroyShaderModule(ctx.getLogicalDevice().getVkDevice(), this.shaderHandle, null);
    }
}
