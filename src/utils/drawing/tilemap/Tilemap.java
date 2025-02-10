package utils.drawing.tilemap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Print;
import utils.drawing.sprites.Sprite;

public class Tilemap extends Sprite {

    private Tile[] tiles;
    private int tileSize;

    public Tilemap(String name, String imagePath, String rulePath, String layerName) {
        super(name, imagePath, layerName);
        try {
            JSONObject tilemapData = new JSONObject( new String(Files.readAllBytes(Paths.get(new File(rulePath).toURI()))));
            tileSize = tilemapData.getInt("tileSize");
            int rows = tilemapData.getInt("rows");
            int cols = tilemapData.getInt("cols");
            tiles = new Tile[rows * cols];

            // fill in tiles
            JSONArray rules = tilemapData.getJSONArray("rules");
            int ruleWidth = tilemapData.getInt("ruleWidth");
            int ruleHeight = tilemapData.getInt("ruleHeight");
            for (int y=0; y<rules.length(); y++) 
                for (int x=0; x<rules.getJSONArray(y).length(); x++) 
                    // convert 2d grid to 1d array
                    tiles[y * cols + x] = new Tile(tileSize, x * tileSize, y * tileSize, rules.getJSONArray(y).getString(x), ruleWidth, ruleHeight);
        } catch (IOException e) {
            Print.println("ERROR", Print.RED);
            e.printStackTrace();
        } catch (JSONException e) {
            Print.println("ERROR", Print.RED);
            e.printStackTrace();
        }
        System.out.println(this);
    }

    public void drawTile(Graphics2D g, int x, int y, int w, int h, boolean[][] adjacentTiles) {
        for (Tile tile : tiles)
                if (tile.rulesMatch(adjacentTiles)) {
                    tile.drawTile(g, this, x, y, w, h);
                    return;
                }
        g.setColor(Color.BLACK);
        g.fillRect(x, y, w, h);
    }   

    @Override
    public String toString() {
        String str = "";
        for (Tile tile : tiles)
            str += "tile: \n" + tile;
        return str;
    }
}
