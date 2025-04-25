package utils.drawing.sprites;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import utils.input.Mouse;
import utils.tween.Ease;
import utils.tween.EaseType;
import utils.tween.Tween;
import utils.tween.Updatables;

public abstract class ButtonSprite extends Sprite {

    private String text;
    private Color textColor;
    private Font textFont;
    private Mouse mouse;
    private int resizeAmount;
    private double resizeTime;
    private int currentScale;
    private int startX, startY, startW, startH;
    public ButtonSprite(int x, int y, int w, int h, String layerName, Mouse mouse) {
        super("button", x, y, w, h, layerName);
        this.mouse = mouse;
        resizeAmount = 4;
        startX = x;
        startY = y;
        startW = w;
        startH = h;
    }
    public ButtonSprite(int x, int y, int w, int h, String text, String layerName, Mouse mouse) {
        super("button", x, y, w, h, layerName);
        this.text = text;
        this.mouse = mouse;
        resizeAmount = 4;
        startX = x;
        startY = y;
        startW = w;
        startH = h;
    }

    public void setText(String text, Color textColor, Font textFont) {
        this.text = text;
        this.textColor = textColor;
        this.textFont = textFont;
    }
    public void setResizeAmount(int resizeAmount, double resizeTime) {
        this.resizeAmount = resizeAmount;
        this.resizeTime = resizeTime;
    }

    private void resize(int scaleFactor) {
        if (currentScale == scaleFactor)
            return;
        currentScale = scaleFactor;
        System.out.println(scaleFactor);
        Updatables.deleteUpdatables(new String[] {"resize button x", "resize button y", "resize button w", "resize button h"});
        Tween.createTween("resize button x", this, "x", getX(), startX - resizeAmount / 2 * scaleFactor, resizeTime).setEaseType(new EaseType(Ease.EASE_OUT));
        Tween.createTween("resize button y", this, "y", getY(), startY - resizeAmount / 2 * scaleFactor, resizeTime).setEaseType(new EaseType(Ease.EASE_OUT));
        Tween.createTween("resize button w", this, "width", getWidth(), startW + resizeAmount * scaleFactor, resizeTime).setEaseType(new EaseType(Ease.EASE_OUT));
        Tween.createTween("resize button h", this, "height", getHeight(), startH + resizeAmount * scaleFactor, resizeTime).setEaseType(new EaseType(Ease.EASE_OUT));
    }

    // this will run after update loop in the draw loop
    // not the best but its ok ig
    public abstract void onClick();

    @Override
    public void draw(Graphics2D g) {
        if (mouse.isOver(this))  {
            resize(mouse.down() ? -1 : 1);
            if (mouse.released())
                onClick();
        }
        else
            resize(0);
        super.draw(g);
        g.setColor(textColor);
        g.setFont(textFont);
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getHeight();
        g.drawString(text, getCenterX() - textWidth / 2, getCenterY() + textHeight / 2);

    }
}
