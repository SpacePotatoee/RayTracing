package sp.sponge.scene.objects.custom.obj;

import sp.sponge.render.Mesh;
import sp.sponge.scene.objects.AbstractObjObject;
import sp.sponge.scene.objects.Objects;
import sp.sponge.util.objects.Transformation;

public class Dragon extends AbstractObjObject {
    private Mesh mesh;

    public Dragon(boolean fixed) {
        super(Objects.DRAGON, fixed, "dragon_8k");
    }

    public Dragon(Transformation transformation, boolean fixed) {
        super(Objects.DRAGON, transformation, fixed, "dragon_8k");
    }
}
