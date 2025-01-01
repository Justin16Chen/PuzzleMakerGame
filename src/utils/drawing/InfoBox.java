package utils.drawing;

import java.util.ArrayList;

import gameplay.GameManager;
import utils.JMath;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

public class InfoBox {

    private static boolean hasGameManager = false;
    private static GameManager gameManager;

    public static void setGameManager(GameManager gameManager) {
        hasGameManager = true;
        InfoBox.gameManager = gameManager;
        infoBoxList = new ArrayList<InfoBox>();
    }

    private static ArrayList<InfoBox> infoBoxList;

    public static void drawInfoBoxes(Graphics2D g) {
        for (int i=0; i<infoBoxList.size(); i++) {

            if (i >= infoBoxList.size()) break;

            InfoBox infoBox = infoBoxList.get(i);

            if (infoBox.delete) {
                infoBoxList.remove(i);
                i--;
                continue;
            }
            
            if (infoBox.visible) infoBox.drawList(g);
        }
    }

    public static void clearInfoBoxes() {
        infoBoxList.clear();
    }

    public static void removeInfoBox(InfoBox infoBox) {
        infoBoxList.get(infoBoxList.indexOf(infoBox)).setDelete(true);
    }

    public static InfoBox createInfoBox() {
        if (!hasGameManager) return null;
        InfoBox infoBox = new InfoBox(BG_COLOR, TEXT_COLOR, BORDER_SIZE);
        infoBoxList.add(infoBox);
        return infoBox;
    }
    public static InfoBox createInfoBox(Color bgColor, Color textColor, int borderSize) {
        if (!hasGameManager) return null;
        InfoBox infoBox = new InfoBox(bgColor, textColor, borderSize);
        infoBoxList.add(infoBox);
        return infoBox;
    }

    final public static Color BG_COLOR = new Color(20, 20, 20, 200);
    final public static Color TEXT_COLOR = new Color(230, 230, 230);
    final public static int BORDER_SIZE = 15;
    
    private int x, y, width, height;
    private Insets offset;
    private double offsetPercentX, offsetPercentY;
    private ArrayList<String> drawList;
    private Font font;
    private FontMetrics fontMetrics;
    private Color bgColor, textColor;
    private int borderSize;
    private boolean visible;
    private boolean delete;

    private InfoBox(Color bgColor, Color textColor, int borderSize) {
        drawList = new ArrayList<>();
        font = new Font("Arial", Font.PLAIN, 12);
        offset = new Insets(0, 0, 0, 0);
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.borderSize = borderSize;
        this.visible = true;
    }

    public int getOriginX() { return x; }
    public int getOriginY() { return y; }
    public int getLeft() { return x - offset.left; }
    public int getTop() { return y - offset.top; }
    public int getRight() { return x + offset.right; }
    public int getBottom() { return y + offset.bottom; }
    public int getWidth() { return width; } 
    public int getHeight() { return height; }
    public FontMetrics getFontMetrics() { return fontMetrics; }
    public boolean isVisible() { return visible; }
    public void show() { visible = true; }
    public void hide() { visible = false; }
    public void setDelete(boolean delete) { this.delete = delete; }
    public boolean getDelete() { return delete; }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setOffsets(double offsetx, double offsety) {
        this.offsetPercentX = offsetx;
        this.offsetPercentY = offsety;
    }

    public void updateInsets() {
        offset.left = (int) (width * offsetPercentX);
        offset.top = (int) (height * offsetPercentY);
        offset.right = width - offset.left;
        offset.bottom = height - offset.top;
    }

    private void updateDimensions() {
        // Calculate total height for the list with padding
        height = fontMetrics.getHeight() * drawList.size() + borderSize * 2;
    
        // Calculate maximum width of the list items, including padding
        int maxWidth = 0;
        for (String string : drawList) 
            maxWidth = Math.max(maxWidth, fontMetrics.stringWidth(string));
        maxWidth += borderSize * 2;  // Add padding on both sides
        width = maxWidth;
    }

    private void clampPos() {
        x = JMath.clamp(x, gameManager.getWidth() - offset.right, offset.left);
        y = JMath.clamp(y, gameManager.getHeight() - offset.bottom, offset.top);
    }

    public void setFont(Font font) { this.font = font; }

    public void setDrawList(ArrayList<String> drawList) { 
        for (String string : drawList) {
            if (string.contains("\n")) {
                String[] strings = string.split("\n");
                for (String s : strings) {
                    this.drawList.add(s);
                }
            }
            else this.drawList.add(string);
        }
    }
    public void clearDrawList() { this.drawList.clear(); }

    public void drawList(Graphics2D g) {
        fontMetrics = g.getFontMetrics(font);
    
        updateDimensions();
        updateInsets();
        clampPos();
    
        // Draw background rectangle
        g.setColor(bgColor);
        g.fillRect(getLeft(), getTop(), width, height);
    
        // Draw text
        g.setColor(textColor);
        g.setFont(font);
        for (int i = 0; i < drawList.size(); i++) {
            // Calculate the Y-coordinate for each string (baseline-aligned with padding)
            int textY = getTop() + borderSize + (i + 1) * fontMetrics.getAscent() + i * fontMetrics.getDescent();
            g.drawString(drawList.get(i), getLeft() + borderSize, textY);
        }
    }
    
}
