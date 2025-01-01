package utils.tween;

public class Timer extends Updatable {

    public static Timer createSetTimer(String name, Object target, double duration, String propertyName, Object finalPropertyValue) {
        Timer timer = new Timer(name, target, duration, propertyName, finalPropertyValue, 0, 0);
        Updatable.addUpdatable(timer);
        return timer;
    }
    public static Timer createCallTimer(String name, Object target, double duration, String methodName, Object... args) {
        Timer timer = new Timer(name, target, duration, methodName, 0, 0, args);
        Updatable.addUpdatable(timer);
        return timer;
    }
    public static Timer resetSetTimerTo(String name, Object target, double duration, String propertyName, Object finalPropertyValue, int currentLoop, int targetLoopCount) {
        Timer timer = new Timer(name, target, duration, propertyName, finalPropertyValue, currentLoop, targetLoopCount);
        Updatable.addUpdatable(timer);
        return timer;
    }
    public static Timer resetCallTimerTo(String name, Object target, double duration, String methodName, int currentLoop, int targetLoopCount, Object... args) {
        Timer timer = new Timer(name, target, duration, methodName, currentLoop, targetLoopCount, args);
        Updatable.addUpdatable(timer);
        return timer;
    }

    private Object finalPropertyValue;

    public Timer(String name, Object target, double duration, String propertyName, Object finalPropertyValue, int currentLoopCount, int targetLoopCount) {
        super(name, target, propertyName, duration, currentLoopCount, targetLoopCount, false);
        this.finalPropertyValue = finalPropertyValue;
    }

    public Timer(String name, Object target, double duration, String methodName, int currentLoopCount, int targetLoopCount, Object... args) {
        super(name, target, methodName, duration, currentLoopCount, targetLoopCount, false, args);
    }

    public Timer setLoopCount(int loopCount) { targetLoopCount = loopCount; return this; }

    @Override
    public String toString() {
        if (getType() == Type.SET) {
            return "Timer(name: " + getName() + " | set property: " + getPropertyName() + " to " + finalPropertyValue + " | duration: " + getDuration() + " | elapsed time: " + getElapsedTime() + ")";
        }
        return "Timer(name: " + getName() + " | call function " + getMethodName() + " with " + getParameterTypesString(getMethodArgs()) + " | elapsed time: " + getElapsedTime() + "/" + getDuration() + " | loop complete: " + isLoopComplete() + " | complete: " + isComplete() + ")";
    }

    public Object getFinalPropertyValue() { return finalPropertyValue; }


    @Override
    public void update(double deltaTime) {
        if (isComplete())   
            if (getType() == Type.SET) 
                Updatable.setProperty(getTarget(), getPropertyName(), finalPropertyValue);
            else if (getType() == Type.CALL) 
                Updatable.callMethodByName(getTarget(), getMethodName(), getMethodArgs());
    }
}
