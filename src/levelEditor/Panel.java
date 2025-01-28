package levelEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import gameplay.gameObjects.GameObject;
import utils.drawing.sprites.Sprite;
import utils.input.MouseInput;

public class Panel {
    private static int V_BORDER_PADDING = 24, H_BORDER_PADDING = 12;
    private static int SELECTED_PADDING = 4;
    private static int OPTION_PADDING = 8;
    private static int OPTION_SIZE = 32;
    private MouseInput mouseInput;
    private int x, y;
    private int width, height;
    private ArrayList<GameObject> gameObjects;

    private Sprite panelSprite;
    private Color panelColor;
    private Sprite selectedSprite;

    public Panel(int x, int y, int width, int height, Color panelColor, MouseInput mouseInput) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.panelColor = panelColor;
        this.mouseInput = mouseInput;
    }

    public void setup() {
        panelSprite = new Sprite("panel", x, y, width, height, "default");
        panelSprite.setColor(panelColor);
        selectedSprite = new Sprite("selected option", x, y, 1, 1, "default") {
            @Override
            public void draw(Graphics2D g) {
                for (GameObject gameObject : gameObjects)
                    if (mouseInput.isOver(gameObject.getSprite())) {
                        setWidth(gameObject.getSprite().getWidth() + SELECTED_PADDING * 2);
                        setHeight(gameObject.getSprite().getHeight() + SELECTED_PADDING * 2);
                        setCenterX(gameObject.getSprite().getCenterX());
                        setCenterY(gameObject.getSprite().getCenterY());
                        setVisible(true);
                    }
            }
        };
        selectedSprite.setVisible(false);
        
        for (int i=0; i<gameObjects.size(); i++) {
            Sprite sprite = gameObjects.get(i).getSprite();
            sprite.setX(x + H_BORDER_PADDING);
            sprite.setY(i * (OPTION_SIZE + OPTION_PADDING) + y + V_BORDER_PADDING);
            sprite.setWidth(OPTION_SIZE);
            sprite.setHeight(OPTION_SIZE);
            sprite.moveToLayer("ui");
            System.out.println(i + ": " + sprite);
        }
    }

    public void setDrawSprites(ArrayList<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }
    
}
