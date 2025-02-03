package gameplay.gameObjects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

import gameplay.gameObjects.GameObject.ObjectType;

public class GameObjectData {
    public static final String OBJECT_DATA_FILE_PATH = "res/properties/gameObjectData.json";
    private static JSONObject objectData;

    public static void loadObjectData() {
        File file = new File(OBJECT_DATA_FILE_PATH);
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            objectData = new JSONObject(content).getJSONObject("data");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    // get the string name of of the game object type
    public static String objectTypeToName(ObjectType objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return "puzzlePiece";
            case PLAYER_PIECE: return "playerPiece";
            case WALL: return "wall";
            case BOX: return "box";
            default: throw new IllegalArgumentException(objectType + " is not valid");
        }
    }
    // get the enum given the string name of the object type
    public static ObjectType nameToObjectType(String objectTypeName) {
        switch (objectTypeName) {
            case "puzzlePiece": return ObjectType.PUZZLE_PIECE;
            case "playerPiece": return ObjectType.PLAYER_PIECE;
            case "wall": return ObjectType.WALL;
            case "box": return ObjectType.BOX;
            default: throw new IllegalArgumentException(objectTypeName + " is not valid");
        }
    }
    
    // whether or not a game object is movable
    public static boolean isMovable(ObjectType objectType) {
        return objectData.getJSONObject(objectTypeToName(objectType)).getBoolean("movable");
    }
    public static boolean isResizable(ObjectType objectType) {
        return objectData.getJSONObject(objectTypeToName(objectType)).getBoolean("resizable");
    }
}
