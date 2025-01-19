package utils.direction;

import gameplay.gameObjects.GameObject;

public class Directions {
    public static Direction[] DirectionList = getAllDirections();
    
    public static Direction[] getAllDirections() {
        return new Direction[] { Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT };
    }
    // gets the direction of a side based off of a vector
    // assumes that hdir and vdir are between -1 and 1
    // and at least 1 of them are not zero
    public static Direction getDirection(int hdir, int vdir) {
        if (hdir * hdir + vdir * vdir > 1)
            return Direction.INVALID;
        
        if (hdir == 0) {
            if (vdir == 1) { return Direction.DOWN; }
            return Direction.UP;
        }
        if (hdir == -1) { return Direction.LEFT; }
        return Direction.RIGHT;
    }
    public static Direction getDirection(int index) {
        switch (index) {
            case -1: return Direction.INVALID;
            case 0: return Direction.UP;
            case 1: return Direction.LEFT;
            case 2: return Direction.DOWN;
            case 3: return Direction.RIGHT;
            default: return Direction.INVALID;
        }
    }
    
    // gets the index based on the side
    public static int getMoveIndex(Direction direction) {
        switch (direction) {
            case INVALID: return -1;
            case UP: return 0;
            case LEFT: return 1;
            case DOWN: return 2;
            case RIGHT: return 3;
            default: return -1;
        }
    }
    public static int getDirectionX(Direction direction) {
        switch (direction) {
            case INVALID: return 0;
            case UP: return 0;
            case LEFT: return -1;
            case DOWN: return 0;
            case RIGHT: return 1; 
            default: return 0;
        }
    }
    public static int getDirectionY(Direction direction) {
        switch (direction) {
            case INVALID: return 0;
            case UP: return -1;
            case LEFT: return 0;
            case DOWN: return 1;
            case RIGHT: return 0; 
            default: return -1;
        }
    }
    public static Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case INVALID: return Direction.INVALID;
            case UP: return Direction.DOWN;
            case LEFT: return Direction.RIGHT;
            case DOWN: return Direction.UP;
            case RIGHT: return Direction.LEFT;
            default: return Direction.INVALID;
        }
    }
    public static int getOppositeDirectionIndex(int index) {
        switch (index) {
            case -1: return -1;
            case 0: return 2;
            case 1: return 3;
            case 2: return 0;
            case 3: return 1;
            default: return -1;
        }
    }

    // gets the direction from game object 1 to game object 2
    public static Direction getDirectionBetweenGameObjects(GameObject gameObject1, GameObject gameObject2) {
        Direction direction = Directions.getDirection(gameObject2.getBoardX() - gameObject1.getBoardX(), gameObject2.getBoardY() - gameObject1.getBoardY());
        if (direction == Direction.INVALID)
            throw new IllegalArgumentException(gameObject1 + " is not adjacent to " + gameObject2);
        return direction;
    }

    // returns if game object 1 is directly adjacent to game object 2
    public static boolean areGameObjectsAdjacent(GameObject gameObject1, GameObject gameObject2) {
        int hdir = gameObject2.getBoardX() - gameObject1.getBoardX();
        int vdir = gameObject2.getBoardY() - gameObject1.getBoardY();
        return hdir * hdir + vdir * vdir == 1;
    }
}