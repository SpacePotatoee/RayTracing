package sp.sponge.scene.objects.custom.obj;

import org.joml.Vector3f;
import sp.sponge.render.Mesh;
import sp.sponge.scene.objects.AbstractObjObject;

public class Dragon extends AbstractObjObject {
    private Mesh mesh;

    public Dragon(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed, "dragon_8k");
    }

    public Dragon(Vector3f position, boolean fixed) {
        super(position, fixed, "dragon_8k");
    }
}
