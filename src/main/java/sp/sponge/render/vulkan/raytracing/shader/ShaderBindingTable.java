package sp.sponge.render.vulkan.raytracing.shader;

import sp.sponge.render.vulkan.VulkanCtx;

public record ShaderBindingTable(ShaderGroup rayGen, ShaderGroup miss, ShaderGroup hit) {

    public void free(VulkanCtx ctx) {
        rayGen.free(ctx);
        miss.free(ctx);
        hit.free(ctx);
    }

}
