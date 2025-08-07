package sp.sponge.scene.objects.custom;

import org.joml.Vector3d;
import sp.sponge.render.VertexBuffer;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.SceneObject;

public class Circle extends SceneObject implements ObjectWithResolution {
    private int resolution = 12;

    //TODO: Make the positions of the shapes actually do something
    public Circle(double x, double y, double z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Circle(Vector3d position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public void render(VertexBuffer vertexBuffer) {
        double radius = 0.5f;

        for (int i = 0; i < this.resolution; i++) {
            double angle = Math.toRadians((360.0 / this.resolution) * i);
            float offsetY = (float) (Math.sin(angle) * radius);
            float offsetX = (float) (Math.cos(angle) * radius);

            double angle2 = Math.toRadians((360.0 / this.resolution) * (i + 1));
            float offset2Y = (float) (Math.sin(angle2) * radius);
            float offset2X = (float) (Math.cos(angle2) * radius);

            vertexBuffer.vertex(0.0f, 0.0f, 0.0f);
            vertexBuffer.vertex(offsetX, offsetY, 0.0f);
            vertexBuffer.vertex(offset2X, offset2Y, 0.0f);
        }

    }

    @Override
    public int getResolution() {
        return this.resolution;
    }

    @Override
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }
}
