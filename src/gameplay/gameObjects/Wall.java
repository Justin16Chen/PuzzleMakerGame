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

    public Wall(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.ObjectType.WALL, boardx, boardy);
    }

    @Override
    public void setup() {
        sprite = new SimpleSprite("wall", gameBoard.findGameObjectDrawX(this), gameBoard.findGameObjectDrawY(this), gameBoard.getTileSize(), gameBoard.getTileSize(), "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(COLOR);
                g.fillRect(getX(), getY(), gameBoard.getTileSize(), gameBoard.getTileSize());
            }
        };
    }

    public static ArrayList<GameObject> loadWall(JSONObject jsonObject, GameBoard gameBoard) {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        int width = jsonObject.has("width") ? jsonObject.getInt("width") : 1;
        int height = jsonObject.has("height") ? jsonObject.getInt("height") : 1;
        for (int y=0; y<height; y++) 
            for (int x=0; x<width; x++) 
                gameObjects.add(new Wall(gameBoard, jsonObject.getInt("x") + x, jsonObject.getInt("y") + y));
        return gameObjects;
    }
}
