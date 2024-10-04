package com.example;

public interface Hittable {

    class HitRecord {
        public Vec3 point = new Vec3();
        public Vec3 normal = new Vec3();
        public double time;
        public boolean frontFace;
        public Material material;
        public Vec3 attenuation = new Vec3();
        public Interval interval = new Interval(0.001, Double.MAX_VALUE);

        public void setFaceNormal(Ray ray, Vec3 outwardNormal) {
            // Sets the hit record normal vector.
            // NOTE: the parameter `outward_normal` is assumed to have unit length.
            frontFace = Vec3.dot(ray.direction, outwardNormal) < 0;
            normal.set(outwardNormal);
            if (!frontFace) {
                normal.mul(-1);
            }
        }
    }

    boolean hit(Ray ray, HitRecord record, Interval rayT);
    boolean hit(Ray ray, HitRecord record, Interval rayT, double time);
    int doublesSize();
    void toData(double[] data, int i);
}
