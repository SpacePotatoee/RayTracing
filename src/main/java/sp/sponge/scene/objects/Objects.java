package sp.sponge.scene.objects;

import sp.sponge.Sponge;
import sp.sponge.scene.objects.custom.*;
import sp.sponge.scene.objects.custom.obj.Bunny;
import sp.sponge.scene.objects.custom.obj.Cube;
import sp.sponge.scene.objects.custom.obj.Dragon;
import sp.sponge.scene.registries.Registries;
import sp.sponge.scene.registries.custom.object.ObjectType;

public class Objects {

    public static final ObjectType<SceneObject> SQUARE = Registries.SceneObjectRegistry.register(
            new ObjectType<>(Square.class, "square", Square::new)
    );

    public static final ObjectType<SceneObject> CIRCLE = Registries.SceneObjectRegistry.register(
            new ObjectType<>(Circle.class, "circle", Circle::new)
    );

    public static final ObjectType<SceneObject> SPHERE = Registries.SceneObjectRegistry.register(
            new ObjectType<>(Sphere.class, "sphere", Sphere::new)
    );

    public static final ObjectType<SceneObject> CUBE = Registries.SceneObjectRegistry.register(
            new ObjectType<>(Cube.class, "cube", Cube::new)
    );

    public static final ObjectType<SceneObject> BUNNY = Registries.SceneObjectRegistry.register(
            new ObjectType<>(AbstractObjObject.class, "bunny", Bunny::new)
    );

    public static final ObjectType<SceneObject> DRAGON = Registries.SceneObjectRegistry.register(
            new ObjectType<>(Dragon.class, "dragon", Dragon::new)
    );

    public static final ObjectType<SceneObject> SPONZA = Registries.SceneObjectRegistry.register(
            new ObjectType<>(Dragon.class, "sponza1", Dragon::new)
    );

    public static void registerObjects() {
        Sponge.getInstance().getLogger().info("Registering Objects");
    }

}
