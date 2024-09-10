package gameplay.mapLoading;

import java.io.*;

import java.util.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import gameplay.gameObjects.GameObject.ObjectType;

public class GameLoader {

    final private static String ROOT_PATH = "res/levels";

    // create a game object given the primitive data
    public static GameObject createGameObject(JSONObject jsonObject, GameBoard gameBoard) {
        ObjectType objectType = GameObject.getObjectType(jsonObject.getString("name"));
        switch (objectType) {
            case PUZZLE_PIECE: return new PuzzlePiece(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData"));
            case PLAYER_PIECE: return new PlayerPiece(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData"));
            case WALL: return new Wall(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"));
            case EMPTY: return null;
            case OUT_OF_BOUNDS: return null;
            default: return null;
        }
    }

    // gets all the info to create a map from a json file
    public static MapInfo getMapInfo(String fileName, GameBoard gameBoard) {
        File file = new File(ROOT_PATH + "/" + fileName);
        try {
            int mapWidth = 0;
            int mapHeight = 0;
            ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject mapObject = new JSONObject(content);

            mapWidth = mapObject.getInt("mapWidth");
            mapHeight = mapObject.getInt("mapHeight");

            JSONArray gameObjectsData = mapObject.getJSONArray("gameObjects");

            for (int i=0; i<gameObjectsData.length(); i++) {
                gameObjects.add(createGameObject(gameObjectsData.getJSONObject(i), gameBoard));
            }

            return new MapInfo(mapWidth, mapHeight, gameObjects);
        } catch (IOException e) {
        }
        return null;
    }
}
