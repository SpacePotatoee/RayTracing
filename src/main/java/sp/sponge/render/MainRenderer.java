package sp.sponge.render;

import imgui.ImGui;
import org.lwjgl.opengl.GL11;

public class MainRenderer {
    private long updateTime;
    private int fpsCounter = 0;
    private String currentFpsString = "";

    public MainRenderer () {
        updateTime = System.currentTimeMillis();
    }

    public void renderScene() {
        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

    }

    public void renderImGui() {

        fpsCounter++;
        if (System.currentTimeMillis() >=  updateTime + 1000L) {
            this.currentFpsString = "FPS: " + fpsCounter;
            fpsCounter = 0;
            updateTime = System.currentTimeMillis();
        }

        ImGui.text(this.currentFpsString);
    }

}
