package sp.sponge;

import org.lwjgl.glfw.GLFW;
import sp.sponge.render.MainRenderer;

import java.util.logging.Logger;

public class Main {
    public static final Logger SPONGE_LOGGER = Logger.getLogger("sponge");

    public static void main(String[] args) {
        Window window = Window.getWindow();
        MainRenderer renderer = new MainRenderer();

        while (window.isRunning()) {
            GLFW.glfwPollEvents();
            renderer.render();
        }
    }
}