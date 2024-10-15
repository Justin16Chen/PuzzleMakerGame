package utils.drawing;

import java.awt.*;

public class SimpleSprite extends Sprite {

    public static SimpleSprite createSimpleSprite(String name, int x, int y, int width, int height) {
        SimpleSprite simpleSprite = new SimpleSprite(name, x, y, width, height, Color.BLACK);
        Sprite.addSprite(simpleSprite);
        return simpleSprite;
    }

    public static SimpleSprite createSimpleSprite(String name, int x, int y, int width, int height, Color color) {
        SimpleSprite simpleSprite = new SimpleSprite(name, x, y, width, height, color);
        Sprite.addSprite(simpleSprite);
        return simpleSprite;
    }

    private Color color;

    private SimpleSprite(String name, int x, int y, int width, int height, Color color) {
        super(name, x, y, width, height);
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g) {
        if (isVisible()) {
            g.setColor(color);
            g.fillRect(getX(), getY(), getWidth(), getHeight());
        }
    }
    
}
