package sp.sponge.scene.objects.custom.obj;

import sp.sponge.scene.objects.AbstractObjObject;
import sp.sponge.scene.objects.Objects;
import sp.sponge.util.objects.Transformation;

public class Cube extends AbstractObjObject {
    public Cube(boolean fixed) {
        super(Objects.CUBE, fixed, "quadcube");
    }

    public Cube(Transformation transformation, boolean fixed) {
        super(Objects.CUBE, transformation, fixed, "cube");
    }
}
