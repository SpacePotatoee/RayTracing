package sp.sponge.render;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import sp.sponge.Window;

public class MainRenderer {

    public void render() {
        GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GLFW.glfwSwapBuffers(Window.getWindow().getHandle());
    }

}
