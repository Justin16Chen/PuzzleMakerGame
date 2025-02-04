package utils;

import java.awt.Color;

public class JMath {
    public static final double THRESHOLD = 0.0001;
    public static int clamp(int x, int max, int min) {
        return Math.max(min, Math.min(max, x));
    }
    public static double clamp(double x, double max, double min) {
        return Math.max(min, Math.min(max, x));
    }

    // only works if x is less than y, but more performant than lerp
    public static double simpleLerp(double x, double y, double a) {
        return x + (y - x) * a;
    }

    // works for any values of x and y
    public static double lerp(double x, double y, double a) {
        // prevent rounding errors
        if (a < THRESHOLD)
            return x;
        if (a > 1 - THRESHOLD) {
            return y;
        }

        // actually calculate lerp
        return x * (1 - a) + y * a;
    }

    // creates a new color based on the starting and ending colors
    public static Color lerpColor(Color x, Color y, double a) {
        return new Color(
            (int) lerp(x.getRed(), y.getRed(), a),
            (int) lerp(x.getGreen(), y.getGreen(), a),
            (int) lerp(x.getBlue(), y.getBlue(), a)
        );
    }

    // normalizes a number between 0 and 1 given a min and max
    public static double normalize(double min, double max, double num) {
        return (num - min) / (max - min);
    }
    
}
