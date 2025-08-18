package sp.sponge.scene.objects.custom.obj;

import org.joml.Vector3f;
import sp.sponge.scene.objects.AbstractObjObject;

public class Bunny extends AbstractObjObject {
    public Bunny(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed, "bunny");
    }

    public Bunny(Vector3f position, boolean fixed) {
        super(position, fixed, "bunny");
    }
}
