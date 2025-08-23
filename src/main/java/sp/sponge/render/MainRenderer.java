package sp.sponge.render;

import imgui.ImGui;
import imgui.flag.ImGuiTabBarFlags;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import sp.sponge.Sponge;
import sp.sponge.render.imgui.AddObject;
import sp.sponge.render.shader.ShaderProgram;
import sp.sponge.render.shader.ShaderRegistry;
import sp.sponge.scene.SceneManager;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.objects.custom.Square;
import sp.sponge.util.Transformation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class MainRenderer {
    private long updateTime;
    private int fpsCounter = 0;
    private String currentFpsString = "";
    public final VertexBuffer mainVertexBuffer;
    private final Window window;
    private final Camera camera;
    private final FrameBuffer postFramebuffer;

//    private boolean initGroundPlane;
    private Square groundPlane;

    public MainRenderer () {
        updateTime = System.currentTimeMillis();
        this.mainVertexBuffer = new VertexBuffer(100000000, VertexBuffer.VertexDataType.POSITION_COLOR_NORMAL, true);
        this.window = Window.getWindow();
        this.camera = new Camera();
        this.postFramebuffer = new FrameBuffer(this.window.getWidth(), this.window.getHeight());
    }

    public void renderScene() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        this.updateGroundPlane();

        camera.updateCamera();

        Vector<SceneObject> sceneObjects = SceneManager.getSceneObjects();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        for (SceneObject object : sceneObjects) {
            Mesh mesh = object.getMesh();
            if (mesh != null) {
                mesh.drawMesh(this.mainVertexBuffer);
            }
        }

        this.postFramebuffer.bind();
        OpenGLSystem.enableDepthTest();
        this.mainVertexBuffer.bind();
        ShaderRegistry.defaultShader.bind();
        ShaderRegistry.defaultShader.setMatrices(this.camera.getModelViewMatrix(), this.camera.getProjectionMatrix());

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        this.mainVertexBuffer.drawElements();
        FrameBuffer.unbind();

//        ShaderProgram.unbind();
//        VertexBuffer.unbind();


        this.postFramebuffer.draw();




        ShaderProgram.shaders.forEach((shaderProgram, files) -> {
            long lastModified = -1L;
            for (File file : files) {
                lastModified = Math.max(lastModified, file.lastModified());
            }

            if (lastModified != shaderProgram.lastUpdateTime) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(files[0]);
                    String vertexShaderText = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);

                    fileInputStream = new FileInputStream(files[1]);
                    String fragmentShaderText = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
                    shaderProgram.reCompile(vertexShaderText, fragmentShaderText);
                    shaderProgram.lastUpdateTime = lastModified;
                    Sponge.getInstance().getLogger().info("Hot-swapping shaders");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void renderImGui() {
        if (ImGui.begin("MAIN")) {
            fpsCounter++;
            if (System.currentTimeMillis() >= updateTime + 1000L) {
                this.currentFpsString = "FPS: " + fpsCounter;
                fpsCounter = 0;
                updateTime = System.currentTimeMillis();
            }

            ImGui.text(this.currentFpsString);
            ImGui.spacing();

            if (ImGui.beginTabBar("main", ImGuiTabBarFlags.None)) {
                if (ImGui.beginTabItem("Scene")) {
                    AddObject.render();
                    ImGui.endTabItem();
                }

                if (ImGui.beginTabItem("Camera")) {
                    this.camera.renderImGui();
                    ImGui.endTabItem();
                }
                ImGui.endTabBar();
            }

            ImGui.end();
        }

    }

    private void updateGroundPlane() {
        if (this.groundPlane == null) {
            this.groundPlane = new Square(0, 0, 0, true);
            Transformation transformation = this.groundPlane.getTransformations();

            transformation.rotate(-90f, 0, 0);
            transformation.scale(10f);
            this.groundPlane.setColor(0.36078431372f, 0.50588235294f, 0.61960784313f);
            SceneManager.addObject(this.groundPlane);
        }


//        Vector3f cameraPos = this.camera.getPosition();
//        this.groundPlane.setPosition(0, 0, 0);
    }

    public Camera getCamera() {
        return camera;
    }
}
