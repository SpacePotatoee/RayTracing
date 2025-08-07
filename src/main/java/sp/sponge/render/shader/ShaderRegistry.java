package sp.sponge.render.shader;

import sp.sponge.Sponge;

public class ShaderRegistry {

    //TODO: Make a file reader
    public static ShaderProgram defaultShader = new ShaderProgram(
            ("#version 330 core\n" +
            "\n" +
            "layout (location = 0) in vec3 Position;\n" +
            "layout (location = 1) in vec4 Color;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = Color;\n" +
            "    gl_Position = vec4(Position, 1.0);\n" +
            "}"),

            ("#version 330 core\n" +
            "\n" +
            "in vec4 color;\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fragColor = color;\n" +
            "}")
    );

    public static void registerShaders() {
        Sponge.getInstance().getLogger().info("Registering Shaders");
    }

}
