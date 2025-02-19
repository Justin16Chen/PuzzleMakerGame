package utils.drawing.tilemap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Print;

public class Tilemap {

    protected Tile[] tiles;
    protected int tileSize;
    protected int rows;
    protected int cols;
    protected BufferedImage image;

    public Tilemap(String name, String imagePath, String rulePath) {
        try {
            File file = new File(rulePath);
            String fileString = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject tilemapData = new JSONObject(fileString);
            tileSize = tilemapData.getInt("tileSize");
            rows = tilemapData.getInt("rows");
            cols = tilemapData.getInt("cols");
            tiles = new Tile[rows * cols];

            // fill in tiles
            JSONArray rules = tilemapData.getJSONArray("rules");
            int ruleWidth = tilemapData.getInt("ruleWidth");
            int ruleHeight = tilemapData.getInt("ruleHeight");
            for (int y=0; y<rules.length(); y++) 
                for (int x=0; x<rules.getJSONArray(y).length(); x++) 
                    // convert 2d grid to 1d array
                    try {
                        tiles[y * cols + x] = new Tile(tileSize, x * tileSize, y * tileSize, rules.getJSONArray(y).getString(x), ruleWidth, ruleHeight);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException(rulePath + " is not formatted correctly: " + e.getMessage());
                    }
        } catch (IOException e) {
            Print.println("ERROR", Print.RED);
            e.printStackTrace();
        } catch (JSONException e) {
            Print.println("ERROR", Print.RED);
            e.printStackTrace();
        }
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            Print.println("ERROR LOADING TILEMAP IMAGE " + imagePath);
            System.out.println(e.getMessage());
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void drawTile(Graphics2D g, int x, int y, int w, int h, boolean[][] adjacentTiles) {
        for (Tile tile : tiles)
                if (tile.rulesMatch(adjacentTiles)) {
                    tile.drawTile(g, image, x, y, w, h);
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
