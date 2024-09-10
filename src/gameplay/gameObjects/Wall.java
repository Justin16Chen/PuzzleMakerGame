package gameplay.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import gameplay.GameBoard;

public class Wall extends GameObject {

    public Wall(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.ObjectType.WALL, boardx, boardy);
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw(Graphics2D g, int drawx, int drawy) {
        g.setColor(new Color(100, 100, 100));
        g.fillRect(drawx, drawy, gameBoard.tileSize, gameBoard.tileSize);
    }
    
}
