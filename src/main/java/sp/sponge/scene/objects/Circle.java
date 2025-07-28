package sp.sponge.scene.objects;

import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import sp.sponge.Sponge;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Circle extends SceneObject {
    private static final String vertexShaderSrc = ("#version 330 core\n" +
            "\n" +
            "layout (location = 0) in vec3 Position;\n" +
            "\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = vec4(Position, 1.0);\n" +
            "}");

    private static final String fragmentShaderSrc = ("#version 330 core\n" +
            "\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fragColor = vec4(1.0);\n" +
            "}");

    private int vertexID, fragmentID, shaderProgram;

    private static final float[] vertexArray = {
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private static final int[] elementArray = {
            0, 1, 2,
            0, 2, 3
    };

    private int vaoID, vboID, eboID;

    public Circle(double x, double y, double z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Circle(Vector3d position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public void init() {
        Logger logger = Sponge.getInstance().getLogger();
        vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexID, vertexShaderSrc);
        GL20.glCompileShader(vertexID);

        int success = GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(vertexID, GL20.GL_INFO_LOG_LENGTH);
            logger.severe("ERROR compiling vertex shader");
            logger.info(GL20.glGetShaderInfoLog(vertexID, len));
        }


        fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentID, fragmentShaderSrc);
        GL20.glCompileShader(fragmentID);

        success = GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(fragmentID, GL20.GL_INFO_LOG_LENGTH);
            logger.severe("ERROR compiling fragment shader");
            logger.info(GL20.glGetShaderInfoLog(fragmentID, len));
        }

        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexID);
        GL20.glAttachShader(shaderProgram, fragmentID);
        GL20.glLinkProgram(shaderProgram);

        success = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH);
            logger.severe("ERROR compiling shader proram");
            logger.info(GL20.glGetProgramInfoLog(shaderProgram, len));
        }


        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL30.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_STATIC_DRAW);

        GL20.glEnableVertexAttribArray(0);
        GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 12, 0);
    }

    @Override
    public void render() {
        glUseProgram(shaderProgram);
        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        glUseProgram(0);
    }
}
