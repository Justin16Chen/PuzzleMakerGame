package gameplay.gameObjects.puzzlePiece;

import gameplay.gameObjects.puzzlePiece.PuzzlePiece.Side;
import utils.Direction;

public class ConnectInfo {

    private ConnectType type;
    private PuzzlePiece piece1, piece2;
    private Direction.Type piece1Direction, piece2Direction;
    private PuzzlePiece.Side piece1Side, piece2Side;
    private String invalidMessage;

    public enum ConnectType {
        VALID,
        INVALID,
    }
    
    public static ConnectInfo makeValidConnection(PuzzlePiece piece1, PuzzlePiece piece2, Direction.Type piece1Direction, Direction.Type piece2Direction, PuzzlePiece.Side piece1Side, PuzzlePiece.Side piece2Side) {
        return new ConnectInfo(piece1, piece2, piece1Direction, piece2Direction, piece1Side, piece2Side);
    }
    public static ConnectInfo makeInvalidConnection(String invalidMessage) {
        return new ConnectInfo(invalidMessage);
    }

    private ConnectInfo(PuzzlePiece piece1, PuzzlePiece piece2, Direction.Type piece1Direction, Direction.Type piece2Direction, PuzzlePiece.Side piece1Side, PuzzlePiece.Side piece2Side) {
        this.type = ConnectType.VALID;

        this.piece1 = piece1;
        this.piece2 = piece2;
        this.piece1Direction = piece1Direction;
        this.piece2Direction = piece2Direction;
        this.piece1Side = piece1Side;
        this.piece2Side = piece2Side;
    }
    private ConnectInfo(String invalidMessage) {
        this.type = ConnectType.INVALID;
        this.invalidMessage = invalidMessage;
    }

    public PuzzlePiece getPiece1() { return piece1; }
    public PuzzlePiece getPiece2() { return piece2; }
    public Direction.Type getPiece1Direction() { return piece1Direction; } 
    public Direction.Type getPiece2Direction() { return piece2Direction; } 
    public PuzzlePiece.Side getPiece1Side() { return piece1Side; } 
    public PuzzlePiece.Side getPiece2Side() { return piece2Side; } 
    public boolean canConnect() { return type == ConnectType.VALID; }
    public ConnectType getConnectType() { return type; }

    @Override
    public String toString() {
        if (canConnect()) {
            return "ConnectInfo(canConnect: true | p1: " + piece1 + " | p1 side: " + piece1Side + " | p2: " + piece2 + " | p2Side: " + piece2Side + ")";
        }
        return "ConnectInfo(canConnect: false | reason: " + invalidMessage + ")";
    }
}
