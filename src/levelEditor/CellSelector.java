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
            int boardX = board.getBoardX(mouseInput.getX());
            int boardY = board.getBoardY(mouseInput.getY());
            selectSprite.setVisible(true);
            selectSprite.setX(board.getDrawX(boardX));
            selectSprite.setY(board.getDrawY(boardY));
            selectSprite.setWidth(board.getTileSize());
            selectSprite.setHeight(board.getTileSize());

            if (mouseInput.down()) {
                if (board.getGameObject(boardX, boardY) != null)
                    board.deleteGameObject(board.getGameObject(boardX, boardY));
                JSONObject jsonGameObject = selectedGameObject.toJSONObject();
                jsonGameObject.remove("x");
                jsonGameObject.remove("y");
                jsonGameObject.put("x", boardX);
                jsonGameObject.put("y", boardY);
                GameObject gameObject = LevelLoader.createGameObject(jsonGameObject);
                gameObject.updateVisualsAtStart(board);
                board.addGameObject(gameObject);
            }
        }
        else
            selectSprite.setVisible(false);
    }
}
