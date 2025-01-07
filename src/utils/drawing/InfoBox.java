package utils.drawing;

import java.util.ArrayList;
import java.util.Arrays;

import utils.drawing.sprites.Sprite;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

public class InfoBox extends Sprite {

    final public static String NAME = "infoBox";

    final public static Color BG_COLOR = new Color(40, 40, 40, 200);
    final public static Color TEXT_COLOR = new Color(230, 230, 230);
    final public static int BORDER_SIZE = 15;
    
    private ArrayList<String> drawList;
    private Font font;
    private FontMetrics fontMetrics;
    private Color bgColor, textColor;
    private int borderSize;

    public InfoBox(int x, int y) {
        super(NAME, x, y, 1, 1, "debug");
        font = new Font("Arial", Font.PLAIN, 12);
        this.bgColor = BG_COLOR;
        this.textColor = TEXT_COLOR;
        this.borderSize = BORDER_SIZE;
        drawList = new ArrayList<>();
    }

    public InfoBox(String name, int x, int y) {
        super(name, x, y, 1, 1, "debug");
        font = new Font("Arial", Font.PLAIN, 12);
        this.bgColor = BG_COLOR;
        this.textColor = TEXT_COLOR;
        this.borderSize = BORDER_SIZE;
        drawList = new ArrayList<>();
    }
    
    public InfoBox(int x, int y, Color bgColor, Color textColor, int borderSize) {
        super(NAME, x, y, 1, 1, "debug");
        drawList = new ArrayList<>();
        font = new Font("Arial", Font.PLAIN, 12);
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.borderSize = borderSize;
        drawList = new ArrayList<>();
    }

    public InfoBox(String name, int x, int y, Color bgColor, Color textColor, int borderSize) {
        super(name, x, y, 1, 1, "debug");
        drawList = new ArrayList<>();
        font = new Font("Arial", Font.PLAIN, 12);
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.borderSize = borderSize;
        drawList = new ArrayList<>();
    }

    public FontMetrics getFontMetrics() { return fontMetrics; }

    private void updateDimensions() {
        // Calculate total height for the list with padding
        setHeight(fontMetrics.getHeight() * drawList.size() + borderSize * 2);
    
        // Calculate maximum width of the list items, including padding
        int maxWidth = 0;
        for (int i=0; i<drawList.size(); i++)
            maxWidth = Math.max(maxWidth, fontMetrics.stringWidth(drawList.get(i)));
        maxWidth += borderSize * 2;  // Add padding on both sides
        setWidth(maxWidth);
    }

    public void setFont(Font font) { this.font = font; }

    public void setDrawList(ArrayList<String> drawList) {
        this.drawList.clear();

        for (String string : drawList) {
            if (string.contains("\n")) {
                String[] strings = string.split("\n");
                for (String s : strings) 
                    this.drawList.add(s);
            }
            else this.drawList.add(string);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        fontMetrics = g.getFontMetrics(font);
        updateDimensions();
    
        // Draw background rectangle
        g.setColor(bgColor);
        g.fillRect(getX(), getY(), getWidth(), getHeight());
    
        // Draw text
        g.setColor(textColor);
        g.setFont(font);
        for (int i = 0; i < drawList.size(); i++) {
            // Calculate the Y-coordinate for each string (baseline-aligned with padding)
            int textY = getY() + borderSize + (i + 1) * fontMetrics.getAscent() + i * fontMetrics.getDescent();
            g.drawString(drawList.get(i), getX() + borderSize, textY);
        }
    }

    public String getDrawListToString() {
        return Arrays.toString(drawList.toArray());
    }
    
}
