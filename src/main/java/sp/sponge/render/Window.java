package sp.sponge.render;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import sp.sponge.input.Input;

public class Window {
    private static Window mainWindow;

    private final ImGuiImplGlfw implGlfw;
    private final ImGuiImplGl3 implGl3;
    private long handle;

    private String glslVersion;

    private int width;
    private int height;

    private Input input;

    public static Window getWindow() {
        if (mainWindow == null) {
            mainWindow = new Window();
            mainWindow.init();
        }

        return mainWindow;
    }

    private Window() {
        this.implGlfw = new ImGuiImplGlfw();
        this.implGl3 = new ImGuiImplGl3();
    }

    public void startImGuiFrame() {
        implGlfw.newFrame();
        implGl3.newFrame();
        ImGui.newFrame();
    }

    public void endImGuiFrame() {
        ImGui.render();
        implGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupCurrentContext = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupCurrentContext);
        }

        GLFW.glfwSwapBuffers(this.handle);
        GLFW.glfwPollEvents();
    }

    private void init() {
        initWindow();
        initImGui();
    }

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        glslVersion = "#version 330 core";
        GLFW.glfwDefaultWindowHints();
        // Version 3.3
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        this.width = 1920;
        this.height = 1080;
        this.handle = GLFW.glfwCreateWindow(this.width, this.height, "Sponge", 0L, 0L);

        if (this.handle == 0L) {
            throw new IllegalStateException("Failed to create window");
        }

        GLFW.glfwMakeContextCurrent(this.handle);
        // 0 -> Vsync Off
        // 1 -> Vsync On
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(this.handle);
        GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChange);

        this.input = new Input();
        this.input.setup(this);


        //Open GL stuff
        GL.createCapabilities();
        OpenGLSystem.enableDepthTest();
    }

    private void onWindowSizeChange(long handle, int width, int height) {
        GL11.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    private void initImGui() {
        ImGui.createContext();
        implGlfw.init(this.handle, true);
        implGl3.init(glslVersion);
    }

    public Input getInput() {
        return input;
    }

    public long getHandle() {
        return this.handle;
    }

    public boolean isRunning() {
        return !GLFW.glfwWindowShouldClose(this.handle);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
