package sp.sponge.scene.objects.custom;

import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import sp.sponge.render.shader.ShaderRegistry;
import sp.sponge.scene.objects.SceneObject;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Square extends SceneObject {
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

    public Square(double x, double y, double z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Square(Vector3d position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public void init() {
        //Add vertices to the vertex buffer
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        //Add elements to buffer
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
        //Bind shader
        ShaderRegistry.defaultShader.bind();
        //Bind vertex array
        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);

        //draw
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        glUseProgram(0);
    }
}
