package gameplay;

import java.awt.Color;
import java.awt.Font;

import utils.drawing.sprites.ButtonSprite;
import utils.input.Mouse;

public abstract class MainMenuManager {

    private ButtonSprite[] buttons;
    private Mouse mouse;

    public MainMenuManager(Mouse mouse) {
        this.mouse = mouse;
        buttons = new ButtonSprite[15];
    }
    public void setup(int windowWidth, int windowHeight, String layer) {
        int startX = (int) (windowWidth * 0.2);
        int startY = (int) (windowHeight * 0.3);
        int xSpacing = (int) (windowWidth * 0.1);
        int ySpacing = (int) (windowHeight * 0.15);
        int width = (int) (xSpacing * 0.5);
        int height = (int) (ySpacing * 0.5);
        for (int i=0; i<3; i++) {
            int y = startY + ySpacing * i;
            for (int j=0; j<5; j++) {
                int x = startX + xSpacing * j;
                int index = i * 5 + j;
                buttons[index] = new ButtonSprite(x, y, width, height, layer, mouse) {
                    @Override
                    public void onClick() {
                        onButtonClicked(index);
                    }
                };
                buttons[index].setText("" + (index + 1), Color.WHITE, new Font("Arial", Font.PLAIN, 15));
                buttons[index].setResizeAmount((int) (width * 0.3), 0.07);
            }
        }
    }

    public void resizeComponents(int windowWidth, int windowHeight) {
        int startX = (int) (windowWidth * 0.2);
        int startY = (int) (windowHeight * 0.3);
        int xSpacing = (int) (windowWidth * 0.1);
        int ySpacing = (int) (windowHeight * 0.15);
        int width = (int) (xSpacing * 0.5);
        int height = (int) (ySpacing * 0.5);
        for (int i=0; i<3; i++) {
            int y = startY + ySpacing * i;
            for (int j=0; j<5; j++) {
                int x = startX + xSpacing * j;
                int index = i * 3 + j;
                ButtonSprite b = buttons[index];
                b.setX(x);
                b.setY(y);
                b.setWidth(width);
                b.setHeight(height);
            }
        }
    }

    // ranges from [0, 29]
    public abstract void onButtonClicked(int buttonIndex);
}
