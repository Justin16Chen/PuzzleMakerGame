package gameplay.gameObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.json.JSONObject;

import gameplay.GameBoard;
import utils.drawing.SimpleSprite;

public class Box extends GameObject {

    public static final Color COLOR = new Color(191, 130, 71), OUTLINE_COLOR = new Color(138, 80, 26);
    public static final int OUTLINE_WIDTH = 2, OUTLINE_OFFSET = 12, DIAGONAL_SIZE = 15;
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
                
                g.setStroke(new BasicStroke(DIAGONAL_SIZE));
                g.setColor(OUTLINE_COLOR);
                g.drawLine(getX() + OUTLINE_OFFSET, getY() + OUTLINE_OFFSET, getX() + getWidth() - OUTLINE_OFFSET, getY() + getHeight() - OUTLINE_OFFSET);

                g.setStroke(new BasicStroke(DIAGONAL_SIZE - OUTLINE_WIDTH * 2));
                g.setColor(COLOR);
                g.drawLine(getX() + OUTLINE_OFFSET, getY() + OUTLINE_OFFSET, getX() + getWidth() - OUTLINE_OFFSET, getY() + getHeight() - OUTLINE_OFFSET);

                g.setStroke(new BasicStroke(DIAGONAL_SIZE));
                g.setColor(OUTLINE_COLOR);
                g.drawLine(getX() + getWidth() - OUTLINE_OFFSET, getY() + OUTLINE_OFFSET, getX() + OUTLINE_OFFSET, getY() + getHeight() - OUTLINE_OFFSET);

                g.setStroke(new BasicStroke(DIAGONAL_SIZE - OUTLINE_WIDTH * 2));
                g.setColor(COLOR);
                g.drawLine(getX() + getWidth() - OUTLINE_OFFSET, getY() + OUTLINE_OFFSET, getX() + OUTLINE_OFFSET, getY() + getHeight() - OUTLINE_OFFSET);

                g.setStroke(new BasicStroke(OUTLINE_OFFSET));
                g.setColor(OUTLINE_COLOR);
                g.drawRect(getX() + OUTLINE_OFFSET / 2, getY() + OUTLINE_OFFSET / 2, getWidth() - OUTLINE_OFFSET, getWidth() - OUTLINE_OFFSET);
                g.setStroke(new BasicStroke(OUTLINE_OFFSET - OUTLINE_WIDTH));
                g.setColor(COLOR);
                g.drawRect(getX() + OUTLINE_OFFSET / 2 - OUTLINE_WIDTH / 2, getY() + OUTLINE_OFFSET / 2 - OUTLINE_WIDTH / 2, getWidth() - OUTLINE_OFFSET + OUTLINE_WIDTH, getWidth() - OUTLINE_OFFSET + OUTLINE_WIDTH);

                /*
                for (int i=0; i<4; i++) {
                    int[] xList = new int[4];
                    int[] yList = new int[4];
                    switch(i) {
                        case 0:
                            xList = new int[] { OUTLINE_OFFSET + DIAGONAL_SIZE, getWidth() / 2, getWidth() - OUTLINE_OFFSET - DIAGONAL_SIZE };
                            yList = new int[] { OUTLINE_OFFSET, getWidth() / 2 - DIAGONAL_SIZE, OUTLINE_OFFSET };
                            break;
                        case 1:
                            xList = new int[] { OUTLINE_OFFSET, getWidth() / 2 - DIAGONAL_SIZE, OUTLINE_OFFSET };
                            yList = new int[] { OUTLINE_OFFSET + DIAGONAL_SIZE, getWidth() / 2, getWidth() - OUTLINE_OFFSET - DIAGONAL_SIZE };
                            break;
                        case 2:
                            xList = new int[] { OUTLINE_OFFSET + DIAGONAL_SIZE, getWidth() / 2, getWidth() - OUTLINE_OFFSET - DIAGONAL_SIZE };
                            yList = new int[] { getWidth() - OUTLINE_OFFSET, getWidth() / 2 + DIAGONAL_SIZE, getWidth() - OUTLINE_OFFSET };
                            break;
                        case 3:
                            xList = new int[] { getWidth() - OUTLINE_OFFSET, getWidth() / 2 + DIAGONAL_SIZE, getWidth() - OUTLINE_OFFSET };
                            yList = new int[] { OUTLINE_OFFSET + DIAGONAL_SIZE, getWidth() / 2, getWidth() - OUTLINE_OFFSET - DIAGONAL_SIZE };
                            break;
                    }
    
                    for (int j=0; j<3; j++) {
                        xList[j] += getX();
                        yList[j] += getY();
                    }
                    g.setStroke(new BasicStroke(OUTLINE_WIDTH));
                    g.drawPolygon(xList, yList, 3);
                }
                */

                /*
                g.drawLine(getX() + OUTLINE_OFFSET + DIAGONAL_SIZE, getY() + OUTLINE_OFFSET,getX() +  getWidth() - OUTLINE_OFFSET, getY() + getHeight() - OUTLINE_OFFSET - DIAGONAL_SIZE);
                g.drawLine(getX() + OUTLINE_OFFSET, getY() + OUTLINE_OFFSET + DIAGONAL_SIZE, getX() +  getWidth() - OUTLINE_OFFSET - DIAGONAL_SIZE, getY() + getHeight() - OUTLINE_OFFSET);
                g.drawLine(getX() + getWidth() - OUTLINE_OFFSET - DIAGONAL_SIZE, getY() + OUTLINE_OFFSET, getX() + OUTLINE_OFFSET, getY() + getWidth() - OUTLINE_OFFSET - DIAGONAL_SIZE);
                g.drawLine(getX() + getWidth() - OUTLINE_OFFSET, getY() + OUTLINE_OFFSET + DIAGONAL_SIZE, getX() + OUTLINE_OFFSET + DIAGONAL_SIZE, getY() + getWidth() - OUTLINE_OFFSET);
                */
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
