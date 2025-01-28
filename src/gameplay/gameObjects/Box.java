package gameplay.gameObjects;

import java.awt.Color;

import org.json.JSONObject;

import gameplay.GameBoard;
import utils.drawing.sprites.Sprite;

public class Box extends GameObject {

    public static GameObject loadBox(JSONObject jsonObject, GameBoard gameBoard) {
        int size = jsonObject.has("size") ? jsonObject.getInt("size") : 1;
        return new Box(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), size);
    }

    public static final Color COLOR = new Color(191, 130, 71), OUTLINE_COLOR = new Color(138, 80, 26);
    public static final double OUTLINE_WIDTH = 0.05, OUTLINE_OFFSET = 0.2, DIAGONAL_SIZE = 0.28;
    
    public Box(GameBoard gameBoard, int boardx, int boardy, int size) {
        super(gameBoard, GameObject.ObjectType.BOX, boardx, boardy, size, size);
    }
    
    @Override
    public void setup() {
        sprite = makeSprite(gameBoard.findGameObjectDrawX(this), gameBoard.findGameObjectDrawY(this), getCellWidth() * gameBoard.getTileSize(), getCellHeight() * gameBoard.getTileSize());
    }

    public static Sprite makeSprite(int x, int y, int width, int height) {
        return new Sprite("box", "res/textures/box.png", x, y, width, height, "gameObjects1");
    }
}
