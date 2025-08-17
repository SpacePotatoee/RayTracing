package sp.sponge.scene.objects.custom;

import org.joml.Vector3f;

public class Cube extends AbstractObjObject {
    public Cube(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed, "cube");
    }

    public Cube(Vector3f position, boolean fixed) {
        super(position, fixed, "cube");
    }
}
