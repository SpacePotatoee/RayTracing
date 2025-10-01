package sp.sponge.render;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTabBarFlags;
import imgui.type.ImString;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import sp.sponge.Sponge;
import sp.sponge.render.imgui.AddObject;
import sp.sponge.render.shader.ShaderProgram;
import sp.sponge.render.shader.ShaderRegistry;
import sp.sponge.scene.SceneManager;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.scene.objects.custom.Square;
import sp.sponge.util.objects.Transformation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class MainRenderer {
    private long updateTime;
    private int fpsCounter = 0;
    private String currentFpsString = "";
    public final VertexBuffer mainVertexBuffer;
    private final VertexBuffer screenBuffer;
    private final Window window;
    private final Camera camera;
    private final FrameBuffer postFramebuffer;
    private FrameBuffer prevFrameBuffer;
    private final List<File> savedFiles = new ArrayList<>();

    private Square groundPlane;

    private int numOfRenderedFrames;

    public MainRenderer () {
        updateTime = System.currentTimeMillis();
        this.mainVertexBuffer = new VertexBuffer(100000000, VertexBuffer.VertexDataType.POSITION_COLOR_NORMAL, true);
        this.screenBuffer = new VertexBuffer(168, VertexBuffer.VertexDataType.POSITION_TEXTURE, false);
        this.window = Window.getWindow();
        this.camera = new Camera();
        this.postFramebuffer = new FrameBuffer(this.window.getWidth(), this.window.getHeight());
        this.prevFrameBuffer = new FrameBuffer(this.window.getWidth(), this.window.getHeight());
    }

    public void renderScene() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        this.updateGroundPlane();

        camera.updateCamera();

        Vector<SceneObject> sceneObjects = SceneManager.getSceneObjects();
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
        for (SceneObject object : sceneObjects) {
            Mesh mesh = object.getMesh();
            if (mesh != null) {
                this.mainVertexBuffer.addMesh(object.getTransformMatrix(), mesh);
            }
        }
//
//        this.postFramebuffer.bind();
//        OpenGLSystem.enableDepthTest();
//        this.mainVertexBuffer.bind();
//        ShaderRegistry.defaultShader.bind();
        ShaderRegistry.defaultShader.setMatrices(this.camera.getModelViewMatrix(), this.camera.getProjectionMatrix());
//
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//        this.mainVertexBuffer.drawElements();
//        FrameBuffer.unbind();

//        ShaderProgram.unbind();
//        VertexBuffer.unbind();

        if (!this.camera.hasMoved()) {
            numOfRenderedFrames++;
        } else {
            numOfRenderedFrames = 0;
        }

        this.screenBuffer.vertex(1.0f, 1.0f, 0.0f).texture(1.0f, 1.0f).next();
        this.screenBuffer.vertex(-1.0f, 1.0f, 0.0f).texture(0.0f, 1.0f).next();
        this.screenBuffer.vertex(-1.0f, -1.0f, 0.0f).texture(0.0f, 0.0f).next();

        this.screenBuffer.vertex(1.0f, 1.0f, 0.0f).texture(1.0f, 1.0f).next();
        this.screenBuffer.vertex(-1.0f, -1.0f, 0.0f).texture(0.0f, 0.0f).next();
        this.screenBuffer.vertex(1.0f, -1.0f, 0.0f).texture(1.0f, 0.0f).next();

        OpenGLSystem.disableDepthTest();

        ShaderRegistry.postShader.bind();
        this.screenBuffer.bind();
        VertexBuffer buffer = Sponge.getInstance().renderer.mainVertexBuffer;
        buffer.bindRayTracingBuffers();
        ShaderRegistry.postShader.setInt("Frame", numOfRenderedFrames);
        ShaderRegistry.postShader.setInt("NumOfMeshes", buffer.getNumOfMeshes());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.prevFrameBuffer.getColorTexture());
        ShaderRegistry.postShader.bindTexture("PrevSampler", 0);

        this.screenBuffer.drawElements();

        ShaderProgram.unbind();
        VertexBuffer.unbind();
        buffer.endRayTRacing();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);


        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.prevFrameBuffer.getFrameBuffer());
        GL30.glBlitFramebuffer(0, 0, this.window.getWidth(), this.window.getHeight(),
                                0, 0, this.window.getWidth(), this.window.getHeight(),
                                GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
//        this.postFramebuffer.draw();




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
                    boolean savePopup = ImGui.button("Save Scene");

                    if (savePopup) {
                        ImGui.openPopup("SaveName");
                    }

                    if (ImGui.beginPopup("SaveName")) {
                        ImString filePath = new ImString();
                        if (ImGui.inputText("File Name", filePath, ImGuiInputTextFlags.EnterReturnsTrue)) {
                            SceneManager.saveScene(filePath.get());
                            ImGui.closeCurrentPopup();
                        }
                        ImGui.endPopup();
                    }

                    ImGui.sameLine();

                    boolean loadPopup = ImGui.button("Load Scene");


                    if (loadPopup) {
                        ImGui.openPopup("LoadName");
                        savedFiles.clear();

                        File scenes = Sponge.getInstance().getRunFiles().scenesFile();
                        if (scenes.exists() && scenes.isDirectory()) {
                            savedFiles.addAll(Arrays.asList(scenes.listFiles()));
                        }

                    }

                    if (ImGui.beginPopup("LoadName")) {
                        boolean selected = false;
                        if (ImGui.beginCombo("File", "")) {
                            for (File file : savedFiles) {
                                if (ImGui.selectable(file.getName())) {

                                    SceneManager.loadScene(file.getName());
                                    selected = true;
                                    break;
                                }
                            }
                            ImGui.endCombo();
                        }

                        if (selected) {
                            ImGui.closeCurrentPopup();
                        }
                        ImGui.endPopup();
                    }

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
            this.groundPlane.getMaterial().setColor(0.36078431372f, 0.50588235294f, 0.61960784313f);
            SceneManager.addObject(this.groundPlane);
        }


//        Vector3f cameraPos = this.camera.getPosition();
//        this.groundPlane.setPosition(0, 0, 0);
    }

    public Square getGroundPlane() {
        return this.groundPlane;
    }

    public Camera getCamera() {
        return camera;
    }
}
