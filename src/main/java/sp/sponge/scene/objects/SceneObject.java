package sp.sponge.scene.objects;

import org.joml.Vector3d;

public abstract class SceneObject {
    protected boolean fixed;
    protected Vector3d position;

    public SceneObject(double x, double y, double z, boolean fixed) {
        this(new Vector3d(x, y, z), fixed);
    }

    public SceneObject(Vector3d position, boolean fixed) {
        this.fixed = fixed;
        this.position = position;
        this.init();
    }

    public abstract void init();

    public abstract void render();

}
