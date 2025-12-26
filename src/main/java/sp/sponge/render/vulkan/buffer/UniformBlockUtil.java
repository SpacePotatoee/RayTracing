package sp.sponge.render.vulkan.buffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

public class UniformBlockUtil {
    private final ByteBuffer buffer;
    private int position;

    public UniformBlockUtil(ByteBuffer buffer) {
        this.buffer = buffer;
        this.position = 0;
    }

    public void putInt(int value) {
        this.buffer.putInt(position, value);
        position += Integer.BYTES;
    }

    public void putFloat(float value) {
        this.buffer.putFloat(position, value);
        position += Float.BYTES;
    }

    public void putLong(long value) {
        this.buffer.putLong(position, value);
        position += Long.BYTES;
    }

    public void putMatrix4f(Matrix4f value) {
        value.get(position, buffer);
        position += Float.BYTES * 16;
    }

    public void putVec3f(Vector3f value) {
        this.putFloat(value.x);
        this.putFloat(value.y);
        this.putFloat(value.z);
        this.putFloat(0);
    }

}
