package sp.sponge.scene.objects;

import org.joml.Vector3d;

public class Circle extends SceneObject {

    public Circle(double x, double y, double z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Circle(Vector3d position, boolean fixed) {
        super(position, fixed);
    }

}
