package utils.tween;

import utils.Print;

public class Timer extends Updatable {

    public static Timer createSetTimer(String name, Object target, double duration, String propertyName, Object finalPropertyValue, int repeatCount) {
        Timer timer = new Timer(name, target, duration, propertyName, finalPropertyValue, repeatCount);
        Updatable.addUpdatable(timer);
        return timer;
    }
    public static Timer createCallTimer(String name, Object target, double duration, String methodName, int repeatCount, Object... args) {
        Timer timer = new Timer(name, target, duration, methodName, repeatCount, args);
        Updatable.addUpdatable(timer);
        return timer;
    }

    private Object finalPropertyValue;

    public Timer(String name, Object target, double duration, String propertyName, Object finalPropertyValue, int repeatCount) {
        super(name, target, propertyName, duration, repeatCount);
        this.finalPropertyValue = finalPropertyValue;
    }

    public Timer(String name, Object target, double duration, String methodName, int repeatCount, Object... args) {
        super(name, target, methodName, duration, repeatCount, args);
    }

    @Override
    public String toString() {
        if (getType().equals("set")) {
            return "Timer(name: " + getName() + " | set property: " + getPropertyName() + " to " + finalPropertyValue + " | duration: " + getDuration() + " | elapsed time: " + getElapsedTime() + ")";
        }
        return "Timer(name: " + getName() + " | call function " + getMethodName() + " with " + getParameterTypesString(getMethodArgs()) + " | elapsed time: " + getElapsedTime() + "/" + getDuration() + " | loop complete: " + isLoopComplete() + " | complete: " + isComplete() + ")";
    }

    @Override
    public void update(double deltaTime) {
        addToElapsedTime(deltaTime);

        if (getRepeatCount() >= 0 && isComplete() || getRepeatCount() < 0 && isLoopComplete()) {
            Print.println(this.getName() + " is doing action", Print.BLUE);
            doAction();
        }
        
    }

    private void doAction() {
        if (getType().equals("set")) {
            Updatable.setProperty(getTarget(), getPropertyName(), finalPropertyValue);
        } 
        else if (getType().equals("call")) {
            Updatable.callMethodByName(getTarget(), getMethodName(), getMethodArgs());
        }
    }
}
