package sp.sponge.input.keybind;

import java.util.HashMap;

public class Keybind {
    private static final HashMap<Integer, Keybind> keyBindMap = new HashMap<>();
    private boolean isPressed;

    public Keybind(int key) {
        keyBindMap.put(key, this);
    }

    public boolean isPressed() {
        return this.isPressed;
    }

    public static void setKeyDown(int keyId, boolean down) {
        Keybind key = keyBindMap.get(keyId);
        if (key != null) {
            key.isPressed = down;
        }
    }


}
