package sp.sponge.scene;

import com.google.gson.*;
import sp.sponge.Sponge;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.registries.Registries;
import sp.sponge.scene.registries.custom.object.ObjectType;
import sp.sponge.util.objects.Material;
import sp.sponge.util.objects.Transformation;

import java.io.*;
import java.util.Vector;

public class SceneManager {
    private static Vector<SceneObject> sceneObjects = new Vector<>();

    public static Vector<SceneObject> getSceneObjects() {
        return (Vector<SceneObject>) sceneObjects.clone();
    }

    public static void clear() {
        sceneObjects.removeIf(sceneObject -> !sceneObject.isFixed());
    }

    public static void addObject(SceneObject object) {
        sceneObjects.add(object);
    }

    public static void remove(SceneObject object) {
        sceneObjects.remove(object);
    }
}
