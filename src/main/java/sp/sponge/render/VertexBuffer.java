package sp.sponge.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class VertexBuffer {
    private final ByteBuffer buffer;
    private int vertexCount;
    private final int vertexArrayID, vertexBufferID;

    public VertexBuffer(int capacity) {
        this.buffer = BufferUtils.createByteBuffer(capacity);

        this.vertexArrayID = GL30.glGenVertexArrays();
        this.vertexBufferID = GL30.glGenBuffers();
    }

    public VertexBuffer vertex(float x, float y, float z) {
        this.buffer.putFloat(x);
        this.buffer.putFloat(y);
        this.buffer.putFloat(z);
        return this;
    }

    public VertexBuffer color(float red, float green, float blue, float alpha) {
        this.buffer.putFloat(red);
        this.buffer.putFloat(green);
        this.buffer.putFloat(blue);
        this.buffer.putFloat(alpha);
        return this;
    }

    public VertexBuffer next() {
        this.vertexCount++;
        return this;
    }

    public void drawElements() {
        glDrawArrays(GL_TRIANGLES, 0, this.vertexCount);
        this.end();
    }

    public void end() {
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        this.buffer.clear();
        this.vertexCount = 0;

        this.disableVertexStates();
    }

    public void bind() {
        glBindVertexArray(vertexArrayID);

        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferID);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.flip(), GL15.GL_STATIC_DRAW);

        this.setupVertexStates();
    }

    //Every object will have the vertex attributes of whatever is in here
    public void setupVertexStates() {
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 28, 0);
        GL30.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 28, Float.BYTES * 3);
    }

    public void disableVertexStates() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
    }

    public static void unbind() {
        glBindVertexArray(0);
    }
}
