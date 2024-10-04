package com.example;

public abstract class Material {
    public abstract boolean scatter(Ray ray, Hittable.HitRecord hit, Vec3 colorAttenuation);
}
