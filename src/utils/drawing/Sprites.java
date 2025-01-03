package utils.drawing;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Sprites {

    private static HashMap<Integer, String> layers = new HashMap<>();
    private static HashMap<String, ArrayList<Sprite>> sprites = new HashMap<>();

    
    public static ArrayList<Sprite> getSprites() {
        ArrayList<Sprite> allSprites = new ArrayList<>();
        for (String layerName : layers.values()) 
            allSprites.addAll(sprites.get(layerName));
        return allSprites;
    }

    public static void addLayer(String layerName, int layerNumber) {
        if (layers.containsKey(layerNumber)) 
            throw new IllegalArgumentException("Layer " + layerName + "on layer " + layerNumber + " already exists");
        
        layers.put(layerNumber, layerName);
        sprites.put(layerName, new ArrayList<Sprite>());
    }
    public static void addSprite(Sprite sprite, String layerName) {
        if (!layers.values().contains(layerName)) 
            throw new IllegalArgumentException("Layer " + layerName + " does not exist");
        sprites.get(layerName).add(sprite);
    }

    public static void deleteSprite(Sprite spriteToDelete) {
        for (String layerName : layers.values()) 
            for (Sprite sprite : sprites.get(layerName)) 
                if (sprite.equals(spriteToDelete)) {
                    sprites.get(layerName).remove(sprite);
                    return;
                }
    }

    public static void deleteSprites(String[] names) {
        for (String name : names) 
            for (String layerName : layers.values()) 
                for (Sprite sprite : sprites.get(layerName)) 
                    if (sprite.getName().equals(name)) {
                        sprites.get(layerName).remove(sprite);
                        break;
                    }
    }

    public static void drawSprites(Graphics2D g) {
        for (String layerName : layers.values()) 
            for (Sprite sprite : sprites.get(layerName)) 
                if (sprite.isVisible())
                    sprite.draw(g);
    }

    public static String getLayersToString() {
        return layers.toString();
    }
    public static String getSpritesToString() {
        return sprites.toString();
    }
}
