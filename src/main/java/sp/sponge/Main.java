package sp.sponge;

import sp.sponge.render.Window;

import java.io.File;
import java.io.IOException;

public class Main {
    private static Sponge sponge;


    public static void main(String[] args) {
        try {
            Window window = Window.getWindow();
            sponge = Sponge.getInstance();

            File runFile = new File(args[0]);
            runFile.createNewFile();
            sponge.setArgs(runFile);

            while (window.isRunning()) {
                sponge.mainLoop();
            }
            sponge.free();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        } finally {

        }

    }
}