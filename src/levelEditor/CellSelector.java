package levelEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.json.JSONObject;

import gameplay.gameObjects.GameBoard;
import gameplay.gameObjects.GameObject;
import gameplay.gameObjects.GameObjectData;
import gameplay.mapLoading.LevelLoader;
import utils.drawing.sprites.Sprite;
import utils.input.MouseInput;

public class CellSelector {
    private final static int SELECTED_CELL_STROKE = 4;
    private final static Color SELECTED_CELL_COLOR = new Color(230, 230, 230);
    
    private int startx, starty, curx, cury;
    private int left, top, width, height;
    private Sprite selectSprite;

    public CellSelector() {
    }

    public void setup() {
        selectSprite = new Sprite("tileSelect", "ui") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(SELECTED_CELL_COLOR);
                g.setStroke(new BasicStroke(SELECTED_CELL_STROKE));
                g.drawRect(getX() - SELECTED_CELL_STROKE / 2, getY() - SELECTED_CELL_STROKE / 2, getWidth() + SELECTED_CELL_STROKE, getHeight() + SELECTED_CELL_STROKE);
                g.setColor(Color.BLACK);
                g.drawString(left + " " + top + " " + width + " " + height, 300, 100);
            }
        };
    }

    public void update(MouseInput mouseInput, GameBoard board, Option selectedOption) {
        if (mouseInput.isOver(board.getBoardSprite())) {
            if (!mouseInput.released() && !mouseInput.down()) {
                curx = board.getBoardX(mouseInput.getX());
                cury = board.getBoardY(mouseInput.getY());
                startx = curx;
                starty = cury;
                selectSprite.setVisible(true);
            }
            else {
                curx = board.getBoardX(mouseInput.getX());
                cury = board.getBoardY(mouseInput.getY());
            }
            
            left = Math.min(curx, startx);
            top = Math.min(cury, starty);
            width = Math.abs(curx - startx) + 1;
            height = Math.abs(cury - starty) + 1;

            if (mouseInput.released()) {
                // clear area
                for (int y=top; y<top + height; y++)
                    for (int x=left; x<left + width; x++) {
                        System.out.println("clearing " + x + " " + y);
                            board.deleteGameObject(x, y);
                    }
                
                // tool does not have a game object - ignore
                if (selectedOption.getGameObject() == null) {

                }
                else 
                    addNonResizableGameObject(board, selectedOption.getGameObject(), left, top, width, height);

                board.resizeAndRepositionGameObjects();
                board.printGameObjects();
            }
            updateSprite(board, left, top, width, height);
        }
        else
            selectSprite.setVisible(false);
    }

    private void updateSprite(GameBoard board, int left, int top, int width, int height) {
        selectSprite.setX(board.getDrawX(left));
        selectSprite.setY(board.getDrawY(top));
        selectSprite.setWidth(board.getTileSize() * width);
        selectSprite.setHeight(board.getTileSize() * height);
    }

    private void addNonResizableGameObject(GameBoard board, GameObject selectedGameObject, int left, int top, int width, int height) {
        for (int y=top; y<top + height; y++)
            for (int x=left; x<left + width; x++) {
                JSONObject jsonGameObject = selectedGameObject.toJSONObject();
                jsonGameObject.remove("x");
                jsonGameObject.remove("y");
                jsonGameObject.put("x", x);
                jsonGameObject.put("y", y);
                GameObject gameObject = LevelLoader.createGameObject(jsonGameObject);
                gameObject.updateVisualsAtStart(board);
                board.addGameObject(gameObject);
            }
    }
}
