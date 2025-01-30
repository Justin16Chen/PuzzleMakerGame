package levelEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.gameObjects.GameObject;
import gameplay.mapLoading.LevelLoader;
import utils.drawing.sprites.Sprite;
import utils.input.MouseInput;

public class CellSelector {
    private final static int SELECTED_CELL_STROKE = 4;
    private final static Color SELECTED_CELL_COLOR = new Color(230, 230, 230);
    
    private int left, top, width, height;
    private Sprite selectSprite;

    public CellSelector() {
        left = 0;
        top = 0;
        width = 1;
        height = 1;
    }

    public void setup() {
        selectSprite = new Sprite("tileSelect", "ui") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(SELECTED_CELL_COLOR);
                g.setStroke(new BasicStroke(SELECTED_CELL_STROKE));
                g.drawRect(getX() - SELECTED_CELL_STROKE / 2, getY() - SELECTED_CELL_STROKE / 2, getWidth() + SELECTED_CELL_STROKE, getHeight() + SELECTED_CELL_STROKE);
            }
        };
    }
    public int getLeft() {
        return left;
    }
    public int getTop() {
        return top;
    }
    public int getRight() {
        return left + width;
    }
    public int getBottom() {
        return top + height;
    }

    public void update(MouseInput mouseInput, GameBoard board, GameObject selectedGameObject) {
        if (mouseInput.isOver(board.getBoardSprite())) {
            int boardx = board.getBoardX(mouseInput.getX());
            int boardy = board.getBoardY(mouseInput.getY());
            selectSprite.setVisible(true);
            selectSprite.setX(board.getDrawX(boardx));
            selectSprite.setY(board.getDrawY(boardy));
            selectSprite.setWidth(board.getTileSize());
            selectSprite.setHeight(board.getTileSize());

            if (mouseInput.down()) {
                if (board.getGameObject(boardx, boardy) != null)
                    board.deleteGameObject(board.getGameObject(boardx, boardy));
                JSONObject jsonGameObject = selectedGameObject.toJSONObject();
                jsonGameObject.remove("x");
                jsonGameObject.remove("y");
                jsonGameObject.put("x", boardx);
                jsonGameObject.put("y", boardy);
                board.addGameObject(LevelLoader.createGameObject(jsonGameObject, board));
                board.setupGameObjectVisuals();
            }
        }
        else
            selectSprite.setVisible(false);
    }
}
