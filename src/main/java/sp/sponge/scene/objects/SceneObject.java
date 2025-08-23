package sp.sponge.scene.objects;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import sp.sponge.render.Mesh;
import sp.sponge.util.Transformation;

public abstract class SceneObject {
    protected Mesh mesh;
    protected boolean fixed;
    protected Transformation transformation;
    protected Vector3f color;
    private boolean dirty;

    public SceneObject(float x, float y, float z, boolean fixed) {
        this(new Vector3f(x, y, z), fixed);
    }

    public SceneObject(Vector3f position, boolean fixed) {
        this.fixed = fixed;
        this.transformation = new Transformation();
        this.transformation.setPosition(position);
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

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setColor(float red, float green, float blue) {
        this.color = new Vector3f(red, green, blue);
    }

    public Vector3f getColor() {
        return color;
    }

    public Transformation getTransformations() {
        return this.transformation;
    }


    public Matrix4f getTransformMatrix() {
        return this.transformation.getTransformationMatrix();
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
