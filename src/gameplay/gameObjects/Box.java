package gameplay.gameObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.json.JSONObject;

import gameplay.GameBoard;
import utils.drawing.SimpleSprite;

public class Box extends GameObject {

    public static final Color COLOR = new Color(132, 132, 132), OUTLINE_COLOR = new Color(132, 132, 132);
    public static final int OUTLINE_WIDTH = 2, OUTLINE_OFFSET = 8;
    public Box(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.ObjectType.BOX, boardx, boardy);
    }
    
    @Override
    public void setup() {
        sprite = new SimpleSprite("box", gameBoard.findGameObjectDrawX(this), gameBoard.findGameObjectDrawY(this), gameBoard.getTileSize(), gameBoard.getTileSize(), "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(COLOR);
                g.fillRect(getX(), getY(), getWidth(), getHeight());
                g.setColor(OUTLINE_COLOR);
                g.setStroke(new BasicStroke(OUTLINE_WIDTH));
                g.drawRect(getX() + OUTLINE_OFFSET, getY() + OUTLINE_OFFSET, getWidth() - OUTLINE_OFFSET * 2, getHeight() - OUTLINE_OFFSET * 2);
            }
        };
    }

    public static ArrayList<GameObject> loadBox(JSONObject jsonObject, GameBoard gameBoard) {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        int width = jsonObject.has("width") ? jsonObject.getInt("width") : 1;
        int height = jsonObject.has("height") ? jsonObject.getInt("height") : 1;
        for (int y=0; y<height; y++) 
            for (int x=0; x<width; x++) 
                gameObjects.add(new Box(gameBoard, jsonObject.getInt("x") + x, jsonObject.getInt("y") + y));
        return gameObjects;
    }
}
