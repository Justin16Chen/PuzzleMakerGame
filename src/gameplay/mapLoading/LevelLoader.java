package gameplay.mapLoading;

import java.io.*;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.*;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import gameplay.gameObjects.GameObject.ObjectType;
import gameplay.gameObjects.puzzlePiece.PlayerPiece;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import utils.Print;

public class LevelLoader {

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

    // gets the general info about the levels
    public static GeneralLevelInfo getGeneralLevelInfo(String fileName) {

        File file = new File(ROOT_PATH + "/" + fileName);
        try {
            int numLevels;
            double transitionTime;

            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject levelObject = new JSONObject(content);

            numLevels = levelObject.getInt("numLevels");
            transitionTime = levelObject.getDouble("transitionTime");

            return new GeneralLevelInfo(numLevels, transitionTime);
        } catch (JSONException e) {
            Print.println(fileName + " is not formatted correctly", Print.RED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // gets all the info to create a map from a json file
    public static LevelInfo getLevelInfo(String fileName, GameBoard gameBoard) {

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
                // TODO: check JSON data to make sure all the neccesary info is in the json object
                gameObjects.add(createGameObject(gameObjectsData.getJSONObject(i), gameBoard));
            }

            for (int i=0; i<gameObjects.size(); i++) {
                for (int j=0; j<gameObjects.size(); j++) {
                    if (i == j) continue;
                    GameObject gameObject = gameObjects.get(i);
                    GameObject gameObject2 = gameObjects.get(j);
                    if (gameObject.getBoardX() == gameObject2.getBoardX() && gameObject.getBoardY() == gameObject2.getBoardY()) {
                        throw new RuntimeException(gameObject + " has the same position as " + gameObject2);
                    }
                }
            }   
            return new LevelInfo(mapWidth, mapHeight, gameObjects);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("INVALID JSON FILE");
        }
        return null;
    }
}
