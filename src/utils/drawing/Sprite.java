package utils.drawing;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import utils.tween.Updatable;

public abstract class Sprite {

    private static ArrayList<Sprite> list = new ArrayList<Sprite>();

    public static void addSprite(Sprite sprite) {
        list.add(sprite);
    }

    public static void drawSprites(Graphics2D g) {
        Iterator<Sprite> iterator = list.iterator();
        while (iterator.hasNext()) {
            Sprite sprite = iterator.next();
            sprite.draw(g);
            if (sprite.destroy()) {
                iterator.remove();
            }
        }
    }

    public static void clearUpdatables() {
        list.clear();
    }

    private int x, y, width, height;
    private boolean visible;
    private boolean destroy;

    public Sprite(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public boolean destroy() { return destroy; }

    public void setVisible(boolean visible) { this.visible = visible; }

    public abstract void draw(Graphics2D g);
}
