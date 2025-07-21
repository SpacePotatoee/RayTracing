package sp.sponge;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

public class Window {
    private static Window mainWindow;
    private int width;
    private int height;
    private String title;
    private long handle;


    public static Window getWindow() {
        if (mainWindow == null) {
            mainWindow = new Window();
            mainWindow.initWindow();
        }

        return mainWindow;
    }

    Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Sponge";
    }

    public void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to Initialize GLFW");
        }

        Main.SPONGE_LOGGER.info("Initialized GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);

        this.handle = GLFW.glfwCreateWindow(this.width, this.height, this.title, 0L, 0L);
        if (this.handle == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        GLFW.glfwMakeContextCurrent(this.handle);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(this.handle);
        GL.createCapabilities();
    }

    public boolean isRunning() {
        return !GLFW.glfwWindowShouldClose(this.handle);
    }

    public long getHandle() {
        return this.handle;
    }
}
