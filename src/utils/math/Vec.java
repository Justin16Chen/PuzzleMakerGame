package utils.math;

import java.text.DecimalFormat;

public class Vec {
    private static DecimalFormat df = new DecimalFormat("0.000");

    private double x, y;

    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Vec(double n) {
        this.x = n;
        this.y = n;
    }

    public double x() {
        return x;
    }
    public double y() {
        return y;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double mag() {
        return Math.sqrt(x * x + y * y);
    }
    public double dist(Vec v) {
        return Math.sqrt(Math.pow(x - v.y(), 2) + Math.pow(y - v.y(), 2));
    }
    public Vec normalize() {
        return this.div(mag());
    }
    public Vec add(Vec v) {
        return new Vec(x + v.x(), y + v.y());
    }
    public Vec add(double n) {
        return new Vec(x + n, y + n);
    }
    public Vec sub(Vec v) {
        return new Vec(x - v.x(), y - v.y());
    }
    public Vec sub(double n) {
        return new Vec(x - n, y - n);
    }
    public Vec mult(Vec v) {
        return new Vec(x * v.x(), y * v.y());
    }
    public Vec mult(double n) {
        return new Vec(x * n, y * n);
    }
    public Vec div(Vec v) {
        return new Vec(x / v.x(), y / v.y());
    }
    public Vec div(double n) {
        return new Vec(x / n, y / n);
    }

    @Override
    public String toString() {
        return df.format(x) + ", " + df.format(y);
    }

    @Override
    public Vec clone() {
        return new Vec(x, y);
    }
}
