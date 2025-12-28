package sp.sponge.scene.objects.custom.obj;

import sp.sponge.scene.objects.AbstractObjObject;
import sp.sponge.scene.objects.Objects;
import sp.sponge.util.objects.Transformation;

public class Sponza extends AbstractObjObject {
    public Sponza(boolean fixed) {
        super(Objects.SPONZA, fixed, "sponza");
    }

    public Sponza(Transformation transformation, boolean fixed) {
        super(Objects.SPONZA, transformation, fixed, "sponza");
    }
}
