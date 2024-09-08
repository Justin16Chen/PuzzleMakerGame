package gameplay.MapLoading;

import java.util.*;

import gameplay.gameObjects.*;

public class MapInfo {

    private int width, height;
    private ArrayList<GameObjectPrimitive> gameObjectPrimitives;
    
    public MapInfo(int mapWidth, int mapHeight, ArrayList<GameObjectPrimitive> gameObjectPrimitives) {
        this.width = mapWidth;
        this.height = mapHeight;
        this.gameObjectPrimitives = gameObjectPrimitives;
    }

    @Override
    public String toString() {
        String gmString = "";

        for (GameObjectPrimitive gmPrimitive : gameObjectPrimitives) {
            gmString += gmPrimitive + "\n";
        }
        return "MapInfo(\nwidth: " + width + " | height: " + height + " \ngameObjectPrimitives: " + gmString +")";
    }

    public int getMapWidth() { return width; }
    public int getMapHeight() { return height; }
    public ArrayList<GameObjectPrimitive> getGameObjects() { return gameObjectPrimitives; }
}
