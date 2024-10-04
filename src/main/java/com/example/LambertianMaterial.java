package com.example;

public class LambertianMaterial extends Material {

    final Vec3 color;

    public LambertianMaterial(Vec3 color) {
        this.color = color;
    }

    @Override
    public boolean scatter(Ray ray, Hittable.HitRecord hit, Vec3 colorAttenuation) {
        var scatterDirection = Vec3.randomUnitVector().add(hit.normal);
        if (scatterDirection.isNearZero())
            scatterDirection.set(hit.normal);
        ray.origin.set(hit.point);
        ray.direction.set(scatterDirection);
        colorAttenuation.set(color);
        return true;
    }
}
