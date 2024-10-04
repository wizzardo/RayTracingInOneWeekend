package com.example;

public class MetalMaterial extends Material {

    final Vec3 color;
    final double fuzz;

    public MetalMaterial(Vec3 color, double fuzz) {
        this.color = color;
        this.fuzz = Math.min(fuzz, 1);
    }

    @Override
    public boolean scatter(Ray ray, Hittable.HitRecord hit, Vec3 colorAttenuation) {
        ray.direction.reflect(hit.normal).normalize().add(Vec3.randomUnitVector().mul(fuzz));
        ray.origin.set(hit.point);
        colorAttenuation.set(color);
        return ray.direction.dot(hit.normal) > 0;
    }
}
