package sp.sponge.render.vulkan;

import sp.sponge.Sponge;
import sp.sponge.render.Window;
import sp.sponge.render.vulkan.buffer.descriptors.DescriptorAllocator;
import sp.sponge.render.vulkan.device.LogicalDevice;
import sp.sponge.render.vulkan.device.PhysicalDevice;
import sp.sponge.render.vulkan.pipeline.PipelineCache;
import sp.sponge.render.vulkan.screen.Surface;
import sp.sponge.render.vulkan.screen.SwapChain;

public class VulkanCtx {
    private final VulkanInstance vulkanInstance;
    private final PhysicalDevice physicalDevice;
    private final LogicalDevice logicalDevice;
    private Surface surface;
    private SwapChain swapChain;
    private final PipelineCache pipelineCache;
    private final DescriptorAllocator descriptorAllocator;

    public VulkanCtx() {
        this.vulkanInstance = new VulkanInstance(true);
        this.physicalDevice = PhysicalDevice.getMainPhysicalDevice(this.vulkanInstance.getInstance(), null, Sponge.getInstance().getLogger());
        this.logicalDevice = new LogicalDevice(this.physicalDevice);
        Window window = Window.getWindow();
        this.surface = new Surface(this.vulkanInstance, this.physicalDevice, window);
        this.swapChain = new SwapChain(window, this.logicalDevice, this.surface, 3, true);
        this.pipelineCache = new PipelineCache(this.logicalDevice);
        this.descriptorAllocator = new DescriptorAllocator(this.physicalDevice, this.logicalDevice);
    }

    public DescriptorAllocator getDescriptorAllocator() {
        return descriptorAllocator;
    }

    public PipelineCache getPipelineCache() {
        return pipelineCache;
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

    public void resize() {
        this.swapChain.close();
        this.surface.close();

        Window window = Window.getWindow();
        this.surface = new Surface(this.vulkanInstance, this.physicalDevice, window);
        this.swapChain = new SwapChain(window, this.logicalDevice, this.surface, 3, true);
    }

    public void free() {
        this.descriptorAllocator.close(this.logicalDevice);
        this.pipelineCache.close(this.logicalDevice);
        this.swapChain.close();
        this.surface.close();
        this.logicalDevice.close();
        this.physicalDevice.close();
        this.vulkanInstance.close();
    }
}
