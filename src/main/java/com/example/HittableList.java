package com.example;

public class HittableList implements Hittable {
    private final HitRecord tmp = new HitRecord();
    private Hittable[] array = new Hittable[10];
    private double[] doubles = new double[10];
    private int size = 0;
    private int dsize = 0;

    @Override
    public boolean hit(Ray ray, HitRecord hit, Interval rayT) {
        boolean hitAnything = false;
        double closestSoFar = rayT.max;

        var rd2 = ray.direction.lengthSquared();

        for (int i = this.size() - 1; i >= 0; i--) {
            float t = sphereHitCheck(ray, doubles, i * 4, rayT, rd2);
            if (t > 0 && array[i].hit(ray, tmp, rayT, t)) {
                hitAnything = true;
                if (tmp.time < closestSoFar) {
                    closestSoFar = tmp.time;
                    rayT.max = closestSoFar;
                    hit.normal.set(tmp.normal);
                    hit.point.set(tmp.point);
                    hit.time = tmp.time;
                    hit.material = tmp.material;
                    hit.frontFace = tmp.frontFace;
                }
            }
        }

        return hitAnything;
    }

    private float sphereHitCheck(Ray ray, double[] data, int i, Interval rayT, double rayDirectionLengthSquared) {
        //        Vec3 oc = new Vec3(position).sub(ray.origin);
        double ocX = data[i] - ray.origin.getX();
        double ocY = data[i + 1] - ray.origin.getY();
        double ocZ = data[i + 2] - ray.origin.getZ();
//        Vec3 oc = hit.point.set(position).sub(ray.origin);
//        double h = Vec3.dot(ray.direction, oc);
        double h = ray.direction.getX() * ocX + ray.direction.getY() * ocY + ray.direction.getZ() * ocZ;
//        if (h <= 0)
//            return false;
        double a = rayDirectionLengthSquared;
//        double c = oc.lengthSquared() - radius * radius;
        double radius2 = data[i + 3];
        double c = (ocX * ocX + ocY * ocY + ocZ * ocZ) - radius2;
//        double discriminant = b * b - 4 * a * c;
        double discriminant = h * h - a * c;
        if (discriminant < 0) {
            return -1;
        } else {
            double sqrtd = Math.sqrt(discriminant);
            // Find the nearest root that lies in the acceptable range.
            var root = (h - sqrtd) / a;
            if (!rayT.surrounds(root)) {
                root = (h + sqrtd) / a;
                if (!rayT.surrounds(root))
                    return -1;
            }

            return (float) root;
        }
    }

    @Override
    public boolean hit(Ray ray, HitRecord record, Interval rayT, double d) {
        return false;
    }

    @Override
    public int doublesSize() {
        return 0;
    }

    @Override
    public void toData(double[] data, int i) {
    }

    public boolean add(Hittable hittable) {
        if (size == array.length) {
            Hittable[] newArray = new Hittable[array.length * 2];
            System.arraycopy(array, 0, newArray, 0, array.length);
            array = newArray;
        }
        int i = size++;
        array[i] = hittable;

        int dsize = hittable.doublesSize();
        if (dsize != 4) {
            throw new IllegalArgumentException();
        }
        if (this.dsize + dsize > doubles.length) {
            double[] newDoubles = new double[doubles.length * 2];
            System.arraycopy(doubles, 0, newDoubles, 0, doubles.length);
            doubles = newDoubles;
        }
        hittable.toData(doubles, this.dsize);
        this.dsize += dsize;

        return true;
    }

    public int size() {
        return this.size;
    }
}
