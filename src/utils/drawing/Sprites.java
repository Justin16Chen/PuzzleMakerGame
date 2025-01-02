package utils.drawing;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Sprites {

    private static HashMap<Integer, String> layers = new HashMap<>();
    private static HashMap<String, ArrayList<Sprite>> sprites = new HashMap<>();
    private static int numLayers = 0;

    public static void addLayer(String layerName, int layerNumber) {
        if (layers.containsKey(layerNumber)) 
            throw new IllegalArgumentException("Layer " + layerName + "on layer " + layerNumber + " already exists");
        
        layers.put(layerNumber, layerName);
        sprites.put(layerName, new ArrayList<Sprite>());
        numLayers++;
    }
    public static void addSprite(Sprite sprite, String layerName) {
        if (!layers.values().contains(layerName)) 
            throw new IllegalArgumentException("Layer " + layerName + " does not exist");
        sprites.get(layerName).add(sprite);
    }

    public static void deleteSprite(Sprite spriteToDelete) {
        for (int i=0; i<numLayers; i++) {
            ArrayList<Sprite> list = sprites.get(layers.get(i));
            for (int j=0; j<list.size(); j++)
                if (list.get(j).equals(spriteToDelete)) {
                    list.remove(j);
                    return;
                }
        }
    }

    public static void deleteSprites(String[] names) {
        for (int i=0; i<numLayers; i++) {
            ArrayList<Sprite> list = sprites.get(layers.get(i));
            for (int j=0; j<list.size(); j++) {
                Sprite sprite = list.get(j);

                for (String name : names)
                    if (sprite.getName().equals(name)) {
                        list.remove(sprite);
                        j--;
                    }
            }
        }
    }

    public static void drawSprites(Graphics2D g) {
        for (String layerName : layers.values()) {
            ArrayList<Sprite> list = sprites.get(layerName);
            for (Sprite sprite : list) {
                if (sprite.isVisible())
                    sprite.draw(g);
            }
        }
    }

    public static String getLayersToString() {
        return layers.toString();
    }
    public static String getSpritesToString() {
        return sprites.toString();
    }
}
