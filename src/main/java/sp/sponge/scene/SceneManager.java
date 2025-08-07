package sp.sponge.scene;

import sp.sponge.scene.objects.SceneObject;

import java.util.Vector;

public class SceneManager {
    private static final Vector<SceneObject> sceneObjects = new Vector<>();

    public static Vector<SceneObject> getSceneObjects() {
        return (Vector<SceneObject>) sceneObjects.clone();
    }

    public static void clear() {
        sceneObjects.clear();
    }

    public static void addObject(SceneObject object) {
        sceneObjects.add(object);
    }

    public static void remove(SceneObject object) {
        sceneObjects.remove(object);
    }
}
