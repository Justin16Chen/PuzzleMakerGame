package utils.direction;

public class Directions {
    public static Direction[] DirectionList = getAllDirections();
    
    public static Direction[] getAllDirections() {
        return new Direction[] { Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT };
    }
    // gets the direction of a side based off of a vector
    // assumes that hdir and vdir are between -1 and 1
    // and at least 1 of them are not zero
    public static Direction getDirection(int hdir, int vdir) {
        if (hdir == 0) {
            if (vdir == 1) { return Direction.DOWN; }
            return Direction.UP;
        }
        if (hdir == -1) { return Direction.LEFT; }
        return Direction.RIGHT;
    }
    public static Direction getDirection(int index) {
        switch (index) {
            case 0: return Direction.UP;
            case 1: return Direction.LEFT;
            case 2: return Direction.DOWN;
            case 3: return Direction.RIGHT;
            default: return Direction.UP;
        }
    }
    
    // gets the index based on the side
    public static int getMoveIndex(Direction direction) {
        switch (direction) {
            case UP: return 0;
            case LEFT: return 1;
            case DOWN: return 2;
            case RIGHT: return 3;
            default: return 0;
        }
    }
    public static int getDirectionX(Direction direction) {
        switch (direction) {
            case UP: return 0;
            case LEFT: return -1;
            case DOWN: return 0;
            case RIGHT: return 1; 
            default: return 0;
        }
    }
    public static int getDirectionY(Direction direction) {
        switch (direction) {
            case UP: return -1;
            case LEFT: return 0;
            case DOWN: return 1;
            case RIGHT: return 0; 
            default: return 0;
        }
    }
    public static Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case UP: return Direction.DOWN;
            case LEFT: return Direction.RIGHT;
            case DOWN: return Direction.UP;
            case RIGHT: return Direction.LEFT;
            default: return Direction.UP;
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