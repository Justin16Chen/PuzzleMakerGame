package gameplay.gameObjects.puzzlePiece;

import utils.Direction;

public class ConnectInfo {

    private PuzzlePiece piece1, piece2;
    private Direction.Type piece1Direction, piece2Direction;
    private Side piece1Side, piece2Side;
    private String invalidMessage;
    private boolean valid;
    private Side.Strength connectStrength;

    
    public static ConnectInfo makeValidConnection(PuzzlePiece piece1, PuzzlePiece piece2, Direction.Type piece1Direction) {
        return new ConnectInfo(piece1, piece2, piece1Direction);
    }
    public static ConnectInfo makeInvalidConnection(String invalidMessage) {
        return new ConnectInfo(invalidMessage);
    }

    private ConnectInfo(PuzzlePiece piece1, PuzzlePiece piece2, Direction.Type piece1Direction) {
        this.valid = true;

        this.piece1 = piece1;
        this.piece2 = piece2;

        this.piece1Direction = piece1Direction;
        this.piece2Direction = Direction.getOppositeDirection(piece1Direction);

        this.piece1Side = piece1.getSide(piece1Direction);
        this.piece2Side = piece2.getSide(piece2Direction);

        connectStrength = Side.getConnectionStrength(piece1Side.getStrength(), piece2Side.getStrength());
    }
    private ConnectInfo(String invalidMessage) {
        this.valid = false;
        this.invalidMessage = invalidMessage;
    }

    public PuzzlePiece getPiece1() { return piece1; }
    public PuzzlePiece getPiece2() { return piece2; }
    public Direction.Type getPiece1Direction() { return piece1Direction; }
    public Direction.Type getPiece2Direction() { return piece2Direction; }
    public Side getPiece1Side() { return piece1Side; }
    public Side getPiece2Side() { return piece2Side; }
    public boolean canConnect() { return valid; }
    public boolean isStrong() { 
        if (valid) {
            return connectStrength == Side.Strength.STRONG; 
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
