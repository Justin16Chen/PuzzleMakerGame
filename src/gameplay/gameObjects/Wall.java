package gameplay.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.GameManager;
import utils.drawing.SimpleSprite;

public class Wall extends GameObject {

    public static Color COLOR = GameManager.BG_COLOR;

    public static GameObject loadWall(JSONObject jsonObject, GameBoard gameBoard) {
        int width = jsonObject.has("width") ? jsonObject.getInt("width") : 1;
        int height = jsonObject.has("height") ? jsonObject.getInt("height") : 1;
        return new Wall(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), width, height);
    }

    public Wall(GameBoard gameBoard, int boardx, int boardy, int width, int height) {
        super(gameBoard, GameObject.ObjectType.WALL, boardx, boardy, width, height);
    }

    @Override
    public void setup() {
        sprite = new SimpleSprite("wall", gameBoard.findGameObjectDrawX(this), gameBoard.findGameObjectDrawY(this), getCellWidth() * gameBoard.getTileSize(), getCellHeight() * gameBoard.getTileSize(), "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(COLOR);
                g.fillRect(getX(), getY(), getWidth(), getHeight());
            }
        };
    }
}
