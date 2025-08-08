package sp.sponge.render;

import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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
    private final Window window;

    private float rotation;

    public MainRenderer () {
        updateTime = System.currentTimeMillis();
        this.mainVertexBuffer = new VertexBuffer(10000);
        this.window = Window.getWindow();
    }

    public void renderScene() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        Vector<SceneObject> sceneObjects = SceneManager.getSceneObjects();


        for (SceneObject object : sceneObjects) {
            object.render(this.mainVertexBuffer);
        }

        Matrix4f model = new Matrix4f().identity();
        Matrix4f view = new Matrix4f().identity();
        Matrix4f proj = new Matrix4f();

//        proj.perspective((float) Math.toRadians(175.0), (float) window.getWidth() / window.getHeight(), 0.1f, 100.0f);
        view.rotate(new Quaternionf().rotateXYZ((float) Math.toRadians(rotation),(float) Math.toRadians(rotation), (float) Math.toRadians(rotation)));
//        view.translate(0.0f, 0.0f, 0.5f);
        rotation++;



        this.mainVertexBuffer.bind();
        ShaderRegistry.defaultShader.bind();
        ShaderRegistry.defaultShader.setMatrices(model, view, proj);

        this.mainVertexBuffer.drawElements();

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
