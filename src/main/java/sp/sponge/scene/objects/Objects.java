package sp.sponge.scene.objects;

import sp.sponge.Sponge;
import sp.sponge.scene.objects.custom.*;
import sp.sponge.scene.registries.Registries;
import sp.sponge.scene.registries.custom.object.ObjectType;

public class Objects {

    private static final ObjectType<SceneObject> SQUARE = Registries.SceneObjectRegistry.register(
            Registries.SceneObjectRegistry,
            new ObjectType<>(Square.class, "square", Square::new)
    );

    private static final ObjectType<SceneObject> CIRCLE = Registries.SceneObjectRegistry.register(
            Registries.SceneObjectRegistry,
            new ObjectType<>(Circle.class, "circle", Circle::new)
    );

    private static final ObjectType<SceneObject> SPHERE = Registries.SceneObjectRegistry.register(
            Registries.SceneObjectRegistry,
            new ObjectType<>(Sphere.class, "sphere", Sphere::new)
    );

    private static final ObjectType<SceneObject> CUBE = Registries.SceneObjectRegistry.register(
            Registries.SceneObjectRegistry,
            new ObjectType<>(Cube.class, "cube", Cube::new)
    );

    private static final ObjectType<SceneObject> DRAGON = Registries.SceneObjectRegistry.register(
            Registries.SceneObjectRegistry,
            new ObjectType<>(Dragon.class, "dragon", Dragon::new)
    );

    public static void registerObjects() {
        Sponge.getInstance().getLogger().info("Registering Objects");
    }

}
