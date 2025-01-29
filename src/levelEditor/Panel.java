package levelEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import gameplay.gameObjects.GameObject;
import utils.drawing.sprites.Sprite;
import utils.input.MouseInput;

public class Panel {
    private final static int V_BORDER_PADDING = 70, H_BORDER_PADDING = 40;
    private final static int SELECTED_PADDING = 8;
    private final static int OPTION_SIZE = 32;
    private final static int OPTION_SPACING = 64;
    private final static int TEXT_SPACING = 10;
    private final static Color SELECT_COLOR = new Color(230, 230, 230);
    private MouseInput mouseInput;
    private int x, y;
    private int width, height;
    private ArrayList<GameObject> gameObjects;

    private Sprite panelSprite;
    private Color panelColor;
    private Sprite selectHighlightSprite;
    private GameObject selectedGameObject;

    public Panel(int x, int y, int width, int height, Color panelColor, MouseInput mouseInput) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.panelColor = panelColor;
        this.mouseInput = mouseInput;
    }

    public void setup() {
        panelSprite = new Sprite("panel", x, y, width, height, "ui") {
            @Override
            public void draw(Graphics2D g) {
                super.draw(g);
                g.setColor(SELECT_COLOR);
                g.drawString("Board Size", 7, 15);
                g.drawString("X", 39, 32);
            }
        };
        panelSprite.setColor(panelColor);
        selectHighlightSprite = new Sprite("selected option", x, y, 1, 1, "ui") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(SELECT_COLOR);
                for (GameObject gameObject : gameObjects) {
                    g.drawString(("" + gameObject.getObjectType()).toLowerCase(), x + H_BORDER_PADDING, gameObject.getSprite().getY() - TEXT_SPACING);
                    if (mouseInput.down() && mouseInput.isOver(gameObject.getSprite())) {
                        selectedGameObject = gameObject;
                    }
                }
                setWidth(selectedGameObject.getSprite().getWidth() + SELECTED_PADDING * 2);
                setHeight(selectedGameObject.getSprite().getHeight() + SELECTED_PADDING * 2);
                setCenterX(selectedGameObject.getSprite().getCenterX());
                setCenterY(selectedGameObject.getSprite().getCenterY());
                g.drawRect(getX(), getY(), getWidth(), getHeight());
            }
        };
        selectHighlightSprite.setVisible(true);
        
        for (int i=0; i<gameObjects.size(); i++) {
            Sprite sprite = gameObjects.get(i).getSprite();
            sprite.setX(x + H_BORDER_PADDING);
            sprite.setY(i * (OPTION_SPACING) + y + V_BORDER_PADDING);
            sprite.setWidth(OPTION_SIZE);
            sprite.setHeight(OPTION_SIZE);
            sprite.moveAllChildrenToLayer("ui");
            sprite.setAllChildrenVisible(false, "accessory"); // only show main part of sprite
        }
        selectedGameObject = gameObjects.get(0);
    }

    public void setOptions(ArrayList<GameObject> gameObjects) {
        System.out.println("resizing game object options");
        this.gameObjects = gameObjects;
        for (int i=0; i<gameObjects.size(); i++)
            gameObjects.get(i).setup(H_BORDER_PADDING, V_BORDER_PADDING + i * OPTION_SPACING, OPTION_SIZE, OPTION_SIZE);
    }

    public GameObject getSelectedGameObject() {
        return selectedGameObject;
    }
    
}
