package gameplay;

public class PuzzlePiece extends GameObject {
    public PuzzlePiece(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.OBJECT_TYPE.PUZZLE_PIECE, boardx, boardy);
    }

    @Override
    public void update() {

    }
}
