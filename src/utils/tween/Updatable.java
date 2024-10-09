package utils.tween;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import utils.Print;

public abstract class Updatable {

    final public static boolean ALLOW_PRINT = false;

    public enum PrintType {
        ALWAYS,
        NEVER,
        ON_COMPLETE,
        ON_LOOP
    }
    private static ArrayList<Updatable> list = new ArrayList<>();

    public static void addUpdatable(Updatable updatable) {
        boolean valid = true;
        if (updatable.getType().equals("call")) {
            if (!Updatable.hasMethod(updatable.getTarget(), updatable.getMethodName(), updatable.getMethodArgs())) {
                valid = false;
                throw new RuntimeException("method " + updatable.getMethodName() + " does not exist in " + updatable.getTarget() + " with parameters " + Updatable.getParameterTypesString(updatable.getMethodArgs()));
            }
        } 
        else if (updatable.getType().equals("set")) {
            if (!Updatable.hasProperty(updatable.getTarget(), updatable.getPropertyName())) {
                valid = false;
                throw new RuntimeException(updatable + " does not have property named " + updatable.getPropertyName());
            }
        }

        if (valid) {
            if (ALLOW_PRINT) Print.println("VALID: " + updatable, Print.GREEN);
            list.add(updatable);
        } else {
            Print.println("INVALID: " + updatable, Print.RED);
        }
    }

    @SuppressWarnings("unused")
    public static void updateUpdatables(double dt) {
        for (int i=0; i<list.size(); i++) {

            // make sure it doesn't go past list
            if (i >= list.size()) {
                break;
            }

            Updatable updatable = list.get(i);

            // update updatable
            updatable.step(dt);

            // print updatable
            if (ALLOW_PRINT && canPrintUpdatable(updatable)) {
                System.out.println("PRINTING UPDATABLE: " + updatable);
            }

            // remove updatable
            if (updatable.isComplete()) {
                list.remove(i);
                i--;
            }
        }
    }

    public static void clearUpdatables() {
        list.clear();
    }

    public static void deleteUpdatables(String stringToLookFor) {
        for (int i=0; i<list.size(); i++) {

            // make sure it doesn't go past list
            if (i >= list.size()) {
                break;
            }

            Updatable updatable = list.get(i);
            if (updatable.getName().contains(stringToLookFor)) {
                list.remove(i);
                i--;
            }
        } 
    }

    private static boolean canPrintUpdatable(Updatable updatable) {
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

    public static boolean hasProperty(Object target, String propertyName) {
        Field field = null;
        Class<?> clazz = target.getClass();

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

    public static void setProperty(Object target, String propertyName, Object value) {
        try {
            Field field = null;
            Class<?> clazz = target.getClass();
    
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
                field.setFloat(target, ((Number) value).floatValue());  // Set float value
            } else if (propertyType == int.class || propertyType == Integer.class) {
                field.setInt(target, ((Number) value).intValue());  // Set int value
            } else if (propertyType == double.class || propertyType == Double.class) {
                field.setDouble(target, ((Number) value).doubleValue());  // Set double value
            } else if (propertyType == long.class || propertyType == Long.class) {
                field.setLong(target, ((Number) value).longValue());  // Set long value
            } else if (propertyType == short.class || propertyType == Short.class) {
                field.setShort(target, ((Number) value).shortValue());  // Set short value
            } else if (propertyType == byte.class || propertyType == Byte.class) {
                field.setByte(target, ((Number) value).byteValue());  // Set byte value
            } else if (propertyType == boolean.class || propertyType == Boolean.class) {
                field.setBoolean(target, (Boolean) value);  // Set boolean value
            } else if (propertyType == char.class || propertyType == Character.class) {
                field.setChar(target, (Character) value);  // Set char value
            } else {
                // For other types or objects, use the general set method
                field.set(target, value);  // Set the value of the field
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

    private String name;
    private String type;
    private Object target;         // The object to be tweened
    private String propertyName;   // The name of the property to modify
    private String methodName;
    private Object[] methodArgs;
    private double duration;        // Duration of the tween
    private double elapsedTime;     // Time elapsed since the tween started
    private boolean paused;         // if the updatable is paused or not
    private int repeatCount;
    private int currentRepeat;
    private boolean loopFinished;     // if the updatable just finished looping  
    private boolean complete;         // if the updatable is fully complete
    private PrintType print;          // Print values for debugging

    public Updatable(String name, Object target, String propertyName, double duration, int repeatCount) {
        this.name = name;
        this.type = "set";
        this.target = target;
        this.propertyName = propertyName;
        this.duration = duration;
        this.repeatCount = repeatCount;
        this.elapsedTime = 0;
        this.currentRepeat = 0;
        this.print = PrintType.NEVER;
    }
    public Updatable(String name, Object target, String methodName, double duration, int repeatCount, Object...methodArgs) {
        this.name = name;
        this.type = "call";
        this.target = target;
        this.methodName = methodName;
        this.duration = duration;
        this.repeatCount = repeatCount;
        this.methodArgs = methodArgs;
        this.elapsedTime = 0;
        this.currentRepeat = 0;
        this.print = PrintType.NEVER;
    }
    @Override
    public String toString() {
        return "Updatable(name: " + name + " | type: " + type + " | property: " + propertyName + " | duration" + duration + " | elapsed time:" + elapsedTime + ")";
    }

    public void step(double deltaTime) {
        if (paused) {
            return;
        }

        if (elapsedTime >= duration) {
            currentRepeat++;
            loopFinished = true;
            if (repeatCount > 0) {
                elapsedTime -= duration;
                complete = currentRepeat >= repeatCount + 1;
            }
            else {
                complete = true;
            }
        }

        update(deltaTime);

        loopFinished = false;
    }
    protected abstract void update(double deltaTime);

    public String getName() { return name; }
    public String getType() { return type; }
    public Object getTarget() { return target; }
    public String getPropertyName() { return propertyName; }
    public String getMethodName() { return methodName; }
    public Object[] getMethodArgs() { return methodArgs; }
    public double getDuration() { return duration; }
    public double getElapsedTime() { return elapsedTime; }
    public boolean isPaused() { return paused; }
    public int getRepeatCount() { return repeatCount; }
    public boolean isLoopComplete() { return loopFinished; }
    public boolean isComplete() { return complete; }
    public PrintType getPrint() { return print; }

    public void setElapsedTime(double num) { elapsedTime = num; }
    public void addToElapsedTime(double num) { elapsedTime += num; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public void addToRepeat(int num) { currentRepeat += num; }
    public void complete() { complete = true; }
    public void setPrint(PrintType print) { this.print = print; }

}
