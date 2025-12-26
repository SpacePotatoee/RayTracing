package sp.sponge.render.vulkan.image.texture;

import org.lwjgl.vulkan.VK10;
import sp.sponge.Sponge;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.device.Queue;
import sp.sponge.render.vulkan.device.command.CommandBuffer;
import sp.sponge.render.vulkan.device.command.CommandPool;
import sp.sponge.util.manager.Manager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class TextureManager implements Manager {
    private static final String directory = "textures";
    private final HashMap<String, Texture> textureMap = new HashMap<>();

    public Texture getTexture(String id) {
        return textureMap.get(id);
    }

    public void addTexture(String id, Path path, int format) {
        VulkanCtx ctx = Sponge.getInstance().getMainRenderer().getVulkanCtx();

        if (textureMap.containsKey(id)) {
            return;
        }

        Texture.TextureInfo textureInfo = TextureUtils.loadTexture(path.toString());
        Texture texture = new Texture(ctx, id, textureInfo, format);
        TextureUtils.freeTexture(textureInfo);

        textureMap.put(id, texture);
    }

    public void sendAllTexturesToGpu(VulkanCtx ctx, CommandPool commandPool, Queue queue) {
        CommandBuffer commandBuffer = new CommandBuffer(ctx, commandPool, true, true);

        commandBuffer.beginRecordingPrimary();
        textureMap.forEach((s, texture) -> texture.recordImageTransition(commandBuffer));
        commandBuffer.endRecording();

        commandBuffer.submitAndWait(ctx, queue);
        commandBuffer.close(ctx, commandPool);

        //Don't need CPU buffers anymore since they're now on the gpu
        textureMap.forEach((s, texture) -> texture.freeBuffer(ctx));
    }


    @Override
    public String getDirectoryToCheck() {
        return directory;
    }

    @Override
    public void acceptPath(Path relativePath, Path absolutePath) {
        String id = relativePath.toString().replace(".png", "");
        this.addTexture(id, absolutePath, VK10.VK_FORMAT_R8G8B8A8_SRGB);
    }

    @Override
    public void free(VulkanCtx ctx) {
        textureMap.forEach((s, texture) -> texture.free(ctx));
    }
}
