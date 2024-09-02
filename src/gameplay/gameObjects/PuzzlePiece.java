package gameplay.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import gameplay.GameBoard;
import utils.*;

public class PuzzlePiece extends GameObject {


    // gets the direction of a side based off of a vector
    // assumes that hdir and vdir are between -1 and 1
    // and at least 1 of them are not zero
    public static Direction getDirection(int hdir, int vdir) {
        if (hdir == 0) {
            if (vdir == 1) { return Direction.DOWN; }
            return Direction.UP;
        }
        if (hdir == -1) { return Direction.LEFT; }
        return Direction.RIGHT;
    }

    public enum Side {
        EDGE,
        OUT,
        IN,
    }

    public static Side[] getSideData(String sideString) {
        Side[] sideData = new Side[4];
        for (int i=0; i<sideString.length(); i++) {
            sideData[i] = getSideData(Integer.valueOf(String.valueOf(sideString.charAt(i))));
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

    // whether the side types can connect to each other
    public static boolean isCompatible(Side side1, Side side2) {
        return (side1 == Side.IN && side2 == Side.OUT) || 
               (side1 == Side.OUT && side2 == Side.IN);
    }

    // whether this piece can connect to another puzzle piece or not
    public static boolean canConnect(ConnectInfo connectInfo) {
        return isCompatible(connectInfo.getPiece1Side(), connectInfo.getPiece2Side());
    }

    protected Side[] sides;
    protected boolean[] connectedSides;

    public PuzzlePiece(GameBoard gameBoard, int boardx, int boardy, String sideString) {
        super(gameBoard, GameObject.ObjectType.PUZZLE_PIECE, boardx, boardy);
        this.sides = getSideData(sideString);
        this.connectedSides = new boolean[4];
    }
    public PuzzlePiece(GameBoard gameBoard, GameObject.ObjectType objectType, int boardx, int boardy, String sideString) {
        super(gameBoard, objectType, boardx, boardy);
        this.sides = getSideData(sideString);
        this.connectedSides = new boolean[4];
    }

    // gets the index based on the side
    public int getSideIndex(Direction direction) {
        switch (direction) {
            case UP: return 0;
            case LEFT: return 1;
            case DOWN: return 2;
            case RIGHT: return 3;
            default: return 0;
        }
    }
    // gets the type of side based on a direction
    public Side getSideType(Direction direction) { return sides[getSideIndex(direction)]; }
    // gets if a side is already connected with another piece's side based on a direction
    public boolean isSideConnected(Direction direction) { return connectedSides[getSideIndex(direction)]; }
    // connect a side
    public void connectSide(Direction direction) { connectedSides[getSideIndex(direction)] = true; }
    
    // get the connection info between two puzzle pieces
    public ConnectInfo getConnectInfo(int movex, int movey, int hdir, int vdir) {

        int targetx = getBoardX() + movex + hdir;
        int targety = getBoardY() + movey + vdir;

        // make sure it is in bounds
        if (!gameBoard.inBounds(targetx, targety)) {
            System.out.println("out of bounds, line 96");
            return ConnectInfo.makeInvalidConnection(true);
        }

        // get game object at target location
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);

        // can only connect with puzzle pieces
        if (gameObject == null) {
            System.out.println("null gameObject, line 105");
            System.out.println("target pos: " + targetx + ", " + targety);
            System.out.println("move offset: " + movex + ", " + movey);
            System.out.println("side to get connect info on: " + hdir + ", " + vdir);
            return ConnectInfo.makeInvalidConnection(true);
        }
        if (gameObject.getObjectType() != GameObject.ObjectType.PUZZLE_PIECE && gameObject.getObjectType() != GameObject.ObjectType.PLAYER_PIECE) {
            System.out.println("gameObject other than puzzle/player, line 109");
            return ConnectInfo.makeInvalidConnection(true);
        }

        // get the other puzzle piece
        PuzzlePiece piece2 = (PuzzlePiece) gameObject;

        // out facing edges can only connect with in facing edges
        // flat edges cannot connect with anything
            // find the direction from self to piece and direction from piece to self
        Direction directionToPiece = PuzzlePiece.getDirection(hdir, vdir);
        Direction directionToSelf = PuzzlePiece.getDirection(-hdir, -vdir);
            // find the type of sides that correspond with directions
        Side piece1Side = getSideType(directionToPiece);
        Side piece2Side = piece2.getSideType(directionToSelf);

        System.out.println("connection info(dir to piece: " + directionToPiece + " | dir to self: " + directionToSelf + " | side: " + piece1Side + " | other side: " + piece2Side + ")");

            // can only connect if one is out edge and one is in edge
        if (PuzzlePiece.isCompatible(piece1Side, piece2Side)) {
            System.out.println("compatible puzzle pieces, line 129");
            return ConnectInfo.makeValidConnection(this, piece2, directionToPiece, directionToSelf, piece1Side, piece2Side);
        }
        System.out.println("incompatible puzzle pieces, line 132");
        return ConnectInfo.makeInvalidConnection(true);
    }

    // connect two puzzle pieces
    public void connect(ConnectInfo connectInfo) {
        connectSide(connectInfo.getPiece1Direction());
        connectInfo.getPiece2().connectSide(connectInfo.getPiece2Direction());

        System.out.println(
            "p1 side: " + connectInfo.getPiece1Direction() + 
            " | type: " + connectInfo.getPiece1Side() + 
            " | connected: " + connectedSides[getSideIndex(connectInfo.getPiece1Direction())]
        );
        System.out.println(
            "p2 side: " + connectInfo.getPiece2Direction() + 
            " | type: " + connectInfo.getPiece2Side() + 
            " | connected: " + connectInfo.getPiece2().connectedSides[getSideIndex(connectInfo.getPiece2Direction())]
        );
    }

    @Override
    public void update(double dt) {}
    @Override
    public void draw(Graphics2D g, int drawx, int drawy) {
        g.setColor(Color.GRAY);
        g.fillRect(drawx, drawy, gameBoard.TILE_SIZE, gameBoard.TILE_SIZE);

        Color outColor = Color.RED;
        Color inColor = Color.BLUE;
        Color edgeColor = Color.YELLOW;
        Color connectedColor = Color.GREEN;

        int right = drawx + gameBoard.TILE_SIZE;
        int bottom = drawy + gameBoard.TILE_SIZE;

        Color[] colorList = new Color[4];

        for (int i=0; i<sides.length; i++) {
            if (connectedSides[i]) {
                colorList[i] = connectedColor;
                continue;
            }
            switch (sides[i]) {
                case IN: colorList[i] = inColor; break;
                case OUT: colorList[i] = outColor; break;
                case EDGE: colorList[i] = edgeColor; break;
            }
        }
        g.setColor(colorList[0]);
        g.drawLine(drawx, drawy, right, drawy);
        g.setColor(colorList[1]);
        g.drawLine(drawx, drawy, drawx, bottom);
        g.setColor(colorList[2]);
        g.drawLine(drawx, bottom, right, bottom);
        g.setColor(colorList[3]);
        g.drawLine(right, drawy, right, bottom);
    }
}
