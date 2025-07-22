package sp.sponge;

import sp.sponge.render.MainRenderer;
import sp.sponge.render.Window;

import java.util.logging.Logger;

public class Sponge {
    private static Sponge INSTANCE;
    public final Logger SPONGE_LOGGER;
    public final MainRenderer renderer;

    public Sponge() {
        INSTANCE = this;

        this.renderer = new MainRenderer();
        this.SPONGE_LOGGER = Logger.getLogger("sponge");
    }

    public void mainLoop() {
        Window window = Window.getWindow();

        this.renderer.renderScene();

        window.startImGuiFrame();
        this.renderer.renderImGui();
        window.endImGuiFrame();
    }

    public static Sponge getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sponge();
        }

        return INSTANCE;
    }

    public Logger getLogger() {
        return this.SPONGE_LOGGER;
    }

}
