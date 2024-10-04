package com.example;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static double random(double min, double max) {
        return random() * (max - min) + min;
    }

    public static double random() {
        return ThreadLocalRandom.current().nextDouble();
    }

//    public static float random(float min, float max) {
//        return random() * (max - min) + min;
//    }
//
//    public static float random() {
//        return ThreadLocalRandom.current().nextFloat();
//    }
}
