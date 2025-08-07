package sp.sponge.render;

import imgui.ImGui;
import org.lwjgl.opengl.GL11;
import sp.sponge.render.imgui.AddObject;
import sp.sponge.render.shader.ShaderProgram;
import sp.sponge.render.shader.ShaderRegistry;
import sp.sponge.scene.SceneManager;
import sp.sponge.scene.objects.SceneObject;

import java.util.Vector;

public class MainRenderer {
    private long updateTime;
    private int fpsCounter = 0;
    private String currentFpsString = "";
    private final VertexBuffer mainVertexBuffer;

    public MainRenderer () {
        updateTime = System.currentTimeMillis();
        this.mainVertexBuffer = new VertexBuffer(10000);
    }

    public void renderScene() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        Vector<SceneObject> sceneObjects = SceneManager.getSceneObjects();


        for (SceneObject object : sceneObjects) {
            object.render(this.mainVertexBuffer);
        }

        this.mainVertexBuffer.bind();
        ShaderRegistry.defaultShader.bind();

        this.mainVertexBuffer.drawElements();
        this.mainVertexBuffer.end();

        ShaderProgram.unbind();
        VertexBuffer.unbind();


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
