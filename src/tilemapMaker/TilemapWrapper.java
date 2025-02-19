package tilemapMaker;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.JSONArray;

import utils.drawing.tilemap.Tile;
import utils.drawing.tilemap.Tilemap;

public class TilemapWrapper extends Tilemap {

    private static final Color FILLED_COLOR = new Color(80, 80, 80);
    private static final Color ANYTHING_COLOR = new Color(60, 200, 60);

    private int drawX, drawY, tileSpacing;
    public TilemapWrapper(String imagePath, String jsonDataPath) {
        super("tilemap wrapper", imagePath, jsonDataPath);
    }

    public void setSelectedTileRule(Tile.Type type, int mouseX, int mouseY) {

        int tileRow = (mouseY - drawY) / tileSpacing;
        if (tileRow < 0 || tileRow >= rows)
            return;
        int tileCol = (mouseX - drawX) / tileSpacing;
        if (tileCol < 0 || tileCol >= cols) 
            return;
        int ruleRow = (int) Math.max(0, Math.min(2, ((mouseY - drawY) % tileSpacing) * 1. / tileSpacing * 3));
        int ruleCol = (int) Math.max(0, Math.min(2, ((mouseX - drawX) % tileSpacing) * 1. / tileSpacing * 3));
        Tile tile = tiles[tileRow * cols + tileCol];
        tile.setRule(type, ruleRow, ruleCol);

    }

    public void drawAllTiles(Graphics2D g, int cx, int cy, int w, int spacing) {
        tileSpacing = w / cols;
        int halfSpacing = spacing / 2;
        int tileDrawSize = tileSpacing - spacing;
        drawX = cx - w / 2;
        drawY = cy - (int) (w * rows * 1.0 / cols / 2);
        for (int i = 0; i < tiles.length; i++) {
            int row = i / cols;
            int col = i % cols;
            int tileDrawX = drawX + tileSpacing * col;
            int tileDrawY = drawY + tileSpacing * row;
            tiles[i].drawRules(g, FILLED_COLOR, ANYTHING_COLOR, tileDrawX, tileDrawY, tileSpacing, tileSpacing);
            tiles[i].drawTile(g, image, tileDrawX + halfSpacing, tileDrawY + halfSpacing, tileDrawSize, tileDrawSize);
        }
    }

    public JSONArray getRules() {
        JSONArray rules = new JSONArray();
        for (int i = 0; i < tiles.length; i++) {
            int row = i / cols;
            if (row >= rules.length())
                rules.put(row, new JSONArray());
            rules.getJSONArray(row).put(tiles[i].getRulesString());
        }
        return rules;
    }
}
