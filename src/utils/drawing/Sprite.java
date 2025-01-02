package utils.drawing;

import java.awt.*;

public abstract class Sprite {

    private String name;
    private int x, y, width, height;
    private boolean visible;
    private String layerName;

    public Sprite(String name, int x, int y, int width, int height, String layerName) {
        Sprites.addSprite(this, layerName);
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layerName = layerName;
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public String getLayerName() { return layerName; }
    
    public void setVisible(boolean visible) { this.visible = visible; }

    public abstract void draw(Graphics2D g);
}
