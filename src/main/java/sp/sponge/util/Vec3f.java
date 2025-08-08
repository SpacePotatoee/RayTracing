package sp.sponge.util;

import org.joml.Math;
import org.joml.Vector3f;

/**
 * My own version of Vector3f except every method creates a new instance and is not so freaking annoying to use
 */
public class Vec3f {
    public float x;
    public float y;
    public float z;

    public Vec3f() {
        this(0);
    }

    public Vec3f(float a) {
        this(a, a, a);
    }

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public Vec3f mul(float a) {
        return this.mul(a, a, a);
    }

    public Vec3f mul(Vec3f vec3f) {
        return this.mul(vec3f.x, vec3f.y, vec3f.z);
    }

    public Vec3f mul(float x, float y, float z) {
        return new Vec3f(this.x * x, this.y * y, this.z * z);
    }


    public Vec3f subtract(float a) {
        return this.add(-a, -a, -a);
    }

    public Vec3f subtract(Vec3f vec3f) {
        return this.add(-vec3f.x, -vec3f.y, -vec3f.z);
    }

    public Vec3f subtract(float x, float y, float z) {
        return this.add(-x, -y, -z);
    }


    public Vec3f add(float a) {
        return this.add(a, a, a);
    }

    public Vec3f add(Vec3f vec3f) {
        return this.add(vec3f.x, vec3f.y, vec3f.z);
    }

    public Vec3f add(float x, float y, float z) {
        return new Vec3f(this.x + x, this.y + y, this.z + z);
    }


    public Vec3f subtractInternal(Vec3f vec3f) {
        return this.subtractInternal(vec3f.x, vec3f.y, vec3f.z);
    }

    public Vec3f subtractInternal(float x, float y, float z) {
        return this.addInternal(-x, -y, -z);
    }


    public Vec3f addInternal(Vec3f vec3f) {
        return this.addInternal(vec3f.x, vec3f.y, vec3f.z);
    }

    public Vec3f addInternal(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }


    public Vec3f normalize() {
        Vec3f vec3f = new Vec3f();
        float scalar = Math.invsqrt(Math.fma(x, x, Math.fma(y, y, z * z)));
        vec3f.x = this.x * scalar;
        vec3f.y = this.y * scalar;
        vec3f.z = this.z * scalar;

        return vec3f;
    }

    public float length() {
        return Math.sqrt(org.joml.Math.fma(x, x, Math.fma(y, y, z * z)));
    }

    public Vector3f toVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public Vec3f negate() {
        return new Vec3f(-this.x, -this.y, -this.z);
    }


    public Vec3f rotateX(float angle) {
        Vec3f vec3f = new Vec3f();
        float sin = Math.sin(angle), cos = Math.cosFromSin(sin, angle);
        float y = this.y * cos - this.z * sin;
        float z = this.y * sin + this.z * cos;
        vec3f.x = this.x;
        vec3f.y = y;
        vec3f.z = z;
        return vec3f;
    }

    public Vec3f rotateY(float angle) {
        Vec3f vec3f = new Vec3f();
        float sin = Math.sin(angle), cos = Math.cosFromSin(sin, angle);
        float x =  this.x * cos + this.z * sin;
        float z = -this.x * sin + this.z * cos;
        vec3f.x = x;
        vec3f.y = this.y;
        vec3f.z = z;
        return vec3f;
    }

    public Vec3f rotateZ(float angle) {
        Vec3f vec3f = new Vec3f();
        float sin = Math.sin(angle), cos = Math.cosFromSin(sin, angle);
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        vec3f.x = x;
        vec3f.y = y;
        vec3f.z = this.z;
        return vec3f;
    }

    @Override
    public String toString() {
        return "Vec3f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
