package gameplay.gameObjects;

import java.util.*;

public class GameObjectPrimitive {
    private String name;
    private int x, y;
    private HashMap<String, String> objectInfo;

    public GameObjectPrimitive(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.objectInfo = new HashMap<String, String>();
    }

    public GameObjectPrimitive(String name, int x, int y, HashMap<String, String> objectInfo) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.objectInfo = objectInfo;
    }

    @Override
    public String toString() {
        return "GameObjectPrimitive(name: " + name + " | x: " + x + " | y: " + y + ")";
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public HashMap<String, String> getObjectInfo() { return objectInfo; }

}
