package sp.sponge.render.shader;

import sp.sponge.Sponge;

public class ShaderRegistry {
    public static ShaderProgram defaultShader = new ShaderProgram("default/default");


    public static void registerShaders() {
        Sponge.getInstance().getLogger().info("Registering Shaders");
    }

}
