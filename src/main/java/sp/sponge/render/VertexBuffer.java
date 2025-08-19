package sp.sponge.render;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class VertexBuffer {
    private final ByteBuffer buffer;
    private int vertexCount;
    private final int vertexArrayID, vertexBufferID;
    private final VertexDataType vertexDataType;

    public VertexBuffer(int capacity, VertexDataType vertexDataType) {
        this.buffer = BufferUtils.createByteBuffer(capacity);

        this.vertexArrayID = GL30.glGenVertexArrays();
        this.vertexBufferID = GL30.glGenBuffers();
        this.vertexDataType = vertexDataType;
    }

    public VertexBuffer vertex(Vector3f offset, float x, float y, float z) {
        this.buffer.putFloat(x + offset.x);
        this.buffer.putFloat(y + offset.y);
        this.buffer.putFloat(z + offset.z);
        return this;
    }

    public VertexBuffer vertex(float x, float y, float z) {
        this.buffer.putFloat(x);
        this.buffer.putFloat(y);
        this.buffer.putFloat(z);
        return this;
    }

    public VertexBuffer color(float red, float green, float blue, float alpha) {
        if (!vertexDataType.contains(VertexData.COLOR)) {
            throw new RuntimeException("Tried to add color data to a buffer that doesn't accept it!");
        }
        this.buffer.putFloat(red);
        this.buffer.putFloat(green);
        this.buffer.putFloat(blue);
        this.buffer.putFloat(alpha);
        return this;
    }

    public VertexBuffer normal(float x, float y, float z) {
        if (!vertexDataType.contains(VertexData.NORMAL)) {
            throw new RuntimeException("Tried to add normal data to a buffer that doesn't accept it!");
        }
        this.buffer.putFloat(x);
        this.buffer.putFloat(y);
        this.buffer.putFloat(z);
        return this;
    }

    public VertexBuffer texture(float x, float y) {
        if (!vertexDataType.contains(VertexData.TEXTURE)) {
            throw new RuntimeException("Tried to add texture data to a buffer that doesn't accept it!");
        }
        this.buffer.putFloat(x);
        this.buffer.putFloat(y);
        return this;
    }

    public void next() {
        this.vertexCount++;
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

    public void setupVertexStates() {
        this.vertexDataType.setupVertexAttributes();
    }

    public void disableVertexStates() {
        this.vertexDataType.disableVertexAttributes();
    }

    public static void unbind() {
        glBindVertexArray(0);
    }



    public enum VertexDataType {
        POSITION_COLOR_NORMAL(VertexData.POSITION, VertexData.COLOR, VertexData.NORMAL),
        POSITION_TEXTURE(VertexData.POSITION, VertexData.TEXTURE),
        POSITION(VertexData.POSITION);

        private final VertexData[] data;
        private final Object2BooleanArrayMap<VertexData> containsMap = new Object2BooleanArrayMap<>();
        private final int stride;

        VertexDataType(VertexData... data) {
            this.data = data;
            int stride = 0;
            for (VertexData vertexData : data) {
                stride += vertexData.numOfBytes;
            }
            this.stride = stride;
        }

        public void setupVertexAttributes() {
            int pointer = 0;
            for (int i = 0; i < data.length; i++) {
                VertexData vertexData = data[i];

                GL20.glEnableVertexAttribArray(i);
                GL30.glVertexAttribPointer(i, vertexData.size, vertexData.type, vertexData.normalized, this.stride, pointer);

                pointer += vertexData.numOfBytes;
            }
        }

        public void disableVertexAttributes() {
            for (int i = 0; i < data.length; i++) {
                GL20.glDisableVertexAttribArray(i);
            }
        }

        public boolean contains(VertexData data) {
            if (containsMap.containsKey(data)) {
                return containsMap.getBoolean(data);
            }

            boolean contains = Arrays.asList(this.data).contains(data);
            containsMap.put(data, contains);

            return contains;
        }
    }

    public enum VertexData {
        POSITION(3, GL11.GL_FLOAT, false),
        COLOR(4, GL11.GL_FLOAT, false),
        NORMAL(3, GL11.GL_FLOAT, true),
        TEXTURE(2, GL11.GL_FLOAT, false);


        private final int size;
        private final int type;
        private final boolean normalized;
        private int numOfBytes;

        VertexData(int size, int type, boolean normalized) {
            this.size = size;
            this.type = type;
            this.normalized = normalized;

            switch (type) {
                case GL11.GL_FLOAT -> this.numOfBytes = 4;
            }

            this.numOfBytes *= size;
        }
    }
}
