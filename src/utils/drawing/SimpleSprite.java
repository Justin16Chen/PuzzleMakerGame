package utils.drawing;

import java.awt.*;

public class SimpleSprite extends Sprite {

    private Color color;

    public SimpleSprite(String name, String layerName) {
        super(name, 0, 0, 1, 1, layerName);
        color = Color.BLACK;
    }

    public SimpleSprite(String name, int x, int y, String layerName) {
        super(name, x, y, 1, 1, layerName);
        this.color = Color.BLACK;
    }

    public SimpleSprite(String name, int x, int y, int width, int height, String layerName) {
        super(name, x, y, width, height, layerName);
        this.color = Color.BLACK;
    }

    public SimpleSprite setColor(Color color) {
        this.color = color;
        return this;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void draw(Graphics2D g) {
        if (isVisible()) {
            g.setColor(color);
            g.fillRect(getX(), getY(), getWidth(), getHeight());
        }
    }
    
}
