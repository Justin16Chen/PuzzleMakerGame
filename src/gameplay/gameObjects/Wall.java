package gameplay.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import gameplay.GameBoard;
import gameplay.GameManager;

public class Wall extends GameObject {

    public static Color COLOR = GameManager.BG_COLOR;
    public Wall(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.ObjectType.WALL, boardx, boardy);
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(COLOR);
        g.fillRect(getCurrentDrawx(), getCurrentDrawy(), gameBoard.tileSize, gameBoard.tileSize);
    }
    
}
