package sp.sponge.scene.objects.custom;

import org.joml.Vector3d;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.SceneObject;

public class Circle extends SceneObject implements ObjectWithResolution {

    public Circle(double x, double y, double z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Circle(Vector3d position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public void init() {

    }

    @Override
    public void render() {

    }

    @Override
    public int getResolution() {
        return 0;
    }

    @Override
    public void setResolution(int resolution) {

    }
}
