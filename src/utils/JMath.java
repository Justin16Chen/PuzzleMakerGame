package utils;

public class JMath {
    public static int clamp(int x, int max, int min) {
        return Math.max(min, Math.min(max, x));
    }
    public static double clamp(double x, double max, double min) {
        return Math.max(min, Math.min(max, x));
    }
}
