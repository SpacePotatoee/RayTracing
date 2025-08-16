package sp.sponge.render.shader;

import sp.sponge.Sponge;
import sp.sponge.resources.ResourceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Logger;

public class ShaderRegistry {
    public static HashMap<ShaderProgram, File[]> shaders = new HashMap<>();

    //TODO: Make a file reader
    public static ShaderProgram defaultShader = new ShaderProgram();

    static {
        Logger logger = Sponge.getInstance().getLogger();
        File defaultVertexShader = ResourceManager.getFile("shaders/default/default.vsh");
        File defaultFragmentShader = ResourceManager.getFile("shaders/default/default.fsh");
        try {
            FileInputStream fileInputStream = new FileInputStream(defaultVertexShader);
            String vertexShaderText = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);

            fileInputStream = new FileInputStream(defaultFragmentShader);
            String fragmentShaderText = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
            defaultShader.compile(vertexShaderText, fragmentShaderText);
        } catch (IOException e) {
            logger.severe("Failed to read Shaders");
            throw new RuntimeException(e);
        }


        shaders.put(defaultShader, new File[]{defaultVertexShader, defaultFragmentShader});
    }


    public static void registerShaders() {
        Sponge.getInstance().getLogger().info("Registering Shaders");
    }

}
