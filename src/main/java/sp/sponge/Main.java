package sp.sponge;

import sp.sponge.render.Window;

public class Main {


    public static void main(String[] args) {
        Sponge sponge = Sponge.getInstance();
        Window window = Window.getWindow();

        while (window.isRunning()) {
            sponge.mainLoop();
        }

    }
}