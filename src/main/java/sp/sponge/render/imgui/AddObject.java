package sp.sponge.render.imgui;

import imgui.ImGui;
import org.joml.Vector3f;
import sp.sponge.scene.SceneManager;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.registries.Registries;
import sp.sponge.scene.registries.custom.object.ObjectType;

public class AddObject {
    private static String objectName;
    private static ObjectType<?> addedObject;

    public static void render() {
        ImGui.setWindowFontScale(1.0f);
        if (ImGui.collapsingHeader("Add an object")) {

            if (ImGui.beginCombo("object", objectName)) {
                for (ObjectType<?> object1 : Registries.SceneObjectRegistry) {
                    if (ImGui.selectable(object1.getName())) {
                        objectName = object1.getName();
                        addedObject = object1;
                    }
                }

                ImGui.endCombo();
            }

            if (ImGui.button("Add Object") && addedObject != null) {
                SceneObject newObject = addedObject.create(new Vector3f(0, 0, 0), false);

                SceneManager.addObject(newObject);
            }
        }

        if (ImGui.collapsingHeader("Objects")) {
            if (ImGui.button("Remove all objects")) {
                SceneManager.clear();
            }
            for (SceneObject object : SceneManager.getSceneObjects()) {
                if(ImGui.treeNode(object.toString())) {
                    float[] positionX = new float[]{object.getX()};
                    float[] positionY = new float[]{object.getY()};
                    float[] positionZ = new float[]{object.getZ()};

                    ImGui.dragFloat("X", positionX, 0.01f);
                    ImGui.dragFloat("Y", positionY, 0.01f);
                    ImGui.dragFloat("Z", positionZ, 0.01f);

                    object.setPosition(new Vector3f(positionX[0], positionY[0], positionZ[0]));

                    if (object instanceof ObjectWithResolution objectWithResolution) {
                        int[] resolution = new int[]{objectWithResolution.getResolution()};
                        ImGui.sliderInt("Resolution", resolution, 4, 50);
                        objectWithResolution.setResolution(resolution[0]);
                    }

                    if(ImGui.button("Remove Object")) {
                        SceneManager.remove(object);
                    }
                    ImGui.treePop();
                }
            }
        }
    }

}
