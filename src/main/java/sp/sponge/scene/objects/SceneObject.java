package sp.sponge.scene.objects;

import org.joml.Vector3d;
import sp.sponge.scene.registries.Registries;

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

    public boolean shouldUseResolution() {
        return this instanceof ObjectWithResolution;
    }

    public abstract void init();

    public abstract void render();

//    public String toString() {
//        return Registries.SceneObjectRegistry.
//    }

}
