package com.example;

public class Ray {
    Vec3 origin;
    Vec3 direction;

    public Ray(Vec3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Ray() {
        this.origin = new Vec3();
        this.direction = new Vec3();
    }

    public Vec3 at(double t) {
        return Vec3.add(origin, Vec3.mul(direction, t));
    }

    public Vec3 at(double t, Vec3 into) {
        return into.set(direction).mul(t).add(origin);
    }
}
