package utils.tween;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import utils.Print;

public abstract class Updatable {

    public static boolean ALLOW_PRINT = true;

    public enum PrintType {
        ALWAYS,
        NEVER,
        ON_COMPLETE,
        ON_LOOP
    }
    
    public enum Type {
        SET, CALL
    }

    private static ArrayList<Updatable> list = new ArrayList<>();

    public static int getUpdatableAmount() { return list.size(); }

    public static ArrayList<Updatable> getUpdatables() { return list; }

    public static void addUpdatable(Updatable updatable) {
        boolean valid = true;
        if (updatable.getType() == Type.CALL) {
            if (!Updatable.hasMethod(updatable.getTarget(), updatable.getMethodName(), updatable.getMethodArgs())) {
                valid = false;
                throw new RuntimeException("method " + updatable.getMethodName() + " does not exist in " + updatable.getTarget() + " with parameters " + Updatable.getParameterTypesString(updatable.getMethodArgs()));
            }
        } 
        else if (updatable.getType() == Type.SET) {
            if (!Updatable.hasProperty(updatable.getTarget(), updatable.getPropertyName())) {
                valid = false;
                throw new RuntimeException(updatable + " does not have property named " + updatable.getPropertyName());
            }
        }

        if (valid) {
            if (ALLOW_PRINT) 
                Print.println("VALID: " + updatable, Print.GREEN);
            list.add(updatable);
        } else {
            Print.println("INVALID: " + updatable, Print.RED);
        }
    }

    public static void updateUpdatables(double dt) {
        ArrayList<Updatable> newList = new ArrayList<>();
        ArrayList<Updatable> copyList = new ArrayList<>(list);
        for (int i=0; i<copyList.size(); i++) {
            Updatable updatable = copyList.get(i);

            // update updatable
            updatable.step(dt);

            // print updatable
            if (canPrintUpdatable(updatable)) 
                System.out.println(updatable.getPrint() + " idx " + i + ": " + updatable);
            

            // loop updatable
            if (updatable.isLoopComplete() && !updatable.isComplete()) {
                int newLoopCount = updatable.getTargetLoopCount() > 0 ? updatable.getCurrentLoop() + 1 : updatable.getCurrentLoop();
                PrintType printType = updatable.getPrint();

                if (canPrintUpdatable(updatable)) 
                    Print.println("refreshing " + updatable, Print.GREEN);
                
                if (updatable.getClass() == Tween.class) {
                    Tween tween = (Tween) updatable;
                    Number startValue = updatable.getPingPong() ? tween.getEndValue() : tween.getStartValue();
                    Number endValue = updatable.getPingPong() ? tween.getStartValue() : tween.getEndValue();
                    boolean pingPong = updatable.getPingPong() ? updatable.getTargetLoopCount() < 0 ? true : updatable.getCurrentLoop() < updatable.getTargetLoopCount() - 1 : false;
                    updatable = Tween.resetTweenTo(updatable.getName(), updatable.getTarget(), updatable.getPropertyName(), startValue, endValue, updatable.getDuration(), newLoopCount, updatable.getTargetLoopCount(), pingPong);
                }
                else if (updatable.getClass() == Timer.class) 
                    if (updatable.getType() == Type.CALL)
                        updatable = Timer.resetCallTimerTo(updatable.getName(), updatable.getTarget(), updatable.getDuration(), updatable.getMethodName(), newLoopCount, updatable.getTargetLoopCount(), updatable.getMethodArgs());
                    else if (updatable.getType() == Type.SET)
                        updatable = Timer.resetSetTimerTo(updatable.getName(), updatable.getTarget(), updatable.getDuration(), updatable.getPropertyName(), ((Timer) updatable).getFinalPropertyValue(), newLoopCount, updatable.getTargetLoopCount());
                
                updatable.setPrint(printType);
            }
            // only add updatable if it isn't complete
            if (!updatable.isComplete()) 
                newList.add(updatable);
            else if (canPrintUpdatable(updatable))
                Print.println(updatable.getName() + " is complete", Print.BLUE);
        }
        list = newList;
    }

    public static void clearUpdatables() {
        list.clear();
    }

    public static Updatable getUpdatable(String name) {
        for (Updatable updatable : list)
            if (updatable.getName().equals(name))
                return updatable;
        return null;
    }
    public static Updatable getUpdatable(ArrayList<Updatable> list, String name) {
        for (Updatable updatable : list)
            if (updatable.getName().equals(name))
                return updatable;
        return null;
    }

