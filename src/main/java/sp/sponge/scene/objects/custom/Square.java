package sp.sponge.scene.objects.custom;

import org.joml.Vector3d;
import org.joml.Vector3f;
import sp.sponge.render.VertexBuffer;
import sp.sponge.scene.objects.SceneObject;

public class Square extends SceneObject {

    public Square(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Square(Vector3f position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public void render(VertexBuffer vertexBuffer) {
        // 0 1 2
        // 0 2 3

        vertexBuffer.vertex(this.position, 0.5f, 0.5f, 0.0f).color(0.0f, 0.0f, 1.0f, 1.0f).next();   // 0
        vertexBuffer.vertex(this.position, -0.5f, 0.5f, 0.0f).color(1.0f, 0.0f, 0.0f, 1.0f).next();  // 1
        vertexBuffer.vertex(this.position, -0.5f, -0.5f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).next(); // 2

        vertexBuffer.vertex(this.position, 0.5f, 0.5f, 0.0f).color(0.0f, 0.0f, 1.0f, 1.0f).next();   // 0
        vertexBuffer.vertex(this.position, -0.5f, -0.5f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).next(); // 2
        vertexBuffer.vertex(this.position, 0.5f, -0.5f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();  // 3
    }
}
