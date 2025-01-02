package utils.drawing;

import java.awt.*;

public class SimpleSprite extends Sprite {

    private Color color;

    public SimpleSprite(String name, int x, int y, Color color, String layerName) {
        super(name, x, y, 1, 1, layerName);
        this.color = color;
    }

    public SimpleSprite(String name, int x, int y, int width, int height, Color color, String layerName) {
        super(name, x, y, width, height, layerName);
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
