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

    final public static boolean ALLOW_PRINT = false;

    final private static String ROOT_PATH = "res/levels";

    private static ArrayList<String> baseObjectData = new ArrayList<>();
    private static HashMap<String, ArrayList<String>> additionalObjectData = new HashMap<>();
    private static HashMap<String, ArrayList<String>> totalObjectData = new HashMap<>();

    public static boolean isObjectDataValid(JSONObject jsonObject) {
        if (!(jsonObject.has("name") && jsonObject.has("x") && jsonObject.has("y"))) {
            return false;
        }
        // name of json object
        String objectName = jsonObject.getString("name");
        // keys of json object
        Object[] objectKeys = jsonObject.keySet().toArray();
        ArrayList<String> requiredKeys = totalObjectData.get(objectName);

        if (ALLOW_PRINT) System.out.println("num required keys: " + requiredKeys.size() + " num keys provided: " + objectKeys.length);
        for (int i=0; i<requiredKeys.size(); i++) {

            // search for required key in json object's keys
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
        
        return true;
    }

    // gets the required object data
    public static void getRequiredObjectData(String fileName) {
        baseObjectData.clear();
        additionalObjectData.clear();

        File file = new File(ROOT_PATH + "/" + fileName);
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject objectData = new JSONObject(content);

            JSONArray baseObjectArray = objectData.getJSONArray("gameObject");

            // get data required for all game objects
            ArrayList<String> baseKeys = JSONArrayToStringArrayList(baseObjectArray);
            baseObjectData = baseKeys;

            // get game object specific requirements
            GameObject.ObjectType[] objectTypes = GameObject.ObjectType.values();
            for (int i=0; i<objectTypes.length; i++) {
                String name = GameObject.getObjectTypeName(objectTypes[i]);
                ArrayList<String> additionalKeys = JSONArrayToStringArrayList(objectData.getJSONArray(name));
                additionalObjectData.put(name, additionalKeys);

                // put base object data into total data
                totalObjectData.put(name, new ArrayList<String>());
                for (String key : baseKeys) {
                    totalObjectData.get(name).add(key);
                }

                // put additional object data into total data
                ArrayList<String> objKeys = totalObjectData.get(name);
                for (String additionalKey : additionalKeys) {
                    objKeys.add(additionalKey);
                }
            }
            
        } catch (JSONException e) {
            Print.println("INVALID JSON FILE", Print.RED);
        } catch (IOException e) {
            Print.println("COULD NOT FIND FILE", Print.RED);
            
        }
    }

    // convert JSONArray to ArrayList
    public static ArrayList<String> JSONArrayToStringArrayList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();
        
        for (int i=0; i<jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }

        return list;
    }
    // create a game object given the primitive data
    public static GameObject createGameObject(JSONObject jsonObject, GameBoard gameBoard) {
        ObjectType objectType = GameObject.getObjectType(jsonObject.getString("name"));
        switch (objectType) {
            case PUZZLE_PIECE: return new PuzzlePiece(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData"), jsonObject.getString("strengthData"));
            case PLAYER_PIECE: return new PlayerPiece(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData"), jsonObject.getString("strengthData"));
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
            int lastLevel;
            double transitionTime;

            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject levelObject = new JSONObject(content);

            lastLevel = levelObject.getInt("lastLevel");
            transitionTime = levelObject.getDouble("transitionTime");

            return new GeneralLevelInfo(lastLevel, transitionTime);
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
            
            for (int i=0; i<gameObjectsData.length(); i++) {
                JSONObject jsonObject = gameObjectsData.getJSONObject(i);
                if (!isObjectDataValid(jsonObject)) {
                    throw new JSONException("object at index " + i + " is invalid");
                }
                gameObjects.add(createGameObject(jsonObject, gameBoard));
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
            Print.println("CANNOT FIND FILE " + fileName, Print.RED);
            e.printStackTrace();
        } catch (JSONException e) {
            Print.println("INVALID JSON FILE", Print.RED);
            e.printStackTrace();
        }
        Print.println("RETURNING NULL FOR LEVEL INFO", Print.RED);
        return null;
    }
}
