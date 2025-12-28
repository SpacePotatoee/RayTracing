package sp.sponge.scene.objects.custom.obj;

import sp.sponge.scene.objects.AbstractObjObject;
import sp.sponge.scene.objects.Objects;
import sp.sponge.util.objects.Transformation;

public class Sphere extends AbstractObjObject {
    public Sphere(boolean fixed) {
        super(Objects.SPHERE, fixed, "sphere");
    }

    public Sphere(Transformation transformation, boolean fixed) {
        super(Objects.SPHERE, transformation, fixed, "sphere");
    }
}
