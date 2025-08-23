package sp.sponge.render.imgui;

import imgui.ImGui;
import org.joml.Vector3f;
import sp.sponge.scene.SceneManager;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.registries.Registries;
import sp.sponge.scene.registries.custom.object.ObjectType;
import sp.sponge.util.Transformation;

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
                    if (!object.isFixed()) {
                        Transformation transformations = object.getTransformations();
                        Vector3f position = transformations.getPosition();
                        float[] positionX = new float[]{position.x};
                        float[] positionY = new float[]{position.y};
                        float[] positionZ = new float[]{position.z};

                        ImGui.dragFloat("X Position", positionX, 0.01f);
                        ImGui.dragFloat("Y Position", positionY, 0.01f);
                        ImGui.dragFloat("Z Position", positionZ, 0.01f);

                        transformations.setPosition(positionX[0], positionY[0], positionZ[0]);

                        ImGui.spacing();
                        Vector3f scale = transformations.getScale();
                        float[] sizeX = new float[]{scale.x};
                        float[] sizeY = new float[]{scale.y};
                        float[] sizeZ = new float[]{scale.z};

                        ImGui.dragFloat("X Size", sizeX, 0.01f);
                        ImGui.dragFloat("Y Size", sizeY, 0.01f);
                        ImGui.dragFloat("Z Size", sizeZ, 0.01f);

                        transformations.scale(sizeX[0], sizeY[0], sizeZ[0]);
                    }

                    float[] color = new float[]{object.getColor().x, object.getColor().y, object.getColor().z};

                    ImGui.colorPicker3("Color", color);
                    object.setColor(color[0], color[1], color[2]);

                    if (object instanceof ObjectWithResolution objectWithResolution) {
                        int[] resolution = new int[]{objectWithResolution.getResolution()};
                        ImGui.sliderInt("Resolution", resolution, 4, 50);
                        if (resolution[0] != objectWithResolution.getResolution()) {
                            objectWithResolution.setResolution(resolution[0]);
                            object.markDirty();
                        }
                    }

                    if(!object.isFixed() && ImGui.button("Remove Object")) {
                        SceneManager.remove(object);
                    }
                    ImGui.treePop();
                }
            }
        }
    }

}
