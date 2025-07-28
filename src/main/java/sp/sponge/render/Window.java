package sp.sponge.render;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Window {
    private static Window mainWindow;

    private final ImGuiImplGlfw implGlfw;
    private final ImGuiImplGl3 implGl3;
    private long handle;

    private String glslVersion;

    private int width;
    private int height;

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

        this.handle = GLFW.glfwCreateWindow(1920, 1080, "Sponge", 0L, 0L);
        if (this.handle == 0L) {
            throw new IllegalStateException("Failed to create window");
        }

        GLFW.glfwMakeContextCurrent(this.handle);
        // 0 -> Vsync Off
        // 1 -> Vsync On
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(this.handle);

        GL.createCapabilities();

        GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChange);
//        GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeChange);
    }

    private void onWindowSizeChange(long handle, int width, int height) {
        GL11.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    private void onFramebufferSizeChange(long handle, int width, int height) {
        System.out.println("WORKED");
        this.width = width;
        this.height = height;
    }

    private void initImGui() {
        ImGui.createContext();
        implGlfw.init(this.handle, true);
        implGl3.init(glslVersion);
    }

    public long getHandle() {
        return this.handle;
    }

    public boolean isRunning() {
        return !GLFW.glfwWindowShouldClose(this.handle);
    }
}
