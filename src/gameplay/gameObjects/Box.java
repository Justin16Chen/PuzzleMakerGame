package gameplay.gameObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.json.JSONObject;

import gameplay.GameBoard;
import utils.drawing.SimpleSprite;

public class Box extends GameObject {

    public static GameObject loadBox(JSONObject jsonObject, GameBoard gameBoard) {
        int width = jsonObject.has("width") ? jsonObject.getInt("width") : 1;
        int height = jsonObject.has("height") ? jsonObject.getInt("height") : 1;
        return new Box(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), width, height);
    }

    public static final Color COLOR = new Color(191, 130, 71), OUTLINE_COLOR = new Color(138, 80, 26);
    public static final double OUTLINE_WIDTH = 0.05, OUTLINE_OFFSET = 0.2, DIAGONAL_SIZE = 0.28;
    
    public Box(GameBoard gameBoard, int boardx, int boardy, int width, int height) {
        super(gameBoard, GameObject.ObjectType.BOX, boardx, boardy, width, height);
    }
    
    @Override
    public void setup() {
        sprite = new SimpleSprite("box", gameBoard.findGameObjectDrawX(this), gameBoard.findGameObjectDrawY(this), getCellWidth() * gameBoard.getTileSize(), getCellHeight() * gameBoard.getTileSize(), "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(COLOR);
                g.fillRect(getX(), getY(), getWidth(), getHeight());

                int diagonalSize = (int) (DIAGONAL_SIZE * getCellWidth() * gameBoard.getTileSize());
                int outlineOffset = (int) (OUTLINE_OFFSET * getCellWidth() * gameBoard.getTileSize());
                int outlineWidth = (int) (OUTLINE_WIDTH * getCellWidth() * gameBoard.getTileSize());
                
                // draw diagonal lines
                g.setStroke(new BasicStroke(diagonalSize));
                g.setColor(OUTLINE_COLOR);
                g.drawLine(getX() + outlineOffset, getY() + outlineOffset, getX() + getWidth() - outlineOffset, getY() + getHeight() - outlineOffset);
                g.setStroke(new BasicStroke(diagonalSize - outlineWidth * 2));
                g.setColor(COLOR);
                g.drawLine(getX() + outlineOffset, getY() + outlineOffset, getX() + getWidth() - outlineOffset, getY() + getHeight() - outlineOffset);

                g.setStroke(new BasicStroke(diagonalSize));
                g.setColor(OUTLINE_COLOR);
                g.drawLine(getX() + getWidth() - outlineOffset, getY() + outlineOffset, getX() + outlineOffset, getY() + getHeight() - outlineOffset);
                g.setStroke(new BasicStroke(diagonalSize - outlineWidth * 2));
                g.setColor(COLOR);
                g.drawLine(getX() + getWidth() - outlineOffset, getY() + outlineOffset, getX() + outlineOffset, getY() + getHeight() - outlineOffset);

                // draw rectangular border 
                g.setStroke(new BasicStroke(outlineOffset));
                g.setColor(OUTLINE_COLOR);
                g.drawRect(getX() + outlineOffset / 2, getY() + outlineOffset / 2, getWidth() - outlineOffset, getWidth() - outlineOffset);
                g.setStroke(new BasicStroke(outlineOffset - outlineWidth));
                g.setColor(COLOR);
                g.drawRect(getX() + outlineOffset / 2 - outlineWidth / 2, getY() + outlineOffset / 2 - outlineWidth / 2, getWidth() - outlineOffset + outlineWidth, getWidth() - outlineOffset + outlineWidth);
        
                
            }
        };
    }
}
