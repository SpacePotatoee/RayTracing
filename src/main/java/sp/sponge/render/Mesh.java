package sp.sponge.render;

import org.joml.Vector3f;
import sp.sponge.util.objects.Material;

public class Mesh {
    private final Face[] faces;
    private Material material;
    private Vector3f color;
    private int currentFaceCount;

    public Mesh(int numberOfFaces) {
        this.faces = new Face[numberOfFaces];
        this.color = new Vector3f(0.5f);
        this.material = new Material();
    }

    public void addFace(Face face) {
        this.faces[currentFaceCount++] = face;
    }

    public void addFace(Vertex v1, Vertex v2, Vertex v3) {
        this.faces[currentFaceCount++] = new Face(v1, v2, v3);
    }


    public Face[] getFaces() {
        return this.faces;
    }

    public int numOfFaces() {
        return this.faces.length;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public void setColor(float red, float green, float blue) {
        this.setColor(new Vector3f(red, green, blue));
    }

    private void setColor(Vector3f color) {
        this.color = color;
    }



    public record Vertex(float x, float y, float z, float normalX, float normalY, float normalZ){}

    public record Face(Vertex v1, Vertex v2, Vertex v3) {}

}
