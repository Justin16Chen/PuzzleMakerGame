package gameplay.mapLoading;

import java.util.*;

import gameplay.gameObjects.*;

public class LevelInfo {

    private int width, height;
    private  ArrayList<GameObject> gameObjects;
    
    public LevelInfo(int mapWidth, int mapHeight, ArrayList<GameObject> gameObjects) {
        this.width = mapWidth;
        this.height = mapHeight;
        this.gameObjects = gameObjects;
    }

    public String getGameObjectsString() {
        String gmString = "";

        for (GameObject gameObject : gameObjects) {
            gmString += gameObject + "\n";
        }
        return gmString;
    }

    @Override
    public String toString() {
        
        return "LevelInfo(\nwidth: " + width + " | height: " + height + " \ngameObjectPrimitives: " + getGameObjectsString() +")";
    }

    public int getMapWidth() { return width; }
    public int getMapHeight() { return height; }
    public ArrayList<GameObject> getGameObjects() { return gameObjects; }
}
