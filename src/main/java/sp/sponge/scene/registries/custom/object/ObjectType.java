package sp.sponge.scene.registries.custom.object;

import org.joml.Vector3f;
import sp.sponge.scene.objects.SceneObject;

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

    public T create(Vector3f position, boolean fixed) {
        return createObjectMethod.create(position, fixed);
    }

    public interface CreateObject<T extends SceneObject> {
        T create(Vector3f position, boolean fixed);
    }
}
