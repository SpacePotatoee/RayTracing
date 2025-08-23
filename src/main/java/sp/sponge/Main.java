package sp.sponge;

import sp.sponge.render.Window;

import java.io.File;
import java.io.IOException;

public class Main {


    public static void main(String[] args) {
        Window window = Window.getWindow();
        Sponge sponge = Sponge.getInstance();

        File runFile = new File(args[0]);

        try {
            runFile.createNewFile();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        sponge.setArgs(runFile);

        while (window.isRunning()) {
            sponge.mainLoop();
        }

    }
}