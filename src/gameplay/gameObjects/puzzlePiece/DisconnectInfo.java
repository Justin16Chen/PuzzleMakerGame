package gameplay.gameObjects.puzzlePiece;

import utils.direction.Direction;
import utils.direction.Directions;

public class DisconnectInfo {
    private PuzzlePiece p1, p2;
    private Direction p1dir, p2dir;

    // p1dir is the direction of p1 to its parent (side.getDirection())
    public DisconnectInfo(PuzzlePiece p1, PuzzlePiece p2, Direction p1dir) {
        this.p1 = p1;
        this.p2 = p2;
        this.p1dir = p1dir;
        this.p2dir = Directions.getOppositeDirection(p1dir);
    }
    @Override
    public String toString() {
        return "DisconnectInfo(p1: (" + p1.getBoardX() + "," + p1.getBoardY() + "), " + p1dir + " | p2: (" + p2.getBoardX() + "," + p2.getBoardY() + "), " + p2dir + ")";
    }

    public PuzzlePiece getPiece1() { return p1; }
    public PuzzlePiece getPiece2() { return p2; }
    public Direction getPiece1Direction() { return p1dir; }
    public Direction getPiece2Direction() { return p2dir; }
}
