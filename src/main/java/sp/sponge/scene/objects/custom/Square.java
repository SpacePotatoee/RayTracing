package sp.sponge.scene.objects.custom;

import org.joml.Vector3d;
import sp.sponge.render.VertexBuffer;
import sp.sponge.scene.objects.SceneObject;

public class Square extends SceneObject {

    public Square(double x, double y, double z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Square(Vector3d position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public void render(VertexBuffer vertexBuffer) {

        vertexBuffer.vertex(0.5f, 0.5f, 0.0f);   // 0
        vertexBuffer.vertex(-0.5f, 0.5f, 0.0f);  // 1
        vertexBuffer.vertex(-0.5f, -0.5f, 0.0f); // 2

        vertexBuffer.vertex(0.5f, 0.5f, 0.0f);   // 0
        vertexBuffer.vertex(-0.5f, -0.5f, 0.0f); // 2
        vertexBuffer.vertex(0.5f, -0.5f, 0.0f);  // 3

//        vertexBuffer.indexBuffer(elementArray);
    }
}
