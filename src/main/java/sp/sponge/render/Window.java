package sp.sponge.render;

import org.lwjgl.glfw.GLFWVulkan;

import static org.lwjgl.glfw.GLFW.*;

public class Window implements AutoCloseable {
    private static Window INSTANCE;
    private long handle;
    private int width;
    private int height;

    public static Window getWindow() {
        if (INSTANCE == null) {
            INSTANCE = new Window();
        }

        return INSTANCE;
    }

    private Window() {
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        if (!GLFWVulkan.glfwVulkanSupported()) {
            throw new RuntimeException("Vulkan is not supported on your hardware");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.width = 1920;
        this.height = 1080;
        this.handle = glfwCreateWindow(this.width, this.height, "Sponge", 0L, 0L);

        if (this.handle == 0L) {
            throw new RuntimeException("Failed to create window");
        }
    }

    public long getHandle() {
        return this.handle;
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public boolean isRunning() {
        return !glfwWindowShouldClose(this.handle);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void close() throws Exception {
        glfwDestroyWindow(this.handle);
        glfwTerminate();
    }
}
