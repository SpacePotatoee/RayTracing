package sp.sponge.scene.registries.custom.object;

import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.registries.Registry;

public class ObjectRegistry<T extends SceneObject> extends Registry<ObjectType<T>> {

    @Override
    public ObjectType<T> register(ObjectType<T> entry) {
        this.registryList.add(entry);
        return entry;
    }

    @Override
    public ObjectType<T> get(String name) {
        return this.registryList.stream()
                .filter(tObjectType -> tObjectType.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
