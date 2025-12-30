package sp.sponge.render.vulkan.sync;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;
import org.lwjgl.vulkan.VkExportSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import sp.sponge.render.vulkan.VulkanCtx;
import sp.sponge.render.vulkan.VulkanUtils;

import java.nio.LongBuffer;

public class Semaphore {
    private final long vkSemaphore;

    public Semaphore(VulkanCtx ctx, boolean external) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSemaphoreCreateInfo createInfo = VkSemaphoreCreateInfo.calloc(stack).sType$Default();

            if (external) {
                VkExportSemaphoreCreateInfo exportSemaphoreCreateInfo = VkExportSemaphoreCreateInfo.calloc(stack)
                        .sType$Default()
                        .handleTypes(VK11.VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_OPAQUE_WIN32_BIT);
                createInfo.pNext(exportSemaphoreCreateInfo);
            }

            LongBuffer semaphorePtr = stack.mallocLong(1);
            VulkanUtils.check(
                    VK10.vkCreateSemaphore(ctx.getLogicalDevice().getVkDevice(), createInfo, null, semaphorePtr),
                    "Failed to create semaphore"
            );
            this.vkSemaphore = semaphorePtr.get(0);
        }
    }

    public void free(VulkanCtx ctx) {
        VK10.vkDestroySemaphore(ctx.getLogicalDevice().getVkDevice(), this.vkSemaphore, null);
    }

    public long getVkSemaphore() {
        return vkSemaphore;
    }
}
