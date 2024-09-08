package gameplay.gameObjects;

import java.awt.Graphics2D;

public class OutOfBounds extends GameObject {

    public OutOfBounds() {
        super(GameObject.ObjectType.OUT_OF_BOUNDS);
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw(Graphics2D g, int drawx, int drawy) {
    }
    
}
