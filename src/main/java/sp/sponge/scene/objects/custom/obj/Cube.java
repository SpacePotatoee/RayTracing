package sp.sponge.scene.objects.custom.obj;

import org.joml.Vector3f;
import sp.sponge.scene.objects.AbstractObjObject;

public class Cube extends AbstractObjObject {
    public Cube(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed, "cube");
    }

    public Cube(Vector3f position, boolean fixed) {
        super(position, fixed, "cube");
    }
}
