package sp.sponge.scene.registries;

import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.registries.custom.object.ObjectRegistry;
import sp.sponge.scene.registries.custom.object.ObjectType;

public class Registries {

    public static Registry<ObjectType<SceneObject>> SceneObjectRegistry = new ObjectRegistry<>();

}
