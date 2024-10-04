package com.example;

public class Sphere implements Hittable {
    double radius;
    Vec3 position;
    Material material;

    public Sphere(Vec3 position, float radius, Material material) {
        this.radius = radius;
        this.position = position;
        this.material = material;
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }
    }

    @Override
    public boolean hit(Ray ray, HitRecord hit, Interval rayT) {
        Vec3 oc = new Vec3(position).sub(ray.origin);
        double h = Vec3.dot(ray.direction, oc);
        if (h <= 0)
            return false;
        double a = ray.direction.lengthSquared();
        double c = oc.lengthSquared() - radius * radius;
        double discriminant = h * h - a * c;
        if (discriminant < 0) {
            return false;
        } else {
            double sqrtd = Math.sqrt(discriminant);

            // Find the nearest root that lies in the acceptable range.
            var root = (h - sqrtd) / a;
            if (!rayT.surrounds(root)) {
                root = (h + sqrtd) / a;
                if (!rayT.surrounds(root))
                    return false;
            }

            double t = root;
            ray.at(t, hit.point);
            hit.normal.set(hit.point).sub(position).mul(1 / radius);
            hit.setFaceNormal(ray, hit.normal);
            hit.time = t;
            hit.material = material;
            return true;
        }
    }

    @Override
    public boolean hit(Ray ray, HitRecord hit, Interval rayT, double time) {
        ray.at(time, hit.point);
        hit.normal.set(hit.point).sub(position).mul(1 / radius);
        hit.setFaceNormal(ray, hit.normal);
        hit.time = time;
        hit.material = material;
        return true;
    }

    @Override
    public int doublesSize() {
        return 4;
    }

    @Override
    public void toData(double[] data, int i) {
        data[i] = position.getX();
        data[i + 1] = position.getY();
        data[i + 2] = position.getZ();
        data[i + 3] = radius * radius;
    }
}
