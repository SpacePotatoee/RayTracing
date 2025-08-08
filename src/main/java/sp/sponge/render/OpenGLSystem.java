package sp.sponge.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class OpenGLSystem {

    public static void enableDepthTest() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void disableDepthTest() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

}
