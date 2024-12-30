package gameplay.gameObjects.puzzlePiece;

import utils.Direction;

public class DisconnectInfo {
    private PuzzlePiece p1, p2;
    private Direction.Type p1dir, p2dir;

    // p1dir is the direction of p1 to its parent (side.getDirection())
    public DisconnectInfo(PuzzlePiece p1, PuzzlePiece p2, Direction.Type p1dir) {
        this.p1 = p1;
        this.p2 = p2;
        this.p1dir = p1dir;
        this.p2dir = Direction.getOppositeDirection(p1dir);
    }

    public PuzzlePiece getPiece1() { return p1; }
    public PuzzlePiece getPiece2() { return p2; }
    public Direction.Type getPiece1Direction() { return p1dir; }
    public Direction.Type getPiece2Direction() { return p2dir; }
}
