package sp.sponge.render;

import imgui.ImGui;

public class Camera {
    private float fov;

    public Camera() {
        this.fov = 45;
    }

    public float getFov() {
        return fov;
    }

    public void renderImGui() {
        float[] fovSlider = new float[]{fov};
        ImGui.sliderFloat("Resolution", fovSlider, 10, 150);
        fov = fovSlider[0];
    }
}
