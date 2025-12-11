package sp.sponge.scene.objects.custom;

import sp.sponge.render.vulkan.model.Mesh;
import sp.sponge.scene.objects.Objects;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.util.objects.Transformation;

public class Square extends SceneObject {

    public Square(float x, float y, float z, boolean fixed) {
        super(Objects.SQUARE, fixed);
    }

    public Square(Transformation transformation, boolean fixed) {
        super(Objects.SQUARE, transformation, fixed);
    }

    @Override
    public Mesh createMesh() {
        Mesh mesh = new Mesh(2);

//        Mesh.Vertex test = new Mesh.Vertex(0.0f, 0.5f, 0.0f,0.0f, 0.0f, 1.0f);

        Mesh.Vertex v0 = new Mesh.Vertex(0.5f, 0.5f, 0.0f,0.0f, 0.0f, 1.0f);
        Mesh.Vertex v1 = new Mesh.Vertex(-0.5f, 0.5f, 0.0f,0.0f, 0.0f, 1.0f);
        Mesh.Vertex v2 = new Mesh.Vertex(-0.5f, -0.5f, 0.0f,0.0f, 0.0f, 1.0f);
        Mesh.Vertex v3 = new Mesh.Vertex(0.5f, -0.5f, 0.0f,0.0f, 0.0f, 1.0f);

        mesh.addFace(v0, v1, v2);
        mesh.addFace(v0, v2, v3);
//        mesh.addFace(v2, test, v3);
        return mesh;
    }
}
