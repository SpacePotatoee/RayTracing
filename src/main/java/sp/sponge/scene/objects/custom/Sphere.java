package sp.sponge.scene.objects.custom;

import sp.sponge.render.vulkan.model.Mesh;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.Objects;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.util.objects.Transformation;

public class Sphere extends SceneObject implements ObjectWithResolution {
    private int resolution = 1;

    public Sphere(boolean fixed) {
        super(Objects.SPHERE, fixed);
    }

    public Sphere(Transformation transformation, boolean fixed) {
        super(Objects.SPHERE, transformation, fixed);
    }

    @Override
    public Mesh createMesh() {
        Mesh mesh = new Mesh(20);
        double radius = 0.5f;

        double phi = 1.618033988749; //Golden ratio
        float a = 1.0f;
        float c = (float) phi;


        Mesh.Vertex v0 = new Mesh.Vertex(0.0f, c, a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v1 = new Mesh.Vertex(-a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v2 = new Mesh.Vertex(a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v0, v1, v2);


        Mesh.Vertex v3 = new Mesh.Vertex(0.0f, c, a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v4 = new Mesh.Vertex(c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v5 = new Mesh.Vertex(a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v3, v4, v5);


        Mesh.Vertex v6 = new Mesh.Vertex(0.0f, c, a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v7 = new Mesh.Vertex(c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v8 = new Mesh.Vertex(0.0f, c, -a, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v6, v7, v8);


        Mesh.Vertex v9 = new Mesh.Vertex(0.0f, c, -a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v10 = new Mesh.Vertex(c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v11 = new Mesh.Vertex(a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v9, v10, v11);


        Mesh.Vertex v12 = new Mesh.Vertex(0.0f, c, -a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v13 = new Mesh.Vertex(a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v14 = new Mesh.Vertex(-a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v12, v13, v14);


        Mesh.Vertex v15 = new Mesh.Vertex(0.0f, c, -a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v16 = new Mesh.Vertex(-a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v17 = new Mesh.Vertex(-c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v15, v16, v17);


        Mesh.Vertex v18 = new Mesh.Vertex(0.0f, c, -a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v19 = new Mesh.Vertex(-c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v20 = new Mesh.Vertex(0.0f, c, a, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v18, v19, v20);


        Mesh.Vertex v21 = new Mesh.Vertex(0.0f, c, a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v22 = new Mesh.Vertex(-c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v23 = new Mesh.Vertex(-a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v21, v22, v23);


        Mesh.Vertex v24 = new Mesh.Vertex(-c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v25 = new Mesh.Vertex(-a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v26 = new Mesh.Vertex(-c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v24, v25, v26);


        Mesh.Vertex v27 = new Mesh.Vertex(-a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v28 = new Mesh.Vertex(-c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v29 = new Mesh.Vertex(0.0f, -c, a, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v27, v28, v29);


        Mesh.Vertex v30 = new Mesh.Vertex(-a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v31 = new Mesh.Vertex(0.0f, -c, a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v32 = new Mesh.Vertex(a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v30, v31, v32);


        Mesh.Vertex v33 = new Mesh.Vertex(0.0f, -c, a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v34 = new Mesh.Vertex(a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v35 = new Mesh.Vertex(c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v33, v34, v35);


        Mesh.Vertex v36 = new Mesh.Vertex(c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v37 = new Mesh.Vertex(a, 0.0f, c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v38 = new Mesh.Vertex(c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v36, v37, v38);


        Mesh.Vertex v39 = new Mesh.Vertex(c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v40 = new Mesh.Vertex(c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v41 = new Mesh.Vertex(a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v39, v40, v41);


        Mesh.Vertex v42 = new Mesh.Vertex(a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v43 = new Mesh.Vertex(c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v44 = new Mesh.Vertex(0.0f, -c, -a, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v42, v43, v44);


        Mesh.Vertex v45 = new Mesh.Vertex(a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v46 = new Mesh.Vertex(0.0f, -c, -a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v47 = new Mesh.Vertex(-a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v45, v46, v47);


        Mesh.Vertex v48 = new Mesh.Vertex(-a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v49 = new Mesh.Vertex(0.0f, -c, -a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v50 = new Mesh.Vertex(-c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v48, v49, v50);


        Mesh.Vertex v51 = new Mesh.Vertex(-a, 0.0f, -c, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v52 = new Mesh.Vertex(-c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v53 = new Mesh.Vertex(-c, a, 0.0f, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v51, v52, v53);


        Mesh.Vertex v54 = new Mesh.Vertex(-c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v55 = new Mesh.Vertex(0.0f, -c, -a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v56 = new Mesh.Vertex(0.0f, -c, a, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v54, v55, v56);


        Mesh.Vertex v57 = new Mesh.Vertex(c, -a, 0.0f, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v58 = new Mesh.Vertex(0.0f, -c, a, 0.0f, 0.0f, 1.0f);
        Mesh.Vertex v59 = new Mesh.Vertex(0.0f, -c, -a, 0.0f, 0.0f, 1.0f);
        mesh.addFace(v57, v58, v59);

        return mesh;
    }

    @Override
    public int getResolution() {
        return resolution;
    }

    @Override
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }
}
