package sp.sponge.scene.objects.custom;

import org.joml.Vector3f;
import sp.sponge.render.Mesh;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.SceneObject;

public class Circle extends SceneObject implements ObjectWithResolution {
    private Mesh mesh;
    private int resolution = 12;

    //TODO: Make the positions of the shapes actually do something
    public Circle(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Circle(Vector3f position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public Mesh getMesh() {
        if (this.mesh == null || this.isDirty()) {
            this.mesh = new Mesh(this.resolution * 3, this.resolution);

            double radius = 0.5f;
            for (int i = 0; i < this.resolution; i++) {
                double angleFactor = 360.0 / this.resolution;

                double angle = Math.toRadians(angleFactor * i);
                float offsetY = (float) (Math.sin(angle) * radius + this.position.y);
                float offsetX = (float) (Math.cos(angle) * radius + this.position.x);

                double angle2 = Math.toRadians(angleFactor * (i + 1));
                float offset2Y = (float) (Math.sin(angle2) * radius + this.position.y);
                float offset2X = (float) (Math.cos(angle2) * radius + this.position.x);


                Mesh.Vertex v0 = new Mesh.Vertex(this.position.x, this.position.y, this.position.z,1.0f, 1.0f, 1.0f, 1.0f, 0, 0, 1);
                Mesh.Vertex v1 = new Mesh.Vertex(offsetX, offsetY, this.position.z, offsetX, offsetY, 1.0f, 1.0f, 0, 0, 1);
                Mesh.Vertex v2 = new Mesh.Vertex(offset2X, offset2Y, this.position.z, offset2X, offset2Y, 1.0f, 1.0f, 0, 0, 1);

                this.mesh.addFace(v0, v1, v2);
            }

            this.clean();
        }


        return this.mesh;
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
