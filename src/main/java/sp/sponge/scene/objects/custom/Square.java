package sp.sponge.scene.objects.custom;

import org.joml.Vector3f;
import sp.sponge.render.Mesh;
import sp.sponge.scene.objects.SceneObject;

public class Square extends SceneObject {

    public Square(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Square(Vector3f position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public Mesh createMesh() {
        Mesh mesh = new Mesh(6, 2);

        Mesh.Vertex v0 = new Mesh.Vertex(0.5f, 0.5f, 0.0f,0.0f, 0.0f, 1.0f);
        Mesh.Vertex v1 = new Mesh.Vertex(-0.5f, 0.5f, 0.0f,0.0f, 0.0f, 1.0f);
        Mesh.Vertex v2 = new Mesh.Vertex(-0.5f, -0.5f, 0.0f,0.0f, 0.0f, 1.0f);
        Mesh.Vertex v3 = new Mesh.Vertex(0.5f, -0.5f, 0.0f,0.0f, 0.0f, 1.0f);

        mesh.addFace(v0, v1, v2);
        mesh.addFace(v0, v2, v3);
        return mesh;
    }
}
