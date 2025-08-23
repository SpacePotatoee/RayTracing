package sp.sponge.scene.objects.custom.obj;

import sp.sponge.scene.objects.AbstractObjObject;
import sp.sponge.scene.objects.Objects;
import sp.sponge.util.Transformation;

public class Cube extends AbstractObjObject {
    public Cube(boolean fixed) {
        super(Objects.CUBE, fixed, "cube");
    }

    public Cube(Transformation transformation, boolean fixed) {
        super(Objects.CUBE, transformation, fixed, "cube");
    }
}
