package main;

import utils.drawing.sprites.Sprites;
import utils.drawing.tilemap.Tile;
import utils.drawing.tilemap.Tilemap;

public class Test {

    public static void main(String[] args) {
        Sprites.addLayer("test", 0);
        Tilemap tilemap = new Tilemap("test tilemap", "res/tilemaps/testTilemap.png", "res/tilemaps/simpleTilemap.json", "test");
        System.out.println(tilemap);
        Tile tile = new Tile(32, 0, 0, "aaa aan aaa", 3, 3);
        System.out.println(tile);
        System.out.println(tile.rulesMatch(new boolean[][] {{true, true, true}, {true, true, false}, {true, true, true}}));
    }
}
