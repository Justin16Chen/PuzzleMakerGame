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
    private ArrayList<Option> options;

    private Sprite panelSprite;
    private Color panelColor;
    private Sprite selectHighlightSprite;
    private Option selectedOption;

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
                for (Option option : options) {
                    g.drawString(("" + option.getName()).toLowerCase(), x + H_BORDER_PADDING, option.getSprite().getY() - TEXT_SPACING);
                    if (mouseInput.down() && mouseInput.isOver(option.getSprite())) {
                        selectedOption = option;
                    }
                }
                setWidth(selectedOption.getSprite().getWidth() + SELECTED_PADDING * 2);
                setHeight(selectedOption.getSprite().getHeight() + SELECTED_PADDING * 2);
                setCenterX(selectedOption.getSprite().getCenterX());
                setCenterY(selectedOption.getSprite().getCenterY());
                g.drawRect(getX(), getY(), getWidth(), getHeight());
            }
        };
        
        for (int i=0; i<options.size(); i++) {
            options.get(i).setup(x + H_BORDER_PADDING, i * (OPTION_SPACING) + y + V_BORDER_PADDING, OPTION_SIZE, OPTION_SIZE);
            Sprite sprite = options.get(i).getSprite();
            sprite.moveAllChildrenToLayer("ui");
            sprite.setAllChildrenVisible(false, "accessory"); // only show main part of sprite
        }
        selectedOption = options.get(0);
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    public Option getSelectedOption() {
        return selectedOption;
    }
}
