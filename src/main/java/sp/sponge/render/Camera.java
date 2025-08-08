package sp.sponge.render;

import imgui.ImGui;
import org.joml.Vector3f;
import sp.sponge.input.Input;
import sp.sponge.input.keybind.Keybinds;
import sp.sponge.util.Vec3f;

public class Camera {
    private float fov;
    private final Vec3f position;
    private final Vec3f rotationVector;

    public Camera() {
        this.fov = 45;
        this.position = new Vec3f();
        this.rotationVector = new Vec3f(0, 0, 1);
    }

    public float getFov() {
        return fov;
    }

    public void updateCamera() {
        float speed = 0.03f;
        Vec3f rotation = this.getRotationVector();
        if (Keybinds.FORWARDS.isPressed()) {
            this.position.addInternal(rotation.mul(speed));
        }
        if (Keybinds.BACKWARDS.isPressed()) {
            this.position.subtractInternal(rotation.mul(speed));
        }
        if (Keybinds.LEFT.isPressed()) {
            this.position.addInternal(rotation.rotateY((float) Math.toRadians(90)).mul(speed));
        }
        if (Keybinds.RIGHT.isPressed()) {
            this.position.subtractInternal(rotation.rotateY((float) Math.toRadians(90)).mul(speed));
        }

        if (Keybinds.SPACE.isPressed()) {
            this.position.y -= speed;
        }
        if (Keybinds.SHIFT.isPressed()) {
            this.position.y += speed;
        }

        Window window = Window.getWindow();
        Input input = window.getInput();
        if (Keybinds.RIGHT_CLICK.isPressed()) {


            float width = window.getWidth();
            float height = window.getHeight();


            float yaw = (float) (input.mousePosX - width / 2) / height;
            float pitch = (float) (input.mousePosY - height / 2) / height;

            rotationVector.x += pitch;
            rotationVector.x = (float) Math.clamp(rotationVector.x, -Math.PI/2.0, Math.PI/2.0);

            rotationVector.y += yaw;

            input.lockCursor(window, width / 2, height / 2);
        } else {
            input.unlockCursor(window);
        }
    }

    private Vec3f getRotationVector() {
        Vector3f rotation = new Vector3f(0, 0, 1);
        rotation.rotateX(-rotationVector.x);
        rotation.rotateY(-rotationVector.y);
        rotation.normalize();
        return new Vec3f(rotation.x, 0.0f, rotation.z).normalize();
    }

    public Vec3f getRotation() {
        return rotationVector;
    }

    public Vector3f getPosition() {
        return position.toVector3f();
    }

    public void renderImGui() {
        float[] fovSlider = new float[]{fov};
        ImGui.sliderFloat("Field of view", fovSlider, 10, 150);
        fov = fovSlider[0];
    }
}
