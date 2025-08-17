package sp.sponge.render;

import org.joml.Vector3f;

public class Mesh {
    private final Vertex[] vertices;
    private final Face[] faces;

    private Vector3f translation;

    private int currentVertexCount;
    private int currentFaceCount;

    public Mesh(int numberOfVertices, int numberOfFaces) {
        this.vertices = new Vertex[numberOfVertices];
        this.faces = new Face[numberOfFaces];
        this.translation = new Vector3f();
    }

    public void addFace(Face face) {
        this.faces[currentFaceCount++] = face;
    }

    public void addFace(Vertex v1, Vertex v2, Vertex v3) {
        this.faces[currentFaceCount++] = new Face(v1, v2, v3);
    }

    public void setPosition(Vector3f position) {
        this.translation = position;
    }

    public void setPosition(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
    }

    public void drawMesh(VertexBuffer vertexBuffer) {
        for (Face face : this.faces) {
            face.drawFace(vertexBuffer, this.translation);
        }
    }

    public record Vertex(float x, float y, float z, float red, float green, float blue, float alpha, float normalX, float normalY, float normalZ){}

    public record Face(Vertex v1, Vertex v2, Vertex v3) {

        public void drawFace(VertexBuffer vertexBuffer, Vector3f translation) {
                vertexBuffer.vertex(translation, v1.x, v1.y, v1.z).color(v1.red, v1.green, v1.blue, v1.alpha).normal(v1.normalX, v1.normalY, v1.normalZ).next();
                vertexBuffer.vertex(translation, v2.x, v2.y, v2.z).color(v2.red, v2.green, v2.blue, v2.alpha).normal(v2.normalX, v2.normalY, v2.normalZ).next();
                vertexBuffer.vertex(translation, v3.x, v3.y, v3.z).color(v3.red, v3.green, v3.blue, v3.alpha).normal(v3.normalX, v3.normalY, v3.normalZ).next();
        }
    }

}
