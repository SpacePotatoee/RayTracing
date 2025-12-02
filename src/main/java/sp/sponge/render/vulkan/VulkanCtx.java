package sp.sponge.render.vulkan;

import sp.sponge.Sponge;
import sp.sponge.render.Window;
import sp.sponge.render.vulkan.device.LogicalDevice;
import sp.sponge.render.vulkan.device.PhysicalDevice;
import sp.sponge.render.vulkan.screen.Surface;
import sp.sponge.render.vulkan.screen.SwapChain;

public class VulkanCtx {
    private final VulkanInstance vulkanInstance;
    private final PhysicalDevice physicalDevice;
    private final LogicalDevice logicalDevice;
    private final Surface surface;
    private final SwapChain swapChain;

    public VulkanCtx() {
        this.vulkanInstance = new VulkanInstance(true);
        this.physicalDevice = PhysicalDevice.getMainPhysicalDevice(this.vulkanInstance.getInstance(), null, Sponge.getInstance().getLogger());
        this.logicalDevice = new LogicalDevice(this.physicalDevice);
        Window window = Window.getWindow();
        this.surface = new Surface(this.vulkanInstance, this.physicalDevice, window);
        this.swapChain = new SwapChain(window, this.logicalDevice, this.surface, 3, true);
    }

    public SwapChain getSwapChain() {
        return swapChain;
    }

    public Surface getSurface() {
        return surface;
    }

    public LogicalDevice getLogicalDevice() {
        return this.logicalDevice;
    }

    public VulkanInstance getVulkanInstance() {
        return vulkanInstance;
    }

    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public void free() {
        this.physicalDevice.close();
        this.logicalDevice.close();
        this.surface.close();
        this.vulkanInstance.close();
    }
}