    public static void deleteUpdatables(String[] names) {
        ArrayList<Updatable> newList = new ArrayList<>();
        for (Updatable updatable : new ArrayList<>(list)) {
            boolean seenInList = false;
            for (String name : names)
                if (updatable.getName().equals(name)) {
                    seenInList = true;
                    break;
                }
            if (!seenInList)
                newList.add(updatable);
        }
        list = newList;
    }

    // removes all updatables except the one with a matching name
    public static void deleteAllUpdatablesExcept(String[] exceptions) {
        ArrayList<Updatable> newList = new ArrayList<>();
        for (Updatable updatable : new ArrayList<>(list))
            for (String exception : exceptions) 
                if (updatable.getName().equals(exception)) {
                    newList.add(updatable);
                    break;
                }
        list = newList;
    }

    private static boolean canPrintUpdatable(Updatable updatable) {
        if (!ALLOW_PRINT) 
            return false;
        switch (updatable.getPrint()) {
            case ALWAYS:
                return true;
            case NEVER:
                return false;
            case ON_COMPLETE:
                return updatable.isComplete();
            case ON_LOOP:
                return updatable.isLoopComplete();
            default:
                return false;
        }
    }
    public static double lerp(Number start, Number end, Number t) {
        return start.doubleValue() + t.doubleValue() * (end.doubleValue() - start.doubleValue());
    }

