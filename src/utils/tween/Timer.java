package utils.tween;

import utils.Print;

public class Timer extends Updatable {

    public static void createSetTimer(String name, Object target, double duration, String propertyName, Object finalPropertyValue) {
        Updatable.addUpdatable(new Timer(name, target, duration, propertyName, finalPropertyValue));
    }
    public static void createCallTimer(String name, Object target, double duration, String methodName, Object... args) {
        Timer timer = new Timer(name, target, duration, methodName, args);
        if (!Updatable.hasMethod(target, methodName, args)) {
            Print.println("method " + methodName + " does not exist in " + target + " with parameters " + Updatable.getParameterTypesString(args), Print.RED);
        }
        Updatable.addUpdatable(timer);
    }

    private Object finalPropertyValue;
    private Object[] args;  // Changed from Object to Object[]

    public Timer(String name, Object target, double duration, String propertyName, Object finalPropertyValue) {
        super(name, "set", target, propertyName, duration);
        this.finalPropertyValue = finalPropertyValue;
    }

    public Timer(String name, Object target, double duration, String methodName, Object... args) {
        super(name, "call", target, methodName, duration);
        this.args = args;
    }

    @Override
    public String toString() {
        if (getType().equals("set")) {
            return "Timer(name: " + getName() + " | set property: " + getPropertyName() + " to " + finalPropertyValue + " | duration: " + getDuration() + " | elapsed time: " + getElapsedTime() + ")";
        }
        return "Timer(name: " + getName() + " | call function " + getPropertyName() + " with " + getParameterTypesString(args) + " | duration: " + getDuration() + " | elapsed time: " + getElapsedTime() + ")";
    }

    @Override
    public void update(double deltaTime) {
        addToElapsedTime(deltaTime);

        if (isComplete()) {
            if (getType().equals("set")) {
                Updatable.setProperty(getTarget(), getPropertyName(), finalPropertyValue);
            } 
            else if (getType().equals("call")) {
                //System.out.println(this);
                //System.out.println("is finished");
                Updatable.callMethodByName(getTarget(), getPropertyName(), args);
            }
        }
    }
}
