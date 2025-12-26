package sp.sponge.util.manager;

import sp.sponge.render.vulkan.VulkanCtx;

import java.nio.file.Path;

public interface Manager {
    String getDirectoryToCheck();

    void acceptPath(Path relativePath, Path absolutePath);

    void free(VulkanCtx ctx);
}
