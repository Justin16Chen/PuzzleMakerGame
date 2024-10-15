package utils;

public class Direction {
    public static enum Type {
        UP,
        LEFT,
        DOWN,
        RIGHT
    }
    public static Type[] DirectionList = { Type.UP, Type.LEFT, Type.DOWN, Type.RIGHT};
    
    // gets the direction of a side based off of a vector
    // assumes that hdir and vdir are between -1 and 1
    // and at least 1 of them are not zero
    public static Type getDirection(int hdir, int vdir) {
        if (hdir == 0) {
            if (vdir == 1) { return Type.DOWN; }
            return Type.UP;
        }
        if (hdir == -1) { return Type.LEFT; }
        return Type.RIGHT;
    }
    public static Type getDirection(int index) {
        switch (index) {
            case 0: return Type.UP;
            case 1: return Type.LEFT;
            case 2: return Type.DOWN;
            case 3: return Type.RIGHT;
            default: return Type.UP;
        }
    }
    
    // gets the index based on the side
    public static int getMoveIndex(Type direction) {
        switch (direction) {
            case UP: return 0;
            case LEFT: return 1;
            case DOWN: return 2;
            case RIGHT: return 3;
            default: return 0;
        }
    }
    public static int getDirectionX(Type direction) {
        switch (direction) {
            case UP: return 0;
            case LEFT: return -1;
            case DOWN: return 0;
            case RIGHT: return 1; 
            default: return 0;
        }
    }
    public static int getDirectionY(Type direction) {
        switch (direction) {
            case UP: return -1;
            case LEFT: return 0;
            case DOWN: return 1;
            case RIGHT: return 0; 
            default: return 0;
        }
    }
    public static Type getOppositeDirection(Type direction) {
        switch (direction) {
            case UP: return Type.DOWN;
            case LEFT: return Type.RIGHT;
            case DOWN: return Type.UP;
            case RIGHT: return Type.LEFT;
            default: return Type.UP;
        }
    }
    public static int getOppositeDirectionIndex(int index) {
        switch (index) {
            case 0: return 2;
            case 1: return 3;
            case 2: return 0;
            case 3: return 1;
            default: return 0;
        }
    }
}