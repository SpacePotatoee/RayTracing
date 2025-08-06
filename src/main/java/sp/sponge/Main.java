package sp.sponge;

import sp.sponge.render.Window;

public class Main {


    public static void main(String[] args) {
        Window window = Window.getWindow();
        Sponge sponge = Sponge.getInstance();


        while (window.isRunning()) {
            sponge.mainLoop();
        }

    }
}