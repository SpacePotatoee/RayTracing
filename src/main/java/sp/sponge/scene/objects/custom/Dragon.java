package sp.sponge.scene.objects.custom;

import org.joml.Vector3f;
import sp.sponge.render.Mesh;
import sp.sponge.scene.objects.SceneObject;

public class Dragon extends AbstractObjObject {
    private Mesh mesh;

    public Dragon(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed, "dragon");
    }

    public Dragon(Vector3f position, boolean fixed) {
        super(position, fixed, "dragon");
    }
}
