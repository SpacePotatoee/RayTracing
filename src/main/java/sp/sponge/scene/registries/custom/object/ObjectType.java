package sp.sponge.scene.registries.custom.object;

import sp.sponge.scene.objects.SceneObject;
import sp.sponge.util.Transformation;

public class ObjectType<T extends SceneObject> {
    private final String name;
    private final CreateObject<T> createObjectMethod;

    public ObjectType(Class<? extends SceneObject> objectClass, String name, CreateObject<T> createObject) {
        this.name = name;
        this.createObjectMethod = createObject;
    }

    public String getName() {
        return this.name;
    }

    public T create(Transformation transformation, boolean fixed) {
        return createObjectMethod.create(transformation, fixed);
    }

    public interface CreateObject<T extends SceneObject> {
        T create(Transformation transformation, boolean fixed);
    }
}
