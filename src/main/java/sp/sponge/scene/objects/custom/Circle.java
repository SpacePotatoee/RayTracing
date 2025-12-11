package sp.sponge.scene.objects.custom;

import sp.sponge.render.vulkan.model.Mesh;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.Objects;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.util.objects.Transformation;

public class Circle extends SceneObject implements ObjectWithResolution {
    private Mesh mesh;
    private int resolution = 12;

    public Circle(boolean fixed) {
        super(Objects.CIRCLE, fixed);
    }

    public Circle(Transformation transformation, boolean fixed) {
        super(Objects.CIRCLE, transformation, fixed);
    }

    @Override
    public Mesh createMesh() {
        Mesh mesh = new Mesh(this.resolution);

        double radius = 0.5f;
        for (int i = 0; i < this.resolution; i++) {
            double angleFactor = 360.0 / this.resolution;

            double angle = Math.toRadians(angleFactor * i);
            float offsetY = (float) (Math.sin(angle) * radius);
            float offsetX = (float) (Math.cos(angle) * radius);

            double angle2 = Math.toRadians(angleFactor * (i + 1));
            float offset2Y = (float) (Math.sin(angle2) * radius);
            float offset2X = (float) (Math.cos(angle2) * radius);


            Mesh.Vertex v0 = new Mesh.Vertex(0, 0, 0,0, 0, 1);
            Mesh.Vertex v1 = new Mesh.Vertex(offsetX, offsetY, 0, 0, 0, 1);
            Mesh.Vertex v2 = new Mesh.Vertex(offset2X, offset2Y, 0,0, 0, 1);

            mesh.addFace(v0, v1, v2);
        }


        return mesh;
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
