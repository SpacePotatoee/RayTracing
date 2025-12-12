package sp.sponge.input;

import org.lwjgl.glfw.GLFW;
import sp.sponge.input.keybind.Keybind;
import sp.sponge.render.Window;

public class Input {
    public double mousePosX;
    public double mousePosY;

    public Input(Window window) {
        GLFW.glfwSetKeyCallback(window.getHandle(), this::onKeyPress);

        GLFW.glfwSetMouseButtonCallback(window.getHandle(), this::onMouseButton);
        GLFW.glfwSetCursorPosCallback(window.getHandle(), this::onCursorPos);
    }

    private void onCursorPos(long window, double xPos, double yPos) {
        this.mousePosX = xPos;
        this.mousePosY = yPos;
    }

    private void onKeyPress(long window, int key, int scancode, int action, int mods) {
        Keybind.setKeyDown(key, action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT);
    }

    private void onMouseButton(long window, int button, int action, int mods) {
        Keybind.setKeyDown(button, action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT);
    }

    public void lockCursor(Window window, float x, float y) {
        GLFW.glfwSetCursorPos(window.getHandle(), x, y);
        GLFW.glfwSetInputMode(window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        this.mousePosX = x;
        this.mousePosY = y;
    }

    public void unlockCursor(Window window) {
        GLFW.glfwSetInputMode(window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

}
