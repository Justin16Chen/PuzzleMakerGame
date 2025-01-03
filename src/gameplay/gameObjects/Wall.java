package gameplay.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import gameplay.GameBoard;
import gameplay.GameManager;
import utils.drawing.SimpleSprite;

public class Wall extends GameObject {

    public static Color COLOR = GameManager.BG_COLOR;

    public Wall(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.ObjectType.WALL, boardx, boardy);
    }

    @Override
    public void setup() {
        sprite = new SimpleSprite("wall", gameBoard.findGameObjectDrawX(this), gameBoard.findGameObjectDrawY(this), gameBoard.getTileSize(), gameBoard.getTileSize(), "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(COLOR);
                g.fillRect(getX(), getY(), gameBoard.getTileSize(), gameBoard.getTileSize());
            }
        };
    }

    @Override
    public void update(double dt) {
        forceToTargetDrawPos();
    }
}
