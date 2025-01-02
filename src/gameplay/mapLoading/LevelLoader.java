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

    private static HashMap<String, ArrayList<String>> requiredObjectData = new HashMap<>();
    private static HashMap<String, ArrayList<String>> optionalObjectData = new HashMap<>();
    private static HashMap<String, ArrayList<String>> totalObjectData    = new HashMap<>();

    public static boolean isObjectDataValid(JSONObject jsonObject) {
        if (!(jsonObject.has("name") && jsonObject.has("x") && jsonObject.has("y"))) {
            return false;
        }
        // name of json object
        String objectName = jsonObject.getString("name");
        // keys of json object
        Object[] objectKeys = jsonObject.keySet().toArray();
        ArrayList<String> requiredKeys = requiredObjectData.get(objectName);

        // make sure object has all required keys
        for (int i=0; i<requiredKeys.size(); i++) {
            boolean foundKey = false;
            for (int j=0; j<objectKeys.length; j++) {
                String objectKey = (String) objectKeys[j];
                if (objectKey.equals(requiredKeys.get(i))) {
                    foundKey = true;
                    break;
                }
            }

            // if key is not found, the object data is invalid
            if (!foundKey) {
                Print.println(requiredKeys.get(i) + " is not found", Print.RED);
                return false;
            }
        }

        // make sure object doesn't have any unknown keys
        ArrayList<String> allKnownKeys = totalObjectData.get(objectName);
        for (int i=0; i<objectKeys.length; i++) {
            boolean foundKey = false;
            for (String key : allKnownKeys) {
                if (objectKeys[i].equals(key)) {
                    foundKey = true;
                    break;
                }
            }
            if (!foundKey) {
                Print.println(objectKeys[i] + " is an unknown key", Print.RED);
                return false;
            }
        }
        return true;
    }

    // gets the required object data
    public static void getObjectData(String fileName) {
        //System.out.println("GETTING OBJECT DATA");
        requiredObjectData.clear();
        optionalObjectData.clear();
        totalObjectData.clear();

        File file = new File(ROOT_PATH + "/" + fileName);
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject objectData = new JSONObject(content);
            JSONObject requiredData = objectData.getJSONObject("required"), optionalData = objectData.getJSONObject("optional");

            // get data required for all game objects
            ArrayList<String> baseObjectData = JSONArrayToStringArrayList(requiredData.getJSONArray("gameObject"));
            HashMap<String, ArrayList<String>> uniqueObjectData = new HashMap<>();

            // get game object specific requirements
            GameObject.ObjectType[] objectTypes = GameObject.ObjectType.values();
            for (int i=0; i<objectTypes.length; i++) {
                String name = GameObject.getObjectTypeName(objectTypes[i]);
                ArrayList<String> uniqueRequiredKeys = JSONArrayToStringArrayList(requiredData.getJSONArray(name));
                ArrayList<String> uniqueOptionalKeys = JSONArrayToStringArrayList(optionalData.getJSONArray(name));
                uniqueObjectData.put(name, uniqueRequiredKeys);

                // put base object data and unique object data into required data
                requiredObjectData.put(name, new ArrayList<String>());
                for (String key : baseObjectData) requiredObjectData.get(name).add(key);
                for (String key : uniqueRequiredKeys) requiredObjectData.get(name).add(key);
                
                // put optional object data into optional data
                optionalObjectData.put(name, uniqueOptionalKeys);

                // combine to make total object data
                totalObjectData.put(name, new ArrayList<String>());
                for (String key : requiredObjectData.get(name)) totalObjectData.get(name).add(key);
                for (String key : optionalObjectData.get(name)) totalObjectData.get(name).add(key);
            }
            
        } catch (JSONException e) {
            Print.println("INVALID JSON FILE", Print.RED);
            e.printStackTrace();
        } catch (IOException e) {
            Print.println("COULD NOT FIND FILE", Print.RED);
            
        }
    }

    // convert JSONArray to ArrayList
    public static ArrayList<String> JSONArrayToStringArrayList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();
        
        for (int i=0; i<jsonArray.length(); i++) list.add(jsonArray.getString(i));
        return list;
    }

    // gets the general info about the levels
    public static GeneralLevelInfo getGeneralLevelInfo(String fileName) {

        File file = new File(ROOT_PATH + "/" + fileName);
        try {
            int startLevel, lastLevel;
            double transitionTime;

            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject levelObject = new JSONObject(content);

            startLevel = levelObject.getInt("startLevel");
            lastLevel = levelObject.getInt("lastLevel");
            transitionTime = levelObject.getDouble("transitionTime");

            return new GeneralLevelInfo(startLevel, lastLevel, transitionTime);
        } catch (JSONException e) {
            Print.println(fileName + " is not formatted correctly", Print.RED);
        } catch (IOException e) {
            Print.println("COULD NOT FIND FILE", Print.RED);
        }
        Print.println("RETURNING NULL FOR LEVEL INFO", Print.RED);
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
            boolean[][] filledPositions = new boolean[mapHeight][mapWidth];
            
            // add gameobjects from json file to array
            for (int i=0; i<gameObjectsData.length(); i++) {
                JSONObject jsonObject = gameObjectsData.getJSONObject(i);

                // make sure all gameobjects have the required keys
                if (!isObjectDataValid(jsonObject)) 
                    throw new JSONException("object at index " + i + " is invalid");
                
                // json gameobject data creates a list of game objects
                for (GameObject gameObject : createGameObjects(jsonObject, gameBoard)) {
                    // only 1 gameobject can be on a position
                    if (gameObject.getBoardX() < 0 || gameObject.getBoardX() >= mapWidth || gameObject.getBoardY() < 0 || gameObject.getBoardY() >= mapHeight)
                        throw new JSONException(gameObject + " tried to be instantiated out of bounds");
                    if (filledPositions[gameObject.getBoardY()][gameObject.getBoardX()])
                        throw new JSONException(gameObject + " tried to be instantiated on a position that was already filled");
                    filledPositions[gameObject.getBoardY()][gameObject.getBoardX()] = true;

                    gameObjects.add(gameObject);
                }
            }
            return new LevelInfo(mapWidth, mapHeight, gameObjects);
        } catch (IOException e) {
            Print.println("CANNOT FIND FILE " + fileName, Print.RED);
            e.printStackTrace();
        } catch (JSONException e) {
            Print.println("INVALID JSON FILE", Print.RED);
            e.printStackTrace();
        }
        Print.println("RETURNING NULL FOR LEVEL INFO", Print.RED);
        return null;
    }

    // create a game object given the primitive data
    public static ArrayList<GameObject> createGameObjects(JSONObject jsonObject, GameBoard gameBoard) {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        ObjectType objectType = GameObject.getObjectType(jsonObject.getString("name"));
        switch (objectType) {
            case PUZZLE_PIECE: 
                String strengths = jsonObject.has("strengthData") ? jsonObject.getString("strengthData") : "ssss";
                gameObjects.add(new PuzzlePiece(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData"), strengths)); 
                break;
            case PLAYER_PIECE: 
                strengths = jsonObject.has("strengthData") ? jsonObject.getString("strengthData") : "ssss";
                gameObjects.add(new PlayerPiece(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData"), strengths));
                break;
            case WALL:
                int width = jsonObject.has("width") ? jsonObject.getInt("width") : 1;
                int height = jsonObject.has("height") ? jsonObject.getInt("height") : 1;
                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        gameObjects.add(new Wall(gameBoard, jsonObject.getInt("x") + x, jsonObject.getInt("y") + y));
                    }
                }
                break;
            default: break;
        }
        return gameObjects;
    }
}
