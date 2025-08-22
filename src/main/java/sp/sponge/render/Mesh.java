package sp.sponge.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import sp.sponge.scene.objects.SceneObject;

public class Mesh {
    private final Vertex[] vertices;
    private final Face[] faces;

    private Vector3f color;
    private Matrix4f transformMatrix;

    private int currentVertexCount;
    private int currentFaceCount;

    public Mesh(int numberOfVertices, int numberOfFaces) {
        this.vertices = new Vertex[numberOfVertices];
        this.faces = new Face[numberOfFaces];
        this.transformMatrix = new Matrix4f();
        this.color = new Vector3f(0.5f);
    }

    public void addFace(Face face) {
        this.faces[currentFaceCount++] = face;
    }

    public void addFace(Vertex v1, Vertex v2, Vertex v3) {
        this.faces[currentFaceCount++] = new Face(v1, v2, v3);
    }

    public void setTransformMatrix(Matrix4f matrix) {
        this.transformMatrix = matrix;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setColor(float red, float green, float blue) {
        this.color = new Vector3f(red, green, blue);
    }

//    public void setPosition(float x, float y, float z) {
//        this.transformMatrix = new Vector3f(x, y, z);
//    }

    public void drawMesh(VertexBuffer vertexBuffer) {
        for (Face face : this.faces) {
            face.drawFace(vertexBuffer, this.transformMatrix, this.color);
        }
    }

    public void setParameters(SceneObject sceneObject) {
        this.setTransformMatrix(sceneObject.getTransformMatrix());
        this.setColor(sceneObject.getColor());
    }

    public record Vertex(float x, float y, float z, float normalX, float normalY, float normalZ){}

    public record Face(Vertex v1, Vertex v2, Vertex v3) {

        public void drawFace(VertexBuffer vertexBuffer, Matrix4f translation, Vector3f color) {
                vertexBuffer.vertex(translation, v1.x, v1.y, v1.z).color(color.x, color.y, color.z, 1.0f).normal(v1.normalX, v1.normalY, v1.normalZ).next();
                vertexBuffer.vertex(translation, v2.x, v2.y, v2.z).color(color.x, color.y, color.z, 1.0f).normal(v2.normalX, v2.normalY, v2.normalZ).next();
                vertexBuffer.vertex(translation, v3.x, v3.y, v3.z).color(color.x, color.y, color.z, 1.0f).normal(v3.normalX, v3.normalY, v3.normalZ).next();
        }
    }

}
