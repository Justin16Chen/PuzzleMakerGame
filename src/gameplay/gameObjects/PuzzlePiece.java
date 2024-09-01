package gameplay.gameObjects;

import gameplay.GameBoard;
import utils.*;

public class PuzzlePiece extends GameObject {

    public enum Side {
        EDGE,
        OUT,
        IN,
    }

    public static Side[] getSideData(String sideString) {
        Side[] sideData = new Side[4];
        for (int i=0; i<sideString.length(); i++) {
            int sideNum = Integer.valueOf(sideString.charAt(i));
            sideData[i] = getSideData(sideNum);
        }
        return sideData;
    }
    public static Side getSideData(int sideNumber) {
        switch (sideNumber) {
            case 0: return Side.EDGE;
            case 1: return Side.IN;
            case 2: return Side.OUT;
            default: return Side.EDGE;
        }
    }
    public static boolean isCompatible(Side side1, Side side2) {
        return (side1 == Side.IN && side2 == Side.OUT) || 
               (side1 == Side.OUT && side2 == Side.IN);
    }

    private Side[] sides;
    private boolean[] connectedSides;

    public PuzzlePiece(GameBoard gameBoard, int boardx, int boardy, String sideString) {
        super(gameBoard, GameObject.ObjectType.PUZZLE_PIECE, boardx, boardy);
        this.sides = getSideData(sideString);
    }
    public PuzzlePiece(GameBoard gameBoard, GameObject.ObjectType objectType, int boardx, int boardy, String sideString) {
        super(gameBoard, objectType, boardx, boardy);
        this.sides = getSideData(sideString);
    }

    public int getSideIndex(Direction direction) {
        switch (direction) {
            case UP: return 0;
            case LEFT: return 1;
            case DOWN: return 2;
            case RIGHT: return 3;
            default: return 0;
        }
    }

    // gets the direction of a side based off of a vector
    // assumes that hdir and vdir are between -1 and 1
    // and at least 1 of them are not zero
    public Direction getDirection(int hdir, int vdir) {
        if (hdir == 0) {
            if (vdir == 1) { return Direction.DOWN; }
            return Direction.UP;
        }
        if (hdir == -1) { return Direction.LEFT; }
        return Direction.RIGHT;
    }

    // gets the type of side based on a direction
    public Side getSideType(Direction direction) { return sides[getSideIndex(direction)]; }

    // gets if a side is already connected with another piece's side based on a direction
    public boolean isSideConnected(Direction direction) { return connectedSides[getSideIndex(direction)]; }
    // connect a side
    public void connectSide(Direction direction) { connectedSides[getSideIndex(direction)] = true; }

    // whether this piece can connect to another puzzle piece or not
    public boolean canConnect(PuzzlePiece puzzlePiece) {

        // get the relative direction to the other piece
        int hdir = puzzlePiece.getBoardX() - getBoardX();
        int vdir = puzzlePiece.getBoardY() - getBoardY();

        // puzzle pieces can only connect to other directly adjacent puzzle pieces (dist between positions cannot exceed 1)
        if (hdir * hdir + vdir * vdir > 1) { return false; }

        // out facing edges can only connect with in facing edges
        // flat edges cannot connect with anything
            // find the direction from self to piece and direction from piece to self
        Direction directionToPiece = getDirection(hdir, vdir);
        Direction directionToSelf = getDirection(-hdir, -vdir);
            // find the type of sides that correspond with directions
        Side selfSideType = getSideType(directionToPiece);
        Side oppositeSideType = puzzlePiece.getSideType(directionToSelf);
            // can only connect if one is out edge and one is in edge
        if (PuzzlePiece.isCompatible(selfSideType, oppositeSideType)) {
            return true;
        }
        return false;
    }
    public void connect(int hdir, int vdir) {
        // get the puzzle piece to connect with
        PuzzlePiece puzzlePiece = (PuzzlePiece) gameBoard.getGameObject(getBoardX() + hdir, getBoardY() + vdir);

        // find the direction from self to piece and direction from piece to self
        Direction directionToPiece = getDirection(hdir, vdir);
        Direction directionToSelf = getDirection(-hdir, -vdir);

        // connect the sides
        connectSide(directionToPiece);
        puzzlePiece.connectSide(directionToSelf);
    }

    @Override
    public void update(double dt) {}
}
