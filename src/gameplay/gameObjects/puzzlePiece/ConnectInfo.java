package gameplay.gameObjects.puzzlePiece;

import utils.direction.Direction;
import utils.direction.Directions;

public class ConnectInfo {

    private PuzzlePiece piece1, piece2;
    private Direction piece1Direction, piece2Direction;
    private Side piece1Side, piece2Side;
    private String invalidMessage;
    private boolean valid;
    private Side.Type connectType;

    
    public static ConnectInfo makeValidConnection(PuzzlePiece piece1, PuzzlePiece piece2, Direction piece1Direction) {
        return new ConnectInfo(piece1, piece2, piece1Direction);
    }
    public static ConnectInfo makeInvalidConnection(String invalidMessage) {
        return new ConnectInfo(invalidMessage);
    }

    private ConnectInfo(PuzzlePiece piece1, PuzzlePiece piece2, Direction piece1Direction) {
        this.valid = true;

        this.piece1 = piece1;
        this.piece2 = piece2;

        this.piece1Direction = piece1Direction;
        this.piece2Direction = Directions.getOppositeDirection(piece1Direction);

        this.piece1Side = piece1.getSide(piece1Direction);
        this.piece2Side = piece2.getSide(piece2Direction);

        connectType = Side.getConnectionType(piece1Side.getType(), piece2Side.getType());
    }
    private ConnectInfo(String invalidMessage) {
        this.valid = false;
        this.invalidMessage = invalidMessage;
    }

    public PuzzlePiece getPiece1() { return piece1; }
    public PuzzlePiece getPiece2() { return piece2; }
    public Direction getPiece1Direction() { return piece1Direction; }
    public Direction getPiece2Direction() { return piece2Direction; }
    public Side getPiece1Side() { return piece1Side; }
    public Side getPiece2Side() { return piece2Side; }
    public boolean canConnect() { return valid; }
    public boolean isStrong() { 
        if (valid) {
            return connectType == Side.Type.STRONG; 
        }
        return false;
    }

    @Override
    public String toString() {
        if (canConnect()) {
            return "ConnectInfo(dir:" + getPiece1Direction() + "|p1:" + piece1 + "|s1:" + getPiece1Side() + "|p2:" + piece2 + "|s2" + getPiece2Side() + ")";
        }
        return "ConnectInfo(" + invalidMessage + ")";
    }
}
