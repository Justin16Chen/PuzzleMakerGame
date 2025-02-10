package gameplay.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.JSONObject;

import gameplay.GameManager;
import utils.drawing.sprites.Sprite;

public class Wall extends GameObject {

    public static Color COLOR = GameManager.BG_COLOR;

    public static GameObject loadWall(JSONObject jsonObject) {
        int width = jsonObject.has("width") ? jsonObject.getInt("width") : 1;
        int height = jsonObject.has("height") ? jsonObject.getInt("height") : 1;
        return new Wall(jsonObject.getInt("x"), jsonObject.getInt("y"), width, height);
    }

    public Wall(int boardX, int boardY, int width, int height) {
        super(GameObject.ObjectType.WALL, boardX, boardY, width, height);
    }

    @Override
    public void setup(int x, int y, int width, int height) {
        sprite = new Sprite("wall", x, y, width, height, "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(COLOR);
                g.fillRect(getX(), getY(), getWidth(), getHeight());
            }
        };
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();
        jsonObject.put("width", getCellWidth());
        jsonObject.put("height", getCellHeight());
        return jsonObject;
    }
}
