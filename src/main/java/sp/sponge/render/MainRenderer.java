package sp.sponge.render;

import sp.sponge.render.vulkan.VulkanCtx;

public class MainRenderer {
    private final VulkanCtx vulkanCtx;

    public MainRenderer() {
        this.vulkanCtx = new VulkanCtx();
    }

    public void renderScene() {

    }


    public VulkanCtx getVulkanCtx() {
        return vulkanCtx;
    }

    public void close() {
        this.vulkanCtx.free();
    }
}
