package utils;

import java.awt.Color;

public class JMath {
    public static int clamp(int x, int max, int min) {
        return Math.max(min, Math.min(max, x));
    }
    public static double clamp(double x, double max, double min) {
        return Math.max(min, Math.min(max, x));
    }
    public static double lerp(double x, double y, double a) {
        return x * (1 - a) + y * a;
    }
    public static Color lerpColor(Color x, Color y, double a) {
        return new Color(
            (int) lerp(x.getRed(), y.getRed(), a),
            (int) lerp(x.getGreen(), y.getGreen(), a),
            (int) lerp(x.getBlue(), y.getBlue(), a)
        );
    }
}
