package levelEditor;

import gameplay.gameObjects.GameObject;
import utils.drawing.sprites.Sprite;

public class Option {
    private final String name;
    private final GameObject gameObject;
    private Sprite sprite;

    public Option(String name, GameObject gameObject) {
        this.name = name;
        this.gameObject = gameObject;
        System.out.println(name + " " + gameObject);
    }
    public String getName() {
        return name;
    }

    public GameObject getGameObject() {
        return gameObject;
    }
    public Sprite getSprite() {
        return sprite;
    }

    public void setup(int x, int y, int width, int height) {
        System.out.println("setting up " + name + " option");
        if (gameObject != null) {
            gameObject.setup(x, y, width, height);
            sprite = gameObject.getSprite();
            System.out.println("game object: " + gameObject);
        }
        else if (sprite != null) {
            sprite.setX(x);
            sprite.setY(y);
            sprite.setWidth(width);
            sprite.setHeight(height);
        }
        else
            throw new RuntimeException("sprite and gameobject cannot be null when calling setup for " + name + " option");
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}
