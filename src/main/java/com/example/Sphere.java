package com.example;

public class Sphere implements Hittable {
    double radius;
    Ray position;
    Material material;

    public Sphere(Vec3 position, float radius, Material material) {
        this.radius = radius;
        this.position = new Ray(position, new Vec3());
        this.material = material;
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }
    }

    public Sphere(Vec3 positionFrom, Vec3 positionTo, float radius, Material material) {
        this.radius = radius;
        this.position = new Ray(positionFrom, Vec3.sub(positionTo, positionFrom));
        this.material = material;
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }
    }

    @Override
    public boolean hit(Ray ray, HitRecord hit, Interval rayT) {
//        Vec3 c = position.at(ray.time);
        double cX = position.origin.getX() + position.direction.getX() * ray.time;
        double cY = position.origin.getY() + position.direction.getY() * ray.time;
        double cZ = position.origin.getZ() + position.direction.getZ() * ray.time;
//        Vec3 oc = new Vec3(c).sub(ray.origin);
        Vec3 oc = new Vec3(cX, cY, cZ).sub(ray.origin);
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
            hit.normal.set(hit.point).sub(cX, cY, cZ).mul(1 / radius);
            hit.setFaceNormal(ray, hit.normal);
            hit.time = t;
            hit.material = material;
            return true;
        }
    }

    @Override
    public boolean hit(Ray ray, HitRecord hit, Interval rayT, double time) {
        ray.at(time, hit.point);
        hit.normal.set(hit.point).sub(position.at(ray.time)).mul(1 / radius);
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
//        data[i] = position.getX();
//        data[i + 1] = position.getY();
//        data[i + 2] = position.getZ();
//        data[i + 3] = radius * radius;
    }
}
