package utils.drawing.tilemap;

import java.awt.Graphics2D;

public class Tile {

    public enum Type {
        FILLED, EMPTY, ANYTHING
    }
    private int tileSize;
    private int tilemapDrawX, tilemapDrawy;
    private Type[][] rules;

    public Tile(int tileSize, int tilemapDrawX, int tilemapDrawY, String rules, int ruleWidth, int ruleHeight) {
        this.tileSize = tileSize;
        this.tilemapDrawX = tilemapDrawX;
        this.tilemapDrawy = tilemapDrawY;

        this.rules = loadRules(rules, ruleWidth, ruleHeight);
    }

    private Type[][] loadRules(String rulesString, int ruleWidth, int ruleHeight) {
        Type[][] rules = new Type[ruleHeight][ruleWidth];

        rulesString = rulesString.replaceAll(" ", "");
        for (int i=0; i< rulesString.length(); i++) {
            Type type = null;
            switch (rulesString.charAt(i)) {
                case 'y': type = Type.FILLED; break;
                case 'n': type = Type.EMPTY; break;
                case 'a': type = Type.ANYTHING; break;
            }
            if (type == null) 
                throw new IllegalArgumentException("rule string " + rulesString + " is invalid at row " + (i / ruleHeight) + " and column " + (i % ruleWidth));
            
            rules[i / ruleHeight][i % ruleWidth] = type;
        }
        return rules;
    }

    public boolean rulesMatch(boolean[][] rules) {
        for (int y=0; y<rules.length; y++)
            for (int x=0; x<rules[0].length; x++) {
                Type requiredType = this.rules[y][x];
                if (requiredType == Type.ANYTHING)
                    continue;
                if (requiredType == Type.FILLED && !rules[y][x] || requiredType == Type.EMPTY && rules[y][x])
                    return false;
            }
        return true;
    }

    public void drawTile(Graphics2D g, Tilemap tilemap, int x, int y, int w, int h) {
        g.drawImage(tilemap.getImage(), x, y, x + w, y + h, tilemapDrawX, tilemapDrawy, tilemapDrawX + tileSize, tilemapDrawy + tileSize, null);
    }

    @Override
    public String toString() {
        String str = "";
        for (Type[] row : rules) {
            for (Type type : row)
                str += type.toString().charAt(0) + " ";
            str += "\n";
        }
        return str;
    }
}
