package sp.sponge;

import sp.sponge.input.keybind.Keybinds;
import sp.sponge.render.MainRenderer;
import sp.sponge.render.Window;
import sp.sponge.render.shader.ShaderRegistry;
import sp.sponge.scene.objects.Objects;

import java.io.File;
import java.util.logging.Logger;

public class Sponge {
    private static Sponge INSTANCE;
    public final Logger SPONGE_LOGGER;
    public final MainRenderer renderer;
    private RunFiles runFiles;

    public Sponge() {
        INSTANCE = this;

        this.renderer = new MainRenderer();
        this.SPONGE_LOGGER = Logger.getLogger("sponge");
        ShaderRegistry.registerShaders();
        Objects.registerObjects();
        Keybinds.registerKeybinds();
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

    public void setArgs(File runFile) {
        File scenesFile = new File(runFile, "scenes");

        scenesFile.mkdir();
        this.runFiles = new RunFiles(runFile, scenesFile);
    }

    public RunFiles getRunFiles() {
        return this.runFiles;
    }

    public Logger getLogger() {
        return this.SPONGE_LOGGER;
    }

    public record RunFiles(File runFile, File scenesFile){}

}
