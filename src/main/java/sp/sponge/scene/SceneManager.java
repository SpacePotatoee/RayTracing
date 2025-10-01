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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void saveScene(String name) {
        File file = new File(Sponge.getInstance().getRunFiles().scenesFile(), name + ".json");

        try {
            file.createNewFile();
            try (FileWriter fileWriter = new FileWriter(file)) {
                Vector<SceneObject> vector = getSceneObjects();
                vector.remove(Sponge.getInstance().renderer.getGroundPlane());
                GSON.toJson(vector, fileWriter);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadScene(String path) {
        File file = new File(Sponge.getInstance().getRunFiles().scenesFile(), path);

        if (file.exists()) {
            try {
                try (FileReader reader = new FileReader(file)) {
                    Vector<SceneObject> newSceneObjects = new Vector<>();
                    JsonElement objectArray = JsonParser.parseReader(reader);
                    for (JsonElement object : objectArray.getAsJsonArray()) {
                        JsonObject jsonObject = object.getAsJsonObject();
                        String name = jsonObject.get("name").getAsString();
                        Transformation transformation = GSON.fromJson(jsonObject.get("transformation"), Transformation.class);
                        Material material = GSON.fromJson(jsonObject.get("material"), Material.class);

                        ObjectType<? extends SceneObject> objectType = Registries.SceneObjectRegistry.get(name);

                        SceneObject sceneObject = objectType.create(transformation, false);
                        sceneObject.getMaterial().copyValues(material);
                        newSceneObjects.add(sceneObject);
                    }
                    sceneObjects = new Vector<>(newSceneObjects);
                    sceneObjects.add(Sponge.getInstance().renderer.getGroundPlane());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

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
