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
        visible = true;
    }

    @Override
    public boolean equals(Object obj) {
        return getName().equals(((Sprite) obj).getName());
    }
    @Override
    public String toString() {
        String visibleString = visible ? "shown" : "hidden"; 
        return "Sprite(" + name + "(" +  x + ", " + y + ") | " + width + "x" + height + " | " + layerName + " layer | " + visibleString + ")";
    }

    public String getName() { return name; }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getCenterX() { return x + (int) (width * 0.5);}
    public void setCenterX(int cx) { x = cx - (int) (width * 0.5); }
    public int getCenterY() { return y + (int) (height * 0.5);}
    public void setCenterY(int cy) { y = cy - (int) (height * 0.5); }
    public int getRight() { return x + width; }
    public void setRight(int right) { x = right - width; }
    public int getBottom() { return y + height; }
    public void setBottom(int bottom) { y = bottom - height; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public String getLayerName() { return layerName; }

    public abstract void draw(Graphics2D g);
}
