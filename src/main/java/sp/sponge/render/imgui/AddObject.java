package sp.sponge.render.imgui;

import imgui.ImGui;
import org.joml.Vector3d;
import sp.sponge.scene.SceneManager;
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

            if (ImGui.button("Add Object")) {
                SceneObject newObject = addedObject.create(new Vector3d(0, 0, 0), false);

                SceneManager.addObject(newObject);
            }
        }

        if (ImGui.collapsingHeader("Objects")) {
            for (SceneObject object : SceneManager.getSceneObjects()) {
                if(ImGui.treeNode(object.toString())) {
                    if(ImGui.button("Remove Object")) {
                        SceneManager.remove(object);
                    }
                    ImGui.treePop();
                }
            }
//            if (ImGui.treeNode("Test1")) {
//                ImGui.button("test2");
//                ImGui.treePop();
//            }
        }
    }

}
