package utils.tween;

import utils.Print;

public class Tween extends Updatable {

    public static Tween createTween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, int repeatCount) {
        Tween tween = new Tween(name, target, propertyName, startValue, endValue, duration, repeatCount);
        Updatable.addUpdatable(tween);
        return tween;
    }
    private Number startValue;      // Starting value of the tween
    private Number endValue;        // Ending value of the tween
    private double currentValue;

    private Tween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, int repeatCount) {
        super(name, target, propertyName, duration, repeatCount);
        this.startValue = startValue;
        this.endValue = endValue;
    }

    @Override
    public String toString() {
        return "Tween(" + getName() + " | target: " + getTarget() + " | property name: " + getPropertyName() + " | start val: " + startValue + " | end val: " + endValue + " | current value: " + currentValue + " | duration: " + getDuration() + ")";
    }

    @SuppressWarnings("unused")
    public void update(double deltaTime) {
        addToElapsedTime(deltaTime);
        double t = Math.min(getElapsedTime() / getDuration(), 1.0f);  // Normalized time (0 to 1)

        currentValue = lerp(startValue, endValue, t);
        Updatable.setProperty(getTarget(), getPropertyName(), currentValue);

        if (ALLOW_PRINT && isComplete()) {
            Print.println(getName() + " tween is complete", Print.BLUE);
        }
    }
}