package sp.sponge.render.shader;

import sp.sponge.Sponge;

public class ShaderRegistry {

    //TODO: Make a file reader
    public static ShaderProgram defaultShader = new ShaderProgram(
            ("#version 330 core\n" +
            "\n" +
            "layout (location = 0) in vec3 Position;\n" +
            "\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = vec4(Position, 1.0);\n" +
            "}"),

            ("#version 330 core\n" +
            "\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fragColor = vec4(1.0);\n" +
            "}")
    );

    public static void registerShaders() {
        Sponge.getInstance().getLogger().info("Registering Shaders");
    }

}
