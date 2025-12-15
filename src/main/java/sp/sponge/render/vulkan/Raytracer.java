package sp.sponge.render.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkAccelerationStructureGeometryKHR;
import org.lwjgl.vulkan.VkPhysicalDeviceRayTracingPipelinePropertiesKHR;
import org.lwjgl.vulkan.VkPhysicalDeviceRayTracingPropertiesNV;

public class Raytracer {
    private final VkPhysicalDeviceRayTracingPipelinePropertiesKHR rtPipelineProperties;

    public Raytracer() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.rtPipelineProperties = VkPhysicalDeviceRayTracingPipelinePropertiesKHR.calloc()
                    .sType$Default();
        }
    }

    public void raytrace() {
//        VkAccelerationStructureGeometryKHR triangleGeometry
    }

}
