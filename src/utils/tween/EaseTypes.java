package utils.tween;

public class EaseTypes {
    public static int EASE_POWER = 3;
    public static double getEasingFunction(EaseType updateType, double t) {
        switch (updateType) {
            case LINEAR:
                return t;
            case EASE_IN:
                return Math.pow(t, EASE_POWER);
            case EASE_OUT:
                return 1 - Math.pow(1 - t, EASE_POWER);
            case EASE_IN_OUT:
                return t < 0.5 ? 4 * Math.pow(t, EASE_POWER) : 1 - Math.pow(-2 * t + 2, 3) / 2;
            default:
                return t;
        }
    }
}
