package sp.sponge.resources;

import java.io.File;

public class ResourceManager {

    public static File getFile(String path) {
        return new File("src/main/resources/assets/" + path);
    }

}
