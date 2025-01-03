package utils.tween;

public class EaseTypes {
    public static int EASE_POWER = 3;
    public static double getEasingFunction(EaseType updateType, double x) {
        double c1 = 1.70158, c3 = c1 + 1, c2 = c1 * 1.525;;
        switch (updateType) {
            case LINEAR:
                return x;
            case EASE_IN:
                return Math.pow(x, EASE_POWER);
            case EASE_OUT:
                return 1 - Math.pow(1 - x, EASE_POWER);
            case EASE_IN_OUT:
                return x < 0.5 ? 4 * Math.pow(x, EASE_POWER) : 1 - Math.pow(-2 * x + 2, 3) / 2;
            case EASE_IN_BACK:
                return c3 * x * x * x - c1 * x * x;
            case EASE_OUT_BACK:
                return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
            case EASE_IN_OUT_BACK:
                return x < 0.5
                    ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                    : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
            default:
                return x;
        }
    }
}
