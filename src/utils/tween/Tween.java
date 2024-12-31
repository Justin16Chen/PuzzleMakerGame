package utils.tween;

import utils.Print;

public class Tween extends Updatable {

    public static Tween createTween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, int targetLoopCount, boolean pingPong) {
        Tween tween = new Tween(name, target, propertyName, startValue, endValue, duration, 0, targetLoopCount, pingPong);
        Updatable.addUpdatable(tween);
        return tween;
    }
    public static Tween createTween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, int targetLoopCount) {
        Tween tween = new Tween(name, target, propertyName, startValue, endValue, duration, 0, targetLoopCount, false);
        Updatable.addUpdatable(tween);
        return tween;
    }
    public static Tween resetTweenTo(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, int currentLoopCount, int targetLoopCount, boolean pingPong) {
        return new Tween(name, target, propertyName, startValue, endValue, duration, currentLoopCount, targetLoopCount, pingPong);
    }
    public static Tween resetTweenTo(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, int currentLoopCount, int targetLoopCount) {
        return new Tween(name, target, propertyName, startValue, endValue, duration, currentLoopCount, targetLoopCount, false);
    }

    private Number startValue;      // Starting value of the tween
    private Number endValue;        // Ending value of the tween
    private double currentValue;

    private Tween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, int currentLoopCount, int targetLoopCount, boolean pingPong) {
        super(name, target, propertyName, duration, currentLoopCount, targetLoopCount, pingPong);
        this.startValue = startValue;
        Updatable.setProperty(target, propertyName, startValue);
        this.endValue = endValue;
    }

    public Number getStartValue() { return startValue; }
    public Number getEndValue() { return endValue;}

    @Override
    public String toString() {
        if (getTargetLoopCount() < 0)
            return "Tween(" + getName() + " | modifying " + getPropertyName() + " | start: " + startValue + " | end: " + endValue + " | current: " + currentValue + " | " + (Math.round(getElapsedTime() * 1000) / 1000) + "/" + getDuration() + "sec | " + getCurrentLoop() + "/" + getTargetLoopCount() + " loops | ping pong: " + getPingPong() + " | loop complete: " + isLoopComplete() + ")";
        return "Tween(" + getName() + " | modifying " + getPropertyName() + " | start: " + startValue + " | end: " + endValue + " | current: " + currentValue + " | " + (Math.round(getElapsedTime() * 1000) / 1000) + "/" + getDuration() + "sec | looping forever | ping pong: " + getPingPong() + " | loop complete: " + isLoopComplete() + ")";

    }

    public void update(double deltaTime) {
        // find time
        double t = Math.min(1, getElapsedTime() / getDuration());  // Normalized time (0 to 1)

        currentValue = lerp(startValue, endValue, t);
        Updatable.setProperty(getTarget(), getPropertyName(), currentValue);

        if (ALLOW_PRINT && isComplete()) {
            Print.println(getName() + " tween is complete", Print.BLUE);
        }
    }
}