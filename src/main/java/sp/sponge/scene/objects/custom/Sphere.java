package sp.sponge.scene.objects.custom;

import org.joml.SimplexNoise;
import org.joml.Vector3f;
import sp.sponge.render.Mesh;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.util.Vec3f;

public class Sphere extends SceneObject implements ObjectWithResolution {
    private Mesh mesh;
    private int resolution = 1;

    public Sphere(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Sphere(Vector3f position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public Mesh getMesh() {
        if (this.mesh == null || this.isDirty()) {
            this.mesh = new Mesh(60, 20);
            double radius = 0.5f;

            double phi = 1.618033988749; //Golden ratio
            float a = 1.0f;
            float c = (float) phi;

            Vec3f color = getRandomColor(3485.0f);
            Mesh.Vertex v0 = new Mesh.Vertex(0.0f, c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v1 = new Mesh.Vertex(-a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v2 = new Mesh.Vertex(a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v0, v1, v2);

            color = getRandomColor(7425.0f);
            Mesh.Vertex v3 = new Mesh.Vertex(0.0f, c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v4 = new Mesh.Vertex(c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v5 = new Mesh.Vertex(a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v3, v4, v5);

            color = getRandomColor(4565.0f);
            Mesh.Vertex v6 = new Mesh.Vertex(0.0f, c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v7 = new Mesh.Vertex(c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v8 = new Mesh.Vertex(0.0f, c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v6, v7, v8);

            color = getRandomColor(3647.0f);
            Mesh.Vertex v9 = new Mesh.Vertex(0.0f, c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v10 = new Mesh.Vertex(c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v11 = new Mesh.Vertex(a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v9, v10, v11);

            color = getRandomColor(7327.0f);
            Mesh.Vertex v12 = new Mesh.Vertex(0.0f, c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v13 = new Mesh.Vertex(a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v14 = new Mesh.Vertex(-a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v12, v13, v14);

            color = getRandomColor(934.6f);
            Mesh.Vertex v15 = new Mesh.Vertex(0.0f, c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v16 = new Mesh.Vertex(-a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v17 = new Mesh.Vertex(-c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v15, v16, v17);

            color = getRandomColor(8246.6f);
            Mesh.Vertex v18 = new Mesh.Vertex(0.0f, c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v19 = new Mesh.Vertex(-c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v20 = new Mesh.Vertex(0.0f, c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v18, v19, v20);

            color = getRandomColor(763.0f);
            Mesh.Vertex v21 = new Mesh.Vertex(0.0f, c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v22 = new Mesh.Vertex(-c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v23 = new Mesh.Vertex(-a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v21, v22, v23);

            color = getRandomColor(843.0f);
            Mesh.Vertex v24 = new Mesh.Vertex(-c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v25 = new Mesh.Vertex(-a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v26 = new Mesh.Vertex(-c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v24, v25, v26);

            color = getRandomColor(563.0f);
            Mesh.Vertex v27 = new Mesh.Vertex(-a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v28 = new Mesh.Vertex(-c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v29 = new Mesh.Vertex(0.0f, -c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v27, v28, v29);

            color = getRandomColor(7356.0f);
            Mesh.Vertex v30 = new Mesh.Vertex(-a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v31 = new Mesh.Vertex(0.0f, -c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v32 = new Mesh.Vertex(a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v30, v31, v32);

            color = getRandomColor(2135.0f);
            Mesh.Vertex v33 = new Mesh.Vertex(0.0f, -c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v34 = new Mesh.Vertex(a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v35 = new Mesh.Vertex(c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v33, v34, v35);

            color = getRandomColor(146.0f);
            Mesh.Vertex v36 = new Mesh.Vertex(c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v37 = new Mesh.Vertex(a, 0.0f, c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v38 = new Mesh.Vertex(c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v36, v37, v38);

            color = getRandomColor(621.0f);
            Mesh.Vertex v39 = new Mesh.Vertex(c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v40 = new Mesh.Vertex(c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v41 = new Mesh.Vertex(a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v39, v40, v41);

            color = getRandomColor(823.0f);
            Mesh.Vertex v42 = new Mesh.Vertex(a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v43 = new Mesh.Vertex(c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v44 = new Mesh.Vertex(0.0f, -c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v42, v43, v44);

            color = getRandomColor(73.0f);
            Mesh.Vertex v45 = new Mesh.Vertex(a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v46 = new Mesh.Vertex(0.0f, -c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v47 = new Mesh.Vertex(-a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v45, v46, v47);

            color = getRandomColor(1124.0f);
            Mesh.Vertex v48 = new Mesh.Vertex(-a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v49 = new Mesh.Vertex(0.0f, -c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v50 = new Mesh.Vertex(-c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v48, v49, v50);

            color = getRandomColor(8433.0f);
            Mesh.Vertex v51 = new Mesh.Vertex(-a, 0.0f, -c, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v52 = new Mesh.Vertex(-c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v53 = new Mesh.Vertex(-c, a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v51, v52, v53);

            color = getRandomColor(7234.0f);
            Mesh.Vertex v54 = new Mesh.Vertex(-c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v55 = new Mesh.Vertex(0.0f, -c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v56 = new Mesh.Vertex(0.0f, -c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v54, v55, v56);

            color = getRandomColor(0.0f);
            Mesh.Vertex v57 = new Mesh.Vertex(c, -a, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v58 = new Mesh.Vertex(0.0f, -c, a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            Mesh.Vertex v59 = new Mesh.Vertex(0.0f, -c, -a, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 1.0f);
            this.mesh.addFace(v57, v58, v59);

            this.clean();
        }

        this.mesh.setPosition(this.position);
        return this.mesh;
    }

    @Override
    public int getResolution() {
        return resolution;
    }

    @Override
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public static Vec3f getRandomColor(float seed) {
        return new Vec3f(
                SimplexNoise.noise(seed * 48.52456f, seed) * 0.5f + 0.5f,
                SimplexNoise.noise(seed * 33.67325f, seed) * 0.5f + 0.5f,
                SimplexNoise.noise(seed * 67.63267f, seed) * 0.5f + 0.5f
        );
    }
}
