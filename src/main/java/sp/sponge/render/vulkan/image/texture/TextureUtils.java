package sp.sponge.render.vulkan.image.texture;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureUtils {

    public static void freeTexture(Texture.TextureInfo textureInfo) {
        STBImage.stbi_image_free(textureInfo.buffer());
    }

    public static Texture.TextureInfo loadTexture(String fileName) {
        Texture.TextureInfo result;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buffer;
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buffer = STBImage.stbi_load(fileName, width, height, channels, 4);
            if (buffer == null) {
                throw new RuntimeException("Could not load " + fileName + " texture: " + STBImage.stbi_failure_reason());
            }

            result = new Texture.TextureInfo(buffer, width.get(0), height.get(0), channels.get(0));
        }

        return result;
    }

}
