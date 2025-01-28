package utils.input;

import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import utils.drawing.sprites.Sprite;

public class MouseInput extends InputBase implements MouseListener, MouseMotionListener {

    final public static MouseInput NOTHING_INPUT = new MouseInput(Type.NOTHING);
    final public static int OFFSET_X = 0;
    final public static int OFFSET_Y = 0;

    private enum Type {
        NOTHING,
        ACTIVE
    }

    private Type type;
    private int x, y;
    private int absX, absY;
    private Insets insets;

    public MouseInput(Insets insets) {
        this.insets = insets;
        type = Type.ACTIVE;
        x = -1;
        y = -1;
        absX = -1;
        absY = -1;
    }
    public MouseInput(Type type) {
        this.type = type;
        insets = new Insets(0, 0, 0, 0);
        x = -1;
        y = -1;
        absX = -1;
        absY = -1;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAbsX() { return absX; }
    public int getAbsY() { return absY; }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    private void setPos(MouseEvent e) {
        x = e.getX() - insets.left;
        y = e.getY() - insets.top;
        absX = e.getXOnScreen();
        absY = e.getYOnScreen();
    }

    public boolean isOver(int x, int y, int w, int h) {
        return this.x >= x && this.y >= y && this.x <= x + w && this.y <= y + h;
    }
    public boolean isOver(Sprite sprite) {
        return isOver(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    // detect mouse input
    @Override
    public void mouseDragged(MouseEvent e) {
        if (type == Type.NOTHING)
            return;
        setPos(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (type == Type.NOTHING)
            return;
        setPos(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (type == Type.NOTHING)
            return;
        setDown(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (type == Type.NOTHING)
            return;
        setDown(false);
    }

    // unused methods
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}