package sp.sponge.scene.objects;

import org.joml.Vector3f;
import sp.sponge.render.Mesh;
import sp.sponge.util.ObjParser;

public abstract class AbstractObjObject extends SceneObject {
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
    public Mesh createMesh() {
        return ObjParser.objToMesh(this.path);
    }
}
