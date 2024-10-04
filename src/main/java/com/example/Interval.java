package com.example;

public class Interval {
    double min;
    double max;

    public Interval(double min, double max) {
        this.min = min;
        this.max = max;
    }

    double size() {
        return max - min;
    }

    boolean contains(double x) {
        return min <= x && x <= max;
    }

    boolean surrounds(double x) {
        return min < x && x < max;
    }

    public Interval set(double min, double max){
        this.min = min;
        this.max = max;
        return this;
    }
}
