package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Camera {
    double aspectRatio = 16.0 / 9.0;
    double vfov = 90;
    int imageWidth = 400;
    int imageHeight = (int) (imageWidth / aspectRatio);
    int samplesPerPixel = 100; // Count of random samples for each pixel
    int maxDepth = 50; // Maximum number of ray bounces into scene
    double pixelSamplesScale;
    double viewportHeight = 2;
    double viewportWidth;
    double defocusAngle = 0;  // Variation angle of rays through each pixel
    double focusDist = 10;    // Distance from camera lookfrom point to plane of perfect focus
    Vec3 center = new Vec3();
    Vec3 viewportU = new Vec3();
    Vec3 viewportV = new Vec3();
    Vec3 pixelDeltaU = new Vec3();
    Vec3 pixelDeltaV = new Vec3();
    Vec3 viewportUpperLeft;
    Vec3 pixel00Loc;
    Vec3 lookfrom = new Vec3(0, 0, 0);   // Point camera is looking from
    Vec3 lookat = new Vec3(0, 0, -1);  // Point camera is looking at
    Vec3 vup = new Vec3(0, 1, 0);     // Camera-relative "up" direction
    Vec3 u, v, w; // Camera frame basis vectors
    Vec3 defocusDiskU;       // Defocus disk horizontal radius
    Vec3 defocusDiskV;       // Defocus disk vertical radius
    boolean multiThreading = false;

    public Camera() {
        prepare();
    }

    public void setImageSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        prepare();
    }

    public void prepare() {
        aspectRatio = ((double) imageWidth) / imageHeight;
        pixelSamplesScale = 1.0f / samplesPerPixel;

        center.set(lookfrom);

        // Determine viewport dimensions.
        double theta = Math.toRadians(vfov);
        double h = Math.tan(theta / 2);
        viewportHeight = (float) (2 * h * focusDist);
        viewportWidth = (float) (viewportHeight * aspectRatio);

        // Calculate the u,v,w unit basis vectors for the camera coordinate frame.
        w = new Vec3(lookfrom).sub(lookat).normalize();
        u = Vec3.cross(vup, w).normalize();
        v = Vec3.cross(w, u);

        // Calculate the vectors across the horizontal and down the vertical viewport edges.
        viewportU = new Vec3(u).mul(viewportWidth); // Vector across viewport horizontal edge
        viewportV = new Vec3(v).mul(-viewportHeight); // Vector down viewport vertical edge

        // Calculate the horizontal and vertical delta vectors from pixel to pixel.
        pixelDeltaU = Vec3.mul(viewportU, 1.0f / imageWidth);
        pixelDeltaV = Vec3.mul(viewportV, 1.0f / imageHeight);

        // Calculate the location of the upper left pixel.
        viewportUpperLeft = new Vec3(center)
                .sub(new Vec3(w).mul(focusDist))
                .sub(viewportU.getX() / 2, viewportU.getY() / 2, viewportU.getZ() / 2)
                .sub(viewportV.getX() / 2, viewportV.getY() / 2, viewportV.getZ() / 2);

        pixel00Loc = new Vec3(pixelDeltaU).add(pixelDeltaV).mul(0.5f).add(viewportUpperLeft);

        // Calculate the camera defocus disk basis vectors.
        float defocusRadius = (float) (focusDist * Math.tan(Math.toRadians(defocusAngle / 2)));
        defocusDiskU = new Vec3(u).mul(defocusRadius);
        defocusDiskV = new Vec3(v).mul(defocusRadius);
    }

    public void renderToFile(String filename, HittableList hittables) throws IOException {
        double[] pixels = render(hittables);
        savePPM(pixels, imageWidth, imageHeight, filename);
    }

    public double[] render(HittableList hittables) {
        double[] pixels = new double[imageWidth * imageHeight * 3];

        AtomicInteger linesRemaining = new AtomicInteger(imageHeight);

        IntStream stream = IntStream.range(0, imageWidth * imageHeight);
        if (multiThreading) {
            stream = stream.parallel();
        }
        stream.forEach(pixel -> {
            Vec3 color = new Vec3(0, 0, 0);
            Ray ray = new Ray();
            Hittable.HitRecord hitRecord = new Hittable.HitRecord();
            int y = pixel / imageWidth;
            int x = pixel - y * imageWidth;
            if (x == 0) {
                int remaining = linesRemaining.decrementAndGet();
                if (remaining % 10 == 0)
                    System.out.println("Scanlines remaining: " + remaining);
            }

            int index = (y * imageWidth + x) * 3;
            for (int i = 0; i < samplesPerPixel; i++) {
                // Construct a camera ray originating from the defocus disk and directed at a randomly
                // sampled point around the pixel location x, y.
                if (defocusAngle <= 0) {
                    ray.origin.set(center);
                } else {
                    defocusDiskSample(ray.origin);
                }

                var xx = x + Utils.random() - 0.5f;
                var yy = y + Utils.random() - 0.5f;
                ray.direction.set(pixel00Loc)
                        .add(xx * pixelDeltaU.getX(), xx * pixelDeltaU.getY(), xx * pixelDeltaU.getZ())
                        .add(yy * pixelDeltaV.getX(), yy * pixelDeltaV.getY(), yy * pixelDeltaV.getZ())
                        .sub(ray.origin)
                ;

                rayColor(ray, color, hittables, hitRecord, maxDepth);

                pixels[index] += color.getX();
                pixels[index + 1] += color.getY();
                pixels[index + 2] += color.getZ();
            }

            pixels[index] *= pixelSamplesScale;
            pixels[index + 1] *= pixelSamplesScale;
            pixels[index + 2] *= pixelSamplesScale;
        });

        return pixels;
    }

    private void defocusDiskSample(Vec3 to) {
        // Returns a random point in the camera defocus disk.
        var p = Vec3.randomUnitVectorInDisk();
        var x = p.getX();
        var y = p.getY();
        to.set(center)
                .add(x * defocusDiskU.getX() + y * defocusDiskV.getX(),
                        x * defocusDiskU.getY() + y * defocusDiskV.getY(),
                        x * defocusDiskU.getZ() + y * defocusDiskV.getZ())
        ;
    }

    private void rayColor(Ray r, Vec3 color, HittableList hittables, Hittable.HitRecord hit, int depth) {
        double attX = 1;
        double attY = 1;
        double attZ = 1;
        for (int i = 0; i <= depth; i++) {
            if (i == depth) {
//                color.set(0, 0, 0);
//                return;
                color.set(1, 1, 1);
                break;
            }
            if (hittables.hit(r, hit, hit.interval.set(0.001, Double.MAX_VALUE))) {
                if (hit.material.scatter(r, hit, hit.attenuation)) {
                    attX *= hit.attenuation.getX();
                    attY *= hit.attenuation.getY();
                    attZ *= hit.attenuation.getZ();
                } else {
                    color.set(0, 0, 0);
                    return;
                }
            } else {
                Vec3 unit_direction = r.direction.normalize();
                var a = 0.5f * (unit_direction.getY() + 1.0f);
                color.set(0.5f, 0.7f, 1.0f).mul(a).add(1 - a, 1 - a, 1 - a);
                break;
            }
        }
        color.mul(attX, attY, attZ);
    }

    double linearToGamma(double linearComponent) {
        if (linearComponent > 0)
            return Math.sqrt(linearComponent);

        return 0;
    }

    private void savePPM(double[] pixels, int width, int height, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("P3\n");
        writer.write(width + " " + height + "\n");
        writer.write("255\n");
        int l = pixels.length / 3;
        for (int i = 0; i < l; i++) {
            int r = clampColor((int) (linearToGamma(pixels[i * 3]) * 255 + 0.5));
            int g = clampColor((int) (linearToGamma(pixels[i * 3 + 1]) * 255 + 0.5));
            int b = clampColor((int) (linearToGamma(pixels[i * 3 + 2]) * 255 + 0.5));
            writer.write(r + " " + g + " " + b + "\n");
        }

        writer.close();
    }

    private int clampColor(int color) {
        return Math.min(255, Math.max(0, color));
    }
}
