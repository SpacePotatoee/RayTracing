package sp.sponge;

import sp.sponge.render.MainRenderer;
import sp.sponge.render.Window;

import java.io.File;
import java.util.logging.Logger;

public class Sponge {
    private static Sponge INSTANCE;
    public final Logger SPONGE_LOGGER;
    private final MainRenderer mainRenderer;
    private RunFiles runFiles;

    public Sponge() {
        INSTANCE = this;
        this.SPONGE_LOGGER = Logger.getLogger("sponge");
        this.mainRenderer = new MainRenderer();
    }

    public void mainLoop() {
        Window window = Window.getWindow();
        window.pollEvents();

        this.mainRenderer.render();
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

    public MainRenderer getMainRenderer() {
        return mainRenderer;
    }

    public RunFiles getRunFiles() {
        return this.runFiles;
    }

    public Logger getLogger() {
        return this.SPONGE_LOGGER;
    }

    public void free() {
        this.mainRenderer.close();
    }

    public record RunFiles(File runFile, File scenesFile){}

}
