package sp.sponge.scene.objects;

import sp.sponge.render.Mesh;
import sp.sponge.scene.registries.custom.object.ObjectType;
import sp.sponge.util.tools.ObjParser;
import sp.sponge.util.objects.Transformation;

public class AbstractObjObject extends SceneObject {
    private final String path;

    public AbstractObjObject(ObjectType<SceneObject> objectType, boolean fixed, String objPath) {
        super(objectType, fixed);
        this.path = objPath;
    }

    public AbstractObjObject(ObjectType<SceneObject> objectType, Transformation transformation, boolean fixed, String objPath) {
        super(objectType, transformation, fixed);
        this.path = objPath;
    }

    @Override
    public Mesh createMesh() {
        return ObjParser.objToMesh(this.path);
    }
}
