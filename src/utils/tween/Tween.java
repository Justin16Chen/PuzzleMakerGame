package utils.tween;

public class Tween extends Updatable {

    public static Tween createTween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration) {
        Tween tween = new Tween(name, target, propertyName, startValue, endValue, duration, 0, 0, false);
        Updatable.addUpdatable(tween);
        return tween;
    }

    private Number startValue;      // Starting value of the tween
    private Number endValue;        // Ending value of the tween
    private double currentValue;

    private Tween(String name, Object target, String propertyName, Number startValue, Number endValue, double duration, int currentLoopCount, int targetLoopCount, boolean pingPong) {
        super(name, target, propertyName, duration, currentLoopCount, targetLoopCount, pingPong);
        this.startValue = startValue;
        this.endValue = endValue;
        updateProperty(0);
    }

    public Number getStartValue() { return startValue; }
    public Number getEndValue() { return endValue;}

    public Tween setLoopCount(int loopCount) { targetLoopCount = loopCount; return this; }
    public Tween pingPong() { pingPong = true; return this; }
    public Tween setPrint(PrintType print) { this.print = print; return this; }

    @Override
    public String toString() {
        double time = Math.round(getElapsedTime() * 1000) / 1000.;
        double finishTime = Math.round(getDuration() * 1000) / 1000.;
        return "Tween(" + getName() + " | modifying "  + targetObject + "'s " + getPropertyName() + " | start: " + startValue + " | end: " + endValue + " | current: " + currentValue + " | " + time + "/" + finishTime + "sec | " + getCurrentLoop() + "/" + getTargetLoopCount() + " loops | ping pong: " + getPingPong() + " | loop complete: " + isLoopComplete() + " | complete: " + isComplete() + ")";

    }

    public void update(double deltaTime) {
        double t = Math.min(1, getElapsedTime() / getDuration());
        updateProperty(t);

        //if (name.equals("playerOutline"))
        //    System.out.println("target: " + getTarget());
    }

    // sets the property of the target object based on a normalized time (0 to 1)
    private void updateProperty(double t) {
        currentValue = lerp(startValue, endValue, t);
        Updatable.setProperty(getTarget(), getPropertyName(), currentValue);
    }

    @Override
    public void loop() {
        elapsedTime = 0;
        currentLoop++;
        if (pingPong) {
            pingPong = targetLoopCount < 0 || currentLoop == targetLoopCount - 1;
            Number temp = startValue;
            startValue = endValue;
            endValue = temp;
        }
    }
}