    public static boolean hasProperty(Object targetObject, String propertyName) {
        Field field = null;
        Class<?> clazz = targetObject.getClass();

        // Look for the field in the class hierarchy
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(propertyName);
                break;  // Field found, break out of the loop
            } catch (NoSuchFieldException e) {
                // Field not found in this class, try the superclass
                clazz = clazz.getSuperclass();
            }
        }
        return field != null;
    }

    public static Object getProperty(Object targetObject, String propertyName) {
        try {
            Field field = null;
            Class<?> clazz = targetObject.getClass();
    
            // Look for the field in the class hierarchy
            while (clazz != null) {
                try {
                    field = clazz.getDeclaredField(propertyName);
                    break;  // Field found, break out of the loop
                } catch (NoSuchFieldException e) {
                    // Field not found in this class, try the superclass
                    clazz = clazz.getSuperclass();
                }
            }
    
            if (field == null) {
                throw new NoSuchFieldException("Field " + propertyName + " not found in class hierarchy.");
            }
    
            field.setAccessible(true);  // Make the field accessible if it's private
    
            return field.get(targetObject);  // Get the value of the field
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setProperty(Object targetObject, String propertyName, Object value) {
        try {
            Field field = null;
            Class<?> clazz = targetObject.getClass();
    
            // Look for the field in the class hierarchy
            while (clazz != null) {
                try {
                    field = clazz.getDeclaredField(propertyName);
                    break;  // Field found, break out of the loop
                } catch (NoSuchFieldException e) {
                    // Field not found in this class, try the superclass
                    clazz = clazz.getSuperclass();
                }
            }
    
            if (field == null) {
                throw new NoSuchFieldException("Field " + propertyName + " not found in class hierarchy.");
            }
    
            field.setAccessible(true);  // Make the field accessible if it's private
    
            Class<?> propertyType = field.getType();  // Get the field's type
    
            // Check for primitive types and their wrappers
            if (propertyType == float.class || propertyType == Float.class) {
                field.setFloat(targetObject, ((Number) value).floatValue());  // Set float value
            } else if (propertyType == int.class || propertyType == Integer.class) {
                field.setInt(targetObject, ((Number) value).intValue());  // Set int value
            } else if (propertyType == double.class || propertyType == Double.class) {
                field.setDouble(targetObject, ((Number) value).doubleValue());  // Set double value
            } else if (propertyType == long.class || propertyType == Long.class) {
                field.setLong(targetObject, ((Number) value).longValue());  // Set long value
            } else if (propertyType == short.class || propertyType == Short.class) {
                field.setShort(targetObject, ((Number) value).shortValue());  // Set short value
            } else if (propertyType == byte.class || propertyType == Byte.class) {
                field.setByte(targetObject, ((Number) value).byteValue());  // Set byte value
            } else if (propertyType == boolean.class || propertyType == Boolean.class) {
                field.setBoolean(targetObject, (Boolean) value);  // Set boolean value
            } else if (propertyType == char.class || propertyType == Character.class) {
                field.setChar(targetObject, (Character) value);  // Set char value
            } else {
                // For other types or objects, use the general set method
                field.set(targetObject, value);  // Set the value of the field
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Class<?>[] getParameterTypes(Object...args) {

        // Determine parameter types and handle primitive type conversion
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Integer) {
                parameterTypes[i] = int.class;
            } else if (args[i] instanceof Double) {
                parameterTypes[i] = double.class;
            } else if (args[i] instanceof Float) {
                parameterTypes[i] = float.class;
            } else if (args[i] instanceof Long) {
                parameterTypes[i] = long.class;
            } else if (args[i] instanceof Boolean) {
                parameterTypes[i] = boolean.class;
            } else if (args[i] instanceof Character) {
                parameterTypes[i] = char.class;
            } else if (args[i] instanceof Byte) {
                parameterTypes[i] = byte.class;
            } else if (args[i] instanceof Short) {
                parameterTypes[i] = short.class;
            } else {
                // For non-primitive types, use the class of the object
                parameterTypes[i] = args[i].getClass();
            }
        }
        return parameterTypes;
    }

    public static boolean hasMethod(Object obj, String methodName, Object...args) {
        try {
            // get the parameter types
            Class<?>[] parameterTypes = getParameterTypes(args);
            // will throw an exception if there doesn't exist a method with the parameters specified
            obj.getClass().getDeclaredMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            Print.println("Method not found: " + methodName + " with parameters: " + getParameterTypesString(args) + " in " + obj.getClass(), Print.RED);
            return false;
        }
    }

    public static void callMethodByName(Object obj, String methodName, Object... args) {
        try {
            // Determine parameter types and handle primitive type conversion
            Class<?>[] parameterTypes = getParameterTypes(args);
    
            // Get the method with the specified parameter types
            Method method = obj.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);

            // Invoke the method
            method.invoke(obj, args);
    
        } catch (NoSuchMethodException e) {
            Print.println("Method not found: " + methodName + " with parameters: " + getParameterTypesString(args) + " in " + obj.getClass(), Print.RED);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Converts the parameter types to strings (for error logging)
    public static String getParameterTypesString(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(arg.getClass().getSimpleName());
        }
        return sb.toString();
    }

    protected String name;
    protected Type type;
    protected Object targetObject;         // The object to be updatable
    protected String propertyName;   // The name of the property to modify
    protected String methodName;
    protected Object[] methodArgs;
    protected double duration;        // Duration of the updatable
    protected double elapsedTime;     // Time elapsed since the updatable started
    protected int currentLoop;        // Current loop count
    protected int targetLoopCount;          // Number of times to loop the updatable
    protected boolean pingPong;
    protected boolean paused;         // if the updatable is paused or not
    protected PrintType print;          // Print values for debugging

    public Updatable(String name, Object targetObject, String propertyName, double duration, int currentLoopCount, int targetLoopCount, boolean pingPong) {
        this.name = name;
        this.type = Type.SET;
        this.targetObject = targetObject;
        this.propertyName = propertyName;
        this.duration = duration;
        this.currentLoop = currentLoopCount;
        this.targetLoopCount = targetLoopCount;
        this.pingPong = pingPong;
        this.elapsedTime = 0;
        this.print = PrintType.NEVER;
    }

    public Updatable(String name, Object targetObject, String methodName, double duration, int currentLoopCount, int targetLoopCount, boolean pingPong, Object...methodArgs) {
        this.name = name;
        this.type = Type.CALL;
        this.targetObject = targetObject;
        this.methodName = methodName;
        this.duration = duration;
        this.currentLoop = currentLoopCount;
        this.targetLoopCount = targetLoopCount;
        this.pingPong = pingPong;
        this.methodArgs = methodArgs;
        this.elapsedTime = 0;
        this.print = PrintType.NEVER;
    }

    @Override
    public String toString() {
        return "Updatable(name: " + name + " | type: " + type + " | property: " + propertyName + " | duration" + duration + " | elapsed time:" + elapsedTime + ")";
    }

    public void step(double deltaTime) {
        if (paused) 
            return;

        elapsedTime += deltaTime;

        update(deltaTime);
    }

    protected abstract void update(double deltaTime);

    public String getName() { return name; }
    public Type getType() { return type; }
    public Object getTarget() { return targetObject; }
    public String getPropertyName() { return propertyName; }
    public String getMethodName() { return methodName; }
    public Object[] getMethodArgs() { return methodArgs; }
    public double getDuration() { return duration; }
    public double getElapsedTime() { return elapsedTime; }
    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public boolean isLoopComplete() { return elapsedTime >= duration; }
    public int getCurrentLoop() { return currentLoop; }
    public int getTargetLoopCount() { return targetLoopCount; }
    public boolean getPingPong() { return pingPong; }
    public PrintType getPrint() { return print; }
    public boolean isComplete() { return currentLoop >= targetLoopCount && elapsedTime >= duration && targetLoopCount >= 0 && !pingPong; }
    public void setPrint(PrintType print) { this.print = print; }

}
