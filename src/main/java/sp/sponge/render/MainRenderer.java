package sp.sponge.render;

import imgui.ImGui;
import imgui.type.ImDouble;
import org.lwjgl.opengl.GL11;
import sp.sponge.render.imgui.AddObject;
import sp.sponge.scene.SceneManager;
import sp.sponge.scene.objects.SceneObject;

import java.util.Vector;

public class MainRenderer {
    private long updateTime;
    private int fpsCounter = 0;
    private String currentFpsString = "";

    public MainRenderer () {
        updateTime = System.currentTimeMillis();
    }

    public void renderScene() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        Vector<SceneObject> sceneObjects = SceneManager.getSceneObjects();

        for (SceneObject object : SceneManager.getSceneObjects()) {
            object.render();
        }


    }

    public void renderImGui() {

        fpsCounter++;
        if (System.currentTimeMillis() >=  updateTime + 1000L) {
            this.currentFpsString = "FPS: " + fpsCounter;
            fpsCounter = 0;
            updateTime = System.currentTimeMillis();
        }

        ImGui.text(this.currentFpsString);

        AddObject.render();
    }

}
