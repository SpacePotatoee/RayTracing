package sp.sponge.scene.objects;

import sp.sponge.Sponge;
import sp.sponge.scene.objects.custom.*;
import sp.sponge.scene.registries.Registries;
import sp.sponge.scene.registries.custom.object.ObjectType;

public class Objects {

    public static final ObjectType<SceneObject> SQUARE = Registries.SceneObjectRegistry.register(
            new ObjectType<>(Square.class, "square", Square::new)
    );

    public static final ObjectType<SceneObject> CIRCLE = Registries.SceneObjectRegistry.register(
            new ObjectType<>(Circle.class, "circle", Circle::new)
    );

    public static void registerObjects() {
        Sponge.getInstance().getLogger().info("Registering Objects");
    }

}
