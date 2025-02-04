package utils.drawing.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import utils.Print;

public class Sprite extends TaggableChild<Sprite> {

    private String name, layerName;
    private String imagePath;
    private Image image;
    private int x, y, width, height;
    private boolean visible;
    private Color color;

    public Sprite(String name, String layerName) {
        this.name = name;
        this.layerName = layerName;
        
        x = 0;
        y = 0;
        width = 1;
        height = 1;
        imagePath = "";
        image = null;
        visible = true;
        color = Color.BLACK;
        Sprites.addSprite(this, layerName);
    }

    public Sprite(String name, String imagePath, String layerName) {
        this.name = name;
        this.imagePath = imagePath;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            Print.println("Failed to load image " + imagePath, Print.RED);
        }
        this.layerName = layerName;

        x = 0;
        y = 0;
        width = 1;
        height = 1;
        visible = true;
        color = Color.BLACK;
        Sprites.addSprite(this, layerName);
    }
    public Sprite(String name, int x, int y, int width, int height, String layerName) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layerName = layerName;

        imagePath = "";
        image = null;
        visible = true;
        color = Color.BLACK;
        Sprites.addSprite(this, layerName);
    }
    public Sprite(String name, String imagePath, int x, int y, int width, int height, String layerName) {
        this.name = name;
        setImagePath(imagePath);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layerName = layerName;

        visible = true;
        color = Color.BLACK;
        Sprites.addSprite(this, layerName);
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

    public void setVisible(boolean visible) { 
        this.visible = visible; 
    }
    public String getLayerName() { return layerName; }
    
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public Image getImage() { return image; }
    public void setImagePath(String imagePath) { 
        this.imagePath = imagePath; 
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            Print.println("Failed to load image " + imagePath, Print.RED);
        }
    }
    public void setImage(Image image) {
        this.image = image;
    }

    // only moves this to layerName
    public void moveToLayer(String layerName) {
        Sprites.addSprite(this, layerName);
        Sprites.deleteSprite(this);
    }
    // sets the visibility of this and all of its children
    public void setAllChildrenVisible(boolean visible) { 
        setVisible(visible);
        for (Sprite child : getDirectChildren())
            child.setAllChildrenVisible(visible);
    }
    // sets the visibility of this and all of its children with matching tag
    public void setAllChildrenVisible(boolean visible, String tag) {
        if (hasTag(tag))
            setVisible(visible);
        for (Sprite child : getDirectChildren())
            child.setAllChildrenVisible(visible, tag);
    }
    // moves this and all children to layerName
    public void moveAllChildrenToLayer(String layerName) {
        moveToLayer(layerName);
        for (Sprite child : getDirectChildren())
            child.moveAllChildrenToLayer(layerName);
    }
    
    public void draw(Graphics2D g) {
        if (isVisible()) {
            if (image == null) {
                g.setColor(color);
                g.fillRect(getX(), getY(), getWidth(), getHeight());
            }
            else 
                g.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
        }
    }

    @Override
    public boolean equals(Object obj) {
        Sprite sprite = (Sprite) obj;
        return name.equals(sprite.getName()) && x == sprite.getX() && y == sprite.getY() && width == sprite.getWidth() && height == sprite.getHeight() && layerName.equals(sprite.getLayerName());
    }
    @Override
    public String toString() {
        String visibleString = visible ? "shown" : "hidden"; 
        return "Sprite(" + name + " (" +  x + ", " + y + ") | " + width + "x" + height + " | " + layerName + " layer | " + visibleString + ")";
    }
}
