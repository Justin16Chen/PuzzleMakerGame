package gameplay.gameObjects;

import java.awt.Color;

import org.json.JSONObject;

import utils.drawing.sprites.Sprite;

public class Box extends GameObject {

    public static GameObject loadBox(JSONObject jsonObject) {
        int size = jsonObject.has("size") ? jsonObject.getInt("size") : 1;
        return new Box(jsonObject.getInt("x"), jsonObject.getInt("y"), size);
    }

    public static final Color COLOR = new Color(191, 130, 71), OUTLINE_COLOR = new Color(138, 80, 26);
    public static final double OUTLINE_WIDTH = 0.05, OUTLINE_OFFSET = 0.2, DIAGONAL_SIZE = 0.28;
    
    public Box(int boardX, int boardY, int size) {
        super(GameObject.ObjectType.BOX, boardX, boardY, size, size);
    }
    
    @Override
    public void setup(int x, int y, int width, int height) {
        sprite = makeSprite(x, y, width, height);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();
        jsonObject.put("size", getCellWidth());
        return jsonObject;
    }

    public static Sprite makeSprite(int x, int y, int width, int height) {
        return new Sprite("box", "res/textures/box.png", x, y, width, height, "gameObjects1");
    }
}
