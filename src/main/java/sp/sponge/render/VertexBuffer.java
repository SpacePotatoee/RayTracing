package sp.sponge.render;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.glBindBufferBase;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;

public class VertexBuffer {
    private final ByteBuffer buffer;
    private int vertexCount;
    private final int vertexArrayID, vertexBufferID;
    private final VertexDataType vertexDataType;

    private final boolean rayTrace;
    private int trianglePositionsBuffer;
    private ByteBuffer trianglePositions;

    private int meshDataBuffer;
    private ByteBuffer meshData;

    private int numOfTriangles;
    private int numOfMeshes;

    public VertexBuffer(int capacity, VertexDataType vertexDataType, boolean rayTrace) {
        this.buffer = BufferUtils.createByteBuffer(capacity);

        this.vertexArrayID = GL30.glGenVertexArrays();
        this.vertexBufferID = GL30.glGenBuffers();
        this.vertexDataType = vertexDataType;
        this.rayTrace = rayTrace;
        if (rayTrace) {
            this.trianglePositions = BufferUtils.createByteBuffer(capacity);
            this.trianglePositionsBuffer = GL30.glGenBuffers();

            this.meshData = BufferUtils.createByteBuffer(capacity);
            this.meshDataBuffer = GL30.glGenBuffers();
        }
    }

    public VertexBuffer addMesh(Matrix4f offset, Mesh mesh) {
        this.meshData.putInt(mesh.numOfFaces());
        this.meshData.putInt(this.numOfTriangles);
        this.meshData.putInt(0);
        this.meshData.putInt(0);

        Vector3f meshColor = mesh.getMaterial().getColor();
        this.meshData.putFloat(meshColor.x);
        this.meshData.putFloat(meshColor.y);
        this.meshData.putFloat(meshColor.z);
        this.meshData.putFloat(mesh.getMaterial().getEmissiveStrength()); //Padding

        for (Mesh.Face face : mesh.getFaces()) {
            //Triangle

            //Point A
            Vector3f pointA = offset.transformPosition(face.v1().x(), face.v1().y(), face.v1().z(), new Vector3f());
            this.trianglePositions.putFloat(pointA.x());
            this.trianglePositions.putFloat(pointA.y());
            this.trianglePositions.putFloat(pointA.z());
            this.trianglePositions.putFloat(0.0f);

            //Point B
            Vector3f pointB = offset.transformPosition(face.v2().x(), face.v2().y(), face.v2().z(), new Vector3f());
            this.trianglePositions.putFloat(pointB.x());
            this.trianglePositions.putFloat(pointB.y());
            this.trianglePositions.putFloat(pointB.z());
            this.trianglePositions.putFloat(0.0f);

            //Point c
            Vector3f pointC = offset.transformPosition(face.v3().x(), face.v3().y(), face.v3().z(), new Vector3f());
            this.trianglePositions.putFloat(pointC.x());
            this.trianglePositions.putFloat(pointC.y());
            this.trianglePositions.putFloat(pointC.z());
            this.trianglePositions.putFloat(0.0f);
            numOfTriangles++;
        }
        numOfMeshes++;

        return this;
    }

//    public VertexBuffer vertex(Matrix4f offset, float x, float y, float z) {
//        Vector3f vector3f = offset.transformPosition(x, y, z, new Vector3f());
//        this.buffer.putFloat(vector3f.x);
//        this.buffer.putFloat(vector3f.y);
//        this.buffer.putFloat(vector3f.z);
//        return this;
//    }

    public VertexBuffer vertex(float x, float y, float z) {
        this.buffer.putFloat(x);
        this.buffer.putFloat(y);
        this.buffer.putFloat(z);
        return this;
    }

//    public VertexBuffer color(float red, float green, float blue, float alpha) {
//        if (!vertexDataType.contains(VertexData.COLOR)) {
//            throw new RuntimeException("Tried to add color data to a buffer that doesn't accept it!");
//        }
//        this.buffer.putFloat(red);
//        this.buffer.putFloat(green);
//        this.buffer.putFloat(blue);
//        this.buffer.putFloat(alpha);
//        return this;
//    }
//
//    public VertexBuffer normal(float x, float y, float z) {
//        if (!vertexDataType.contains(VertexData.NORMAL)) {
//            throw new RuntimeException("Tried to add normal data to a buffer that doesn't accept it!");
//        }
//        this.buffer.putFloat(x);
//        this.buffer.putFloat(y);
//        this.buffer.putFloat(z);
//        return this;
//    }

    public VertexBuffer texture(float x, float y) {
        if (!vertexDataType.contains(VertexData.TEXTURE)) {
            throw new RuntimeException("Tried to add texture data to a buffer that doesn't accept it!");
        }
        this.buffer.putFloat(x);
        this.buffer.putFloat(y);
        return this;
    }

    public VertexBuffer material(int mat) {
        if (!vertexDataType.contains(VertexData.MATERIAL)) {
            throw new RuntimeException("Tried to add material data to a buffer that doesn't accept it!");
        }
        this.buffer.putInt(mat);
        return this;
    }

    public void next() {
        this.vertexCount++;
        if (this.vertexCount % 3 == 0) {
            this.numOfTriangles++;
        }
    }

    public void drawElements() {
        glDrawArrays(GL_TRIANGLES, 0, this.vertexCount);
        this.end();
    }

    public void end() {
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        this.buffer.clear();
        this.vertexCount = 0;

        this.disableVertexStates();
    }

    public void endRayTRacing() {
        if (rayTrace) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, 0);
            this.trianglePositions.clear();
            this.meshData.clear();
            this.numOfTriangles = 0;
            this.numOfMeshes = 0;
        }
    }

    public void bindRayTracingBuffers() {
        if (rayTrace) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, this.trianglePositionsBuffer);
            GL30.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, trianglePositions.flip(), GL15.GL_DYNAMIC_DRAW);

            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, this.meshDataBuffer);
            GL30.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, meshData.flip(), GL15.GL_DYNAMIC_DRAW);
        }
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

    public int getNumOfMeshes() {
        return this.numOfMeshes;
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
        TEXTURE(2, GL11.GL_FLOAT, false),
        MATERIAL(1, GL11.GL_INT, false);


        private final int size;
        private final int type;
        private final boolean normalized;
        private int numOfBytes;

        VertexData(int size, int type, boolean normalized) {
            this.size = size;
            this.type = type;
            this.normalized = normalized;

            switch (type) {
                case GL11.GL_FLOAT, GL11.GL_INT -> this.numOfBytes = 4;
            }

            this.numOfBytes *= size;
        }
    }
}
