package gameplay.mapLoading;

import java.io.*;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.*;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import gameplay.gameObjects.puzzlePiece.PlayerPiece;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import utils.Print;

public class LevelLoader {

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
    public static void updateObjectData(String filePath) {
        //System.out.println("GETTING OBJECT DATA");
        requiredObjectData.clear();
        optionalObjectData.clear();
        totalObjectData.clear();

        File file = new File(filePath);
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
                String name = GameObject.objectTypeToName(objectTypes[i]);
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
            Print.println("COULD NOT FIND FILE" + filePath, Print.RED);
            
        }
    }

    // convert JSONArray to ArrayList
    public static ArrayList<String> JSONArrayToStringArrayList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();
        
        for (int i=0; i<jsonArray.length(); i++) list.add(jsonArray.getString(i));
        return list;
    }

    // gets the general info about the levels
    public static GeneralLevelInfo getGeneralLevelInfo(String filePath) {
        try {
            return new GeneralLevelInfo(filePath);
        } catch (JSONException e) {
            Print.println(filePath + " is not formatted correctly", Print.RED);
        } catch (IOException e) {
            Print.println("COULD NOT FIND FILE" + filePath, Print.RED);
        }
        return null;
    }

    // gets all the info to create a map from a json file
    public static LevelInfo getLevelInfo(String filePath, GameBoard gameBoard) {

        File file = new File(filePath);
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
                GameObject gameObject = createGameObject(jsonObject, gameBoard);

                // only 1 gameobject can be on a position
                if (gameObject.getBoardX() < 0 || gameObject.getBoardX() >= mapWidth || gameObject.getBoardY() < 0 || gameObject.getBoardY() >= mapHeight)
                    throw new JSONException(gameObject + " tried to be instantiated out of bounds");
                if (gameObject.getCellWidth() < 0 || gameObject.getCellHeight() < 0)
                    throw new JSONException(gameObject + "cannot have a width and height of 0");
                if (filledPositions[gameObject.getBoardY()][gameObject.getBoardX()])
                    throw new JSONException(gameObject + " tried to be instantiated on a position that was already filled");

                filledPositions[gameObject.getBoardY()][gameObject.getBoardX()] = true;

                // add game object to list
                gameObjects.add(gameObject);
            }
            return new LevelInfo(mapWidth, mapHeight, gameObjects);
        } catch (IOException e) {
            Print.println("COULD NOT FIND FILE " + filePath, Print.RED);
        } catch (JSONException e) {
            Print.println("INVALID JSON FILE", Print.RED);
            e.printStackTrace();
        }
        return null;
    }

    // create a game object given the primitive data
    public static GameObject createGameObject(JSONObject jsonObject, GameBoard gameBoard) {
        GameObject.ObjectType objectType = GameObject.nameToObjectType(jsonObject.getString("name"));
        switch (objectType) {
            case WALL: return Wall.loadWall(jsonObject, gameBoard);
            case BOX: return Box.loadBox(jsonObject, gameBoard);
            case PUZZLE_PIECE: return PuzzlePiece.loadPuzzlePiece(jsonObject, gameBoard);
            case PLAYER_PIECE: return PlayerPiece.loadPlayerPiece(jsonObject, gameBoard);
            default: 
                Print.println("GAME OBJECT TYPE NOT RECOGNIZED: " + objectType, Print.RED);
                return null;
        }
    }
}
