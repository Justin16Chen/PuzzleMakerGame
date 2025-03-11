package gameplay.mapLoading;

import java.util.*;

import gameplay.gameObjects.*;

public class LevelInfo {

    private String[] instructions;
    private int width, height;
    private  ArrayList<GameObject> gameObjects;
    
    public LevelInfo(String[] instructions, int mapWidth, int mapHeight, ArrayList<GameObject> gameObjects) {
        this.instructions = instructions;
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

    public String[] getInstructions() { return instructions; }
    public int getMapCols() { return width; }
    public int getMapRows() { return height; }
    public ArrayList<GameObject> getGameObjects() { return gameObjects; }
}
