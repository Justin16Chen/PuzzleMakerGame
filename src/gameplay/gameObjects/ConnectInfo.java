package gameplay.gameObjects;

import utils.Direction;

public class ConnectInfo {

    private PuzzlePiece piece1, piece2;
    private Direction piece1Direction, piece2Direction;
    private PuzzlePiece.Side piece1Side, piece2Side;
    private boolean canConnect;
    private ConnectType connectionType;

    public enum ConnectType {
        VALID,
        INVALID,
    }
    
    public static ConnectInfo makeValidConnection(PuzzlePiece piece1, PuzzlePiece piece2, Direction piece1Direction, Direction piece2Direction, PuzzlePiece.Side piece1Side, PuzzlePiece.Side piece2Side) {
        return new ConnectInfo(piece1, piece2, piece1Direction, piece2Direction, piece1Side, piece2Side);
    }
    public static ConnectInfo makeInvalidConnection(boolean canMove) {
        return new ConnectInfo(canMove);
    }

    private boolean canMove;

    private ConnectInfo(PuzzlePiece piece1, PuzzlePiece piece2, Direction piece1Direction, Direction piece2Direction, PuzzlePiece.Side piece1Side, PuzzlePiece.Side piece2Side) {
        this.connectionType = ConnectType.VALID;
        this.canConnect = true;
        this.canMove = true;

        this.piece1 = piece1;
        this.piece2 = piece2;
        this.piece1Direction = piece1Direction;
        this.piece2Direction = piece2Direction;
        this.piece1Side = piece1Side;
        this.piece2Side = piece2Side;
    }
    private ConnectInfo(boolean canMove) {
        this.connectionType = ConnectType.INVALID;
        this.canConnect = false;
        this.canMove = canMove;
    }

    public PuzzlePiece getPiece1() { return piece1; }
    public PuzzlePiece getPiece2() { return piece2; }
    public Direction getPiece1Direction() { return piece1Direction; } 
    public Direction getPiece2Direction() { return piece2Direction; } 
    public PuzzlePiece.Side getPiece1Side() { return piece1Side; } 
    public PuzzlePiece.Side getPiece2Side() { return piece2Side; } 
    public boolean canConnect() { return canConnect; }
    public boolean canMove() { return canMove; }
    public ConnectType getConnectType() { return connectionType; }

    @Override
    public String toString() {
        if (canConnect) {
            return "ConnectInfo(canConnect: true | p1Side: " + piece1Side + " | p2Side: " + piece2Side + ")";
        }
        if (canMove()) {
            return "ConnectInfo(canConnect: false, canMove: true)";
        }
        return "ConnectInfo(canConnect: false, canMove: false)";
    }
}
