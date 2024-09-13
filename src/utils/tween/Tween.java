package utils.tween;

import utils.Print;

public class Tween extends Updatable {

    public static void createTween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration) {
        Tween tween = new Tween(name, target, propertyName, startValue, endValue, duration, false);
        if (!Updatable.hasProperty(target, propertyName)) {
            Print.println(tween + " does not have property named " + propertyName, Print.RED);
        }
        Updatable.addUpdatable(tween);
    }
    public static void createTween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, boolean print) {
        Updatable.addUpdatable(new Tween(name, target, propertyName, startValue, endValue, duration, print));
    }
    private Number startValue;      // Starting value of the tween
    private Number endValue;        // Ending value of the tween
    private double currentValue;
    private boolean print;          // Print values for debugging

    private Tween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, boolean print) {
        super(name, "set", target, propertyName, duration);
        this.startValue = startValue;
        this.endValue = endValue;
        this.print = print;
    }

    @Override
    public String toString() {
        return "Tween(" + getName() + " | target: " + getTarget() + " | property name: " + getPropertyName() + " | start val: " + startValue + " | end val: " + endValue + " | current value: " + currentValue + " | duration: " + getDuration() + ")";
    }

    public void update(double deltaTime) {
        addToElapsedTime(deltaTime);
        double t = Math.min(getElapsedTime() / getDuration(), 1.0f);  // Normalized time (0 to 1)

        currentValue = lerp(startValue, endValue, t);
        Updatable.setProperty(getTarget(), getPropertyName(), currentValue);

        if (print) {
            System.out.println(this);
        }
    }
}