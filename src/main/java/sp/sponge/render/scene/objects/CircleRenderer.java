package sp.sponge.render.scene.objects;

public class CircleRenderer extends ObjectRenderer {
    private String vertexShaderSrc = "#version 330 core\n" +
            "\n" +
            "layout (location = 0) in vec3 Position;\n" +
            "layout (location = 1) in vec4 Color;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = Color;\n" +
            "    gl_Position = vec4(Position, 1.0);\n" +
            "}";

    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 color;\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fragColor = color;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    @Override
    public void render() {

    }
}
