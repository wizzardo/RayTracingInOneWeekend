package com.example;

import java.io.IOException;

public class App {

    HittableList hittables = new HittableList();

    public static void main2(String[] args) throws IOException {
        App app = new App();

        {
            var materialGround = new LambertianMaterial(new Vec3(0.8, 0.8, 0.0));
            var materialCenter = new LambertianMaterial(new Vec3(0.1, 0.2, 0.5));
//        var materialLeft = new MetalMaterial(new Vec3(0.8, 0.8, 0.8), 0.3);
            var materialLeft = new DielectricMaterial(1.5f);
//        var materialLeft = new DielectricMaterial(1 / 1.33);
            var materialBubble = new DielectricMaterial(1 / 1.5f);
            var materialRight = new MetalMaterial(new Vec3(0.8, 0.6, 0.2), 1);

            app.hittables.add(new Sphere(new Vec3(0, -100.5, -1), 100, materialGround));
            app.hittables.add(new Sphere(new Vec3(0, 0, -1.2), 0.5f, materialCenter));
            app.hittables.add(new Sphere(new Vec3(-1.0, 0, -1.0), 0.5f, materialLeft));
            app.hittables.add(new Sphere(new Vec3(-1.0, 0, -1.0), 0.4f, materialBubble));
            app.hittables.add(new Sphere(new Vec3(1.0, 0, -1.0), 0.5f, materialRight));
        }

//        {
//            double r = Math.cos(Math.PI / 4);
//            var materialLeft = new LambertianMaterial(new Vec3(0,0,1));
//            var materialRight = new LambertianMaterial(new Vec3(1,0,0));
//            app.hittables.add(new Sphere(r, new Vec3(-r, 0, -1), materialLeft));
//            app.hittables.add(new Sphere(r, new Vec3(r, 0, -1), materialRight));
//        }

        Camera camera = new Camera();
        camera.lookfrom = new Vec3(-2, 2, 1);
        camera.lookat = new Vec3(0, 0, -1);
        camera.vfov = 20;
        camera.defocusAngle = 10.0f;
        camera.focusDist = 3.4f;
        camera.prepare();
        camera.renderToFile("image.ppm", app.hittables);
    }

    public static void main(String[] args) throws IOException {
        App app = new App();


        var ground_material = new LambertianMaterial(new Vec3(0.5, 0.5, 0.5));
        app.hittables.add(new Sphere(new Vec3(0, -1000, 0), 1000, ground_material));

        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                double choose_mat = Utils.random();
                Vec3 center = new Vec3(a + 0.9 * Utils.random(), 0.2, b + 0.9 * Utils.random());

                if (new Vec3(4, 0.2, 0).sub(center).length() > 0.9) {
                    Material sphere_material;

                    if (choose_mat < 0.8) {
                        // diffuse
                        var albedo = Vec3.random(); //color::random() * color::random() - what?
                        sphere_material = new LambertianMaterial(albedo);
                        var center2 = new Vec3(0, Utils.random(0, 0.5), 0).add(center);
                        app.hittables.add(new Sphere(center, center2, 0.2f, sphere_material));
                    } else if (choose_mat < 0.95) {
                        // metal
                        var albedo = Vec3.random(0.5f, 1);
                        var fuzz = Utils.random(0, 0.5f);
                        sphere_material = new MetalMaterial(albedo, fuzz);
                        app.hittables.add(new Sphere(center, 0.2f, sphere_material));
                    } else {
                        // glass
                        sphere_material = new DielectricMaterial(1.5f);
                        app.hittables.add(new Sphere(center, 0.2f, sphere_material));
                    }
                }
            }
        }

        var material1 = new DielectricMaterial(1.5f);
        app.hittables.add(new Sphere(new Vec3(0, 1, 0), 1.0f, material1));

        var material2 = new LambertianMaterial(new Vec3(0.4, 0.2, 0.1));
        app.hittables.add(new Sphere(new Vec3(-4, 1, 0), 1.0f, material2));

        var material3 = new MetalMaterial(new Vec3(0.7, 0.6, 0.5), 0.0f);
        app.hittables.add(new Sphere(new Vec3(4, 1, 0), 1.0f, material3));

        Camera cam = new Camera();
        cam.multiThreading = true;
        cam.imageWidth = 1200;
        cam.imageHeight = 675;
//        cam.imageWidth /= 2;
//        cam.imageHeight /= 2;
//        cam.imageWidth /= 2;
//        cam.imageHeight /= 2;
//        cam.imageWidth /= 2;
//        cam.imageHeight /= 2;
        cam.samplesPerPixel = 500;
        cam.samplesPerPixel = 100;
//        cam.samplesPerPixel = 50;
        cam.maxDepth = 50;
//        cam.samplesPerPixel = 10;
//        cam.samplesPerPixel = 1;
        cam.maxDepth = 20;
//        cam.samplesPerPixel = 3;
//        cam.maxDepth = 5;
//        cam.maxDepth = 2;
//        cam.maxDepth = 1;

        cam.vfov = 20;
        cam.lookfrom = new Vec3(13, 2, 3);
        cam.lookat = new Vec3(0, 0, 0);
        cam.vup = new Vec3(0, 1, 0);

        cam.defocusAngle = 0.6f;
        cam.focusDist = 10.0f;
        cam.prepare();
        while (true) {
            long start = System.nanoTime();
            cam.renderToFile("cover.ppm", app.hittables);
            long stop = System.nanoTime();
            System.out.println("done in " + (stop - start) / 1000f / 1000f / 1000f + " s");
            break;
        }
    }
}
