package sp.sponge.render;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;
import sp.sponge.input.Input;

import static org.lwjgl.glfw.GLFW.*;

public class Window implements AutoCloseable {
    private static Window INSTANCE;
    private long handle;
    private int width;
    private int height;
    private Input input;

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

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.width = 1920;
        this.height = 1080;
        this.handle = glfwCreateWindow(this.width, this.height, "Sponge", 0L, 0L);

        if (this.handle == 0L) {
            throw new RuntimeException("Failed to create window");
        }

        glfwMakeContextCurrent(this.handle);
        glfwSwapInterval(1);
        glfwShowWindow(this.handle);
        glfwSetWindowSizeCallback(this.handle, this::resize);

        this.input = new Input(this);

        GL.createCapabilities();
    }

    public void resize(long handle, int width, int height) {
        GL11.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    public long getHandle() {
        return this.handle;
    }

    public void pollEvents() {
        glfwSwapBuffers(this.handle);
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

    public Input getInput() {
        return input;
    }

    @Override
    public void close() throws Exception {
        glfwDestroyWindow(this.handle);
        glfwTerminate();
    }
}
