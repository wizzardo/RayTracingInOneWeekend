package com.example;

public class DielectricMaterial extends Material {

    final double refractionIndex;

    public DielectricMaterial(double refractionIndex) {
        this.refractionIndex = refractionIndex;
    }

    @Override
    public boolean scatter(Ray ray, Hittable.HitRecord hit, Vec3 colorAttenuation) {
        colorAttenuation.set(1, 1, 1);
        var ri = hit.frontFace ? (1.0f / refractionIndex) : refractionIndex;

        ray.direction.normalize();

        double cosTheta = Math.min(-ray.direction.dot(hit.normal), 1.0);
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);

        boolean canRefract = !(ri * sinTheta > 1.0);

        if (!canRefract || shouldReflect(cosTheta, ri)) {
            ray.direction.reflect(hit.normal);
        } else {
            ray.direction.refract(hit.normal, ri, cosTheta);
        }

        ray.origin.set(hit.point);
        return true;
    }

    private boolean shouldReflect(double cosTheta, double ri) {
        return reflectance(cosTheta, ri) > Utils.random();
    }

    private double reflectance(double cosine, double refractionIndex) {
        // Use Schlick's approximation for reflectance.
        var r0 = (1 - refractionIndex) / (1 + refractionIndex);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow((1 - cosine), 5);
    }
}
