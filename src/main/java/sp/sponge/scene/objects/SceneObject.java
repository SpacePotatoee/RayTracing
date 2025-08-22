package sp.sponge.scene.objects;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import sp.sponge.render.Mesh;

public abstract class SceneObject {
    protected Mesh mesh;
    protected boolean fixed;
    protected Matrix4f transformMatrix;
    protected Vector3f position;
    protected Vector3f color;
    private boolean dirty;

    public SceneObject(float x, float y, float z, boolean fixed) {
        this(new Vector3f(x, y, z), fixed);
    }

    public SceneObject(Vector3f position, boolean fixed) {
        this.fixed = fixed;
        this.position = position;
        this.transformMatrix = new Matrix4f().translate(position);
        this.color = new Vector3f(0.5f);
    }

    public abstract Mesh createMesh();

    public Mesh getMesh() {
        if (this.mesh == null || this.isDirty()) {
            this.mesh = this.createMesh();
            this.clean();
        }

        this.mesh.setParameters(this);
        return this.mesh;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        this.transformMatrix.setTranslation(position);
    }

    public void setPosition(float x, float y, float z) {
        Vector3f newPosition = new Vector3f(x, y, z);
        this.position = newPosition;
        this.transformMatrix.setTranslation(newPosition);
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setColor(float red, float green, float blue) {
        this.color = new Vector3f(red, green, blue);
    }

    public Vector3f getColor() {
        return color;
    }

    public float getX() {
        return this.position.x;
    }

    public float getY() {
        return this.position.y;
    }

    public float getZ() {
        return this.position.z;
    }

    public void rotate(float x, float y, float z) {
        this.transformMatrix.rotate(new Quaternionf().rotateXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z)));
    }

    public Matrix4f getTransformMatrix() {
        return this.transformMatrix;
    }

    public boolean shouldUseResolution() {
        return this instanceof ObjectWithResolution;
    }

    public boolean isFixed() {
        return this.fixed;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void clean() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

}
