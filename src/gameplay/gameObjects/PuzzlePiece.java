package gameplay.gameObjects;

import gameplay.GameBoard;

public class PuzzlePiece extends GameObject {

    public PuzzlePiece(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.OBJECT_TYPE.PUZZLE_PIECE, boardx, boardy);
    }
    public PuzzlePiece(GameBoard gameBoard, GameObject.OBJECT_TYPE objectType, int boardx, int boardy) {
        super(gameBoard, objectType, boardx, boardy);
    }
    @Override
    public void update(double dt) {
    }
}
