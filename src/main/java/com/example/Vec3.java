package com.example;

public class Vec3 {
    private double x, y, z;

    public Vec3() {
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vec3 add(Vec3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vec3 add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec3 sub(Vec3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vec3 sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vec3 mul(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    public Vec3 mul(double mulX, double mulY, double mulZ) {
        this.x *= mulX;
        this.y *= mulY;
        this.z *= mulZ;
        return this;
    }

    public Vec3 set(Vec3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vec3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vec3 copy() {
        return new Vec3(this);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double dot(Vec3 other) {
        return dot(this, other);
    }

    public Vec3 cross(Vec3 other) {
        return cross(this, other);
    }

    public Vec3 normalize() {
        return mul(1 / length());
    }

    public boolean isNearZero() {
        return Math.abs(x) < 1e-8 && Math.abs(y) < 1e-8 && Math.abs(z) < 1e-8;
    }

    public Vec3 reflect(Vec3 n) {
        double d = -2 * dot(n);
        return add(n.x * d, n.y * d, n.z * d);
    }

    public Vec3 refract(Vec3 n, double etaiOverEtat) {
        var cosTheta = Math.min(-dot(this, n), 1.0);
        return refract(n, etaiOverEtat, cosTheta);
    }

    public Vec3 refract(Vec3 n, double etaiOverEtat, double cosTheta) {
        double x = n.x;
        double y = n.y;
        double z = n.z;
        this.add(cosTheta * x, cosTheta * y, cosTheta * z).mul(etaiOverEtat);

        double scalar = -Math.sqrt(Math.abs(1.0 - this.lengthSquared()));
//        Vec3 tmp = new Vec3(this).mul(-1);
//        tmp.set(n).mul(scalar);

//        auto cos_theta = std::fmin(dot(-uv, n), 1.0);
//        vec3 r_out_perp =  etai_over_etat * (uv + cos_theta*n);
//        vec3 r_out_parallel = -std::sqrt(std::fabs(1.0 - r_out_perp.length_squared())) * n;
//        return r_out_perp + r_out_parallel;
        return this.add(x * scalar, y * scalar, z * scalar);
    }

    @Override
    public String toString() {
        return "Vec3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public static Vec3 add(Vec3 a, Vec3 b) {
        return new Vec3(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vec3 sub(Vec3 a, Vec3 b) {
        return new Vec3(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vec3 mul(Vec3 a, double scalar) {
        return new Vec3(a.x * scalar, a.y * scalar, a.z * scalar);
    }

    public static double dot(Vec3 a, Vec3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vec3 cross(Vec3 a, Vec3 b) {
        return new Vec3(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    public static Vec3 random() {
        return new Vec3(Utils.random(), Utils.random(), Utils.random());
    }

    public static Vec3 random(double min, double max) {
        return new Vec3(Utils.random(min, max), Utils.random(min, max), Utils.random(min, max));
    }

    public static Vec3 randomUnitVector() {
        while (true) {
            var p = Vec3.random(-1, 1);
            var lensq = p.lengthSquared();
            if (1e-160 < lensq && lensq <= 1)
                return p.mul(1 / Math.sqrt(lensq));
        }
    }

    public static Vec3 randomUnitVectorInDisk() {
        while (true) {
            var p = new Vec3(Utils.random(-1, 1), Utils.random(-1, 1), 0);
            var lensq = p.lengthSquared();
            if (1e-160 < lensq && lensq <= 1)
//                return p.mul(1 / Math.sqrt(lensq));
                return p;
        }
    }

    public static Vec3 randomUnitVectorInDisk(Vec3 into) {
        into.z = 0;
        while (true) {
            into.x = Utils.random(-1, 1);
            into.y = Utils.random(-1, 1);
            var lensq = into.lengthSquared();
            if (1e-160 < lensq && lensq <= 1)
                return into;
        }
    }

    public static Vec3 randomOnHemisphere(Vec3 normal) {
        var onUnitSphere = randomUnitVector();
        if (dot(onUnitSphere, normal) > 0.0) // In the same hemisphere as the normal
            return onUnitSphere;
        else
            return onUnitSphere.mul(-1);
    }
}
