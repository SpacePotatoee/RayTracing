package sp.sponge.scene.objects.custom;

import org.joml.SimplexNoise;
import org.joml.Vector3f;
import sp.sponge.render.VertexBuffer;
import sp.sponge.scene.objects.ObjectWithResolution;
import sp.sponge.scene.objects.SceneObject;
import sp.sponge.util.Vec3f;

public class Sphere extends SceneObject implements ObjectWithResolution {
    private int resolution = 1;

    public Sphere(float x, float y, float z, boolean fixed) {
        super(x, y, z, fixed);
    }

    public Sphere(Vector3f position, boolean fixed) {
        super(position, fixed);
    }

    @Override
    public void render(VertexBuffer vertexBuffer) {
        double radius = 0.5f;

        double phi = 1.618033988749; //Golden ratio
        float a = 1.0f;
        float c = (float) phi;


        Vec3f color = getRandomColor(3485.0f);
        vertexBuffer.vertex(this.position,0.0f,       c, a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,     -a, 0.0f, c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      a, 0.0f, c).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(7425.0f);
        vertexBuffer.vertex(this.position, 0.0f,       c,       a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       c,       a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       a, 0.0f,       c).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(4565.0f);
        vertexBuffer.vertex(this.position, 0.0f, c,       a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       c, a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f, c,      -a).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(3647.0f);
        vertexBuffer.vertex(this.position, 0.0f,       c,      -a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       c,       a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       a, 0.0f,      -c).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(7327.0f);
        vertexBuffer.vertex(this.position, 0.0f,       c, -a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       a, 0.0f, -c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -a, 0.0f, -c).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(934.6f);
        vertexBuffer.vertex(this.position, 0.0f,       c,      -a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -a, 0.0f,      -c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -c,       a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(8246.6f);
        vertexBuffer.vertex(this.position, 0.0f, c,      -a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -c, a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f, c,       a).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(763.0f);
        vertexBuffer.vertex(this.position, 0.0f,       c,       a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -c,       a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -a, 0.0f,       c).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(843.0f);
        vertexBuffer.vertex(this.position, -c,       a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, -a, 0.0f,       c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, -c,      -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(563.0f);
        vertexBuffer.vertex(this.position,      -a, 0.0f,       c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -c,      -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f,      -c,       a).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(7356.0f);
        vertexBuffer.vertex(this.position,      -a, 0.0f, c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f,      -c, a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       a, 0.0f, c).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(2135.0f);
        vertexBuffer.vertex(this.position, 0.0f,      -c,       a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       a, 0.0f,       c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       c,      -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(146.0f);
        vertexBuffer.vertex(this.position, c,       a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, a, 0.0f,       c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, c,      -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(621.0f);
        vertexBuffer.vertex(this.position, c,       a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, c,      -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, a, 0.0f,      -c).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(823.0f);
        vertexBuffer.vertex(this.position,       a, 0.0f,      -c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,       c,      -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f,      -c,      -a).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(73.0f);
        vertexBuffer.vertex(this.position,       a, 0.0f, -c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f,      -c, -a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -a, 0.0f, -c).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(1124.0f);
        vertexBuffer.vertex(this.position,      -a, 0.0f,      -c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f,      -c,      -a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position,      -c,      -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(8433.0f);
        vertexBuffer.vertex(this.position, -a, 0.0f,      -c).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, -c,      -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, -c,       a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(7234.0f);
        vertexBuffer.vertex(this.position,      -c, -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f, -c,      -a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f, -c,       a).color(color.x, color.y, color.z, 1.0f).next();

        color = getRandomColor(672.0f);
        vertexBuffer.vertex(this.position,       c, -a, 0.0f).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f, -c,      -a).color(color.x, color.y, color.z, 1.0f).next();
        vertexBuffer.vertex(this.position, 0.0f, -c,       a).color(color.x, color.y, color.z, 1.0f).next();

    }

    @Override
    public int getResolution() {
        return resolution;
    }

    @Override
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public static Vec3f getRandomColor(float seed) {
        return new Vec3f(
                SimplexNoise.noise(seed * 48.52456f, seed) * 0.5f + 0.5f,
                SimplexNoise.noise(seed * 33.67325f, seed) * 0.5f + 0.5f,
                SimplexNoise.noise(seed * 67.63267f, seed) * 0.5f + 0.5f
        );
    }
}
