package sp.sponge.scene.objects.custom;

import org.joml.Vector3f;
import sp.sponge.render.Mesh;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.util.ObjParser;

public abstract class AbstractObjObject extends SceneObject {
    private Mesh mesh;
    private final String path;

    public AbstractObjObject(float x, float y, float z, boolean fixed, String objPath) {
        super(x, y, z, fixed);
        this.path = objPath;
    }

    public AbstractObjObject(Vector3f position, boolean fixed, String objPath) {
        super(position, fixed);
        this.path = objPath;
    }

    @Override
    public Mesh getMesh() {
        if (this.mesh == null || this.isDirty()) {
            this.mesh = ObjParser.objToMesh(this.path);
            this.clean();
        }
        return this.mesh;
    }
}
