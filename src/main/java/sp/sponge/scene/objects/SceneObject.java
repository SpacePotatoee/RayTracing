package sp.sponge.scene.objects;

import org.joml.Vector3f;
import sp.sponge.render.VertexBuffer;

public abstract class SceneObject {
    protected boolean fixed;
    protected Vector3f position;

    public SceneObject(float x, float y, float z, boolean fixed) {
        this(new Vector3f(x, y, z), fixed);
    }

    public SceneObject(Vector3f position, boolean fixed) {
        this.fixed = fixed;
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
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

    public boolean shouldUseResolution() {
        return this instanceof ObjectWithResolution;
    }

    public abstract void render(VertexBuffer vertexBuffer);

//    public String toString() {
//        return Registries.SceneObjectRegistry.
//    }

}
