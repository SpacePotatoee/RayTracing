package sp.sponge.scene.objects.custom.obj;

import sp.sponge.scene.objects.AbstractObjObject;
import sp.sponge.scene.objects.Objects;
import sp.sponge.util.objects.Transformation;

public class Bunny extends AbstractObjObject {
    public Bunny(boolean fixed) {
        super(Objects.BUNNY, fixed, "bunny");
    }

    public Bunny(Transformation transformation, boolean fixed) {
        super(Objects.BUNNY, transformation, fixed, "bunny");
    }
}
