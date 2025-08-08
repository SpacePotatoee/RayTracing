package sp.sponge.input.keybind;

import org.lwjgl.glfw.GLFW;
import sp.sponge.Sponge;

public class Keybinds {

    public static final Keybind FORWARDS = new Keybind(GLFW.GLFW_KEY_W);
    public static final Keybind BACKWARDS = new Keybind(GLFW.GLFW_KEY_S);
    public static final Keybind LEFT = new Keybind(GLFW.GLFW_KEY_A);
    public static final Keybind RIGHT = new Keybind(GLFW.GLFW_KEY_D);

    public static final Keybind SPACE = new Keybind(GLFW.GLFW_KEY_SPACE);
    public static final Keybind SHIFT = new Keybind(GLFW.GLFW_KEY_LEFT_SHIFT);

    public static final Keybind RIGHT_CLICK = new Keybind(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

    public static void registerKeybinds() {
        Sponge.getInstance().getLogger().info("Registering Keybinds");
    }

}
