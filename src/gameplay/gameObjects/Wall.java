package gameplay.gameObjects;

import gameplay.GameBoard;

public class Wall extends GameObject {

    public Wall(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.ObjectType.WALL, boardx, boardy);
    }

    @Override
    public void update(double dt) {
    }
    
}
