package gameplay.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Console;
import java.util.ArrayList;

import gameplay.GameBoard;
import utils.*;
import utils.Direction.Type;

public class PuzzlePiece extends GameObject {

    public enum Side {
        EDGE,
        OUT,
        IN,;
    }
    public enum ConnectionType {
        PARENT,
        CHILD,
        EMPTY
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
        Side side1 = connectInfo.getPiece1Side();
        Side side2 = connectInfo.getPiece2Side();
        return isCompatible(side1, side2) && 
        connectInfo.getPiece1().getSideConnectionType(connectInfo.getPiece1Direction()) == ConnectionType.EMPTY &&
        connectInfo.getPiece2().getSideConnectionType(connectInfo.getPiece2Direction()) == ConnectionType.EMPTY;
    }

    // connect two puzzle pieces
    public static void connect(ConnectInfo connectInfo) {
        connectInfo.getPiece1().connectSide(connectInfo.getPiece1Direction(), ConnectionType.PARENT);
        connectInfo.getPiece2().connectSide(connectInfo.getPiece2Direction(), ConnectionType.CHILD);

        //System.out.println(Print.YELLOW + "CONNECTION DATA: " + Print.RESET);
        //System.out.println(
        //    "p1: " + connectInfo.getPiece1() + 
        //    "| side: " + connectInfo.getPiece1Direction() + 
        //    " | type: " + connectInfo.getPiece1Side() + 
        //    " | connection type: " + connectInfo.getPiece1().getSideConnectionType(connectInfo.getPiece1Direction())
        //);
        //System.out.println(
        //    "p2: " + connectInfo.getPiece2() +
        //    " | side: " + connectInfo.getPiece2Direction() + 
        //    " | type: " + connectInfo.getPiece2Side() + 
        //    " | connection type: " + connectInfo.getPiece2().getSideConnectionType(connectInfo.getPiece2Direction())
        //);
    }

    private Side[] sides;
    private ConnectionType[] sideConnectionType = {
        ConnectionType.EMPTY, 
        ConnectionType.EMPTY, 
        ConnectionType.EMPTY, 
        ConnectionType.EMPTY 
    };

    public PuzzlePiece(GameBoard gameBoard, int boardx, int boardy, String sideString) {
        super(gameBoard, GameObject.ObjectType.PUZZLE_PIECE, boardx, boardy);
        this.sides = getSideData(sideString);
    }
    public PuzzlePiece(GameBoard gameBoard, GameObject.ObjectType objectType, int boardx, int boardy, String sideString) {
        super(gameBoard, objectType, boardx, boardy);
        this.sides = getSideData(sideString);
    }

    // gets the type of side based on a direction
    public Side getSideType(Direction.Type direction) { return sides[Direction.getIndex(direction)]; }
    // get connection type of side
    public ConnectionType getSideConnectionType(Direction.Type direction) { return sideConnectionType[Direction.getIndex(direction)]; }
    // gets if a side is already connected with another piece's side based on a direction
    public boolean isSideConnected(Direction.Type direction) { return sideConnectionType[Direction.getIndex(direction)] != ConnectionType.EMPTY; }
    // if the connectionType is connected
    public boolean isConnectionConnected(ConnectionType connectionType) { return connectionType != ConnectionType.EMPTY; }
    // if at least one of the sides are connected
    public boolean hasConnection() {
        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            if (getSideType(direction) != Side.EDGE && getSideConnectionType(direction) == ConnectionType.EMPTY) {
                return false;
            }
        }
        return true;
    }
    // connect a side
    public void connectSide(Direction.Type direction, ConnectionType connectionType) { sideConnectionType[Direction.getIndex(direction)] = connectionType; }
    
    // get the connection info between two puzzle pieces
    public ConnectInfo getConnectInfo(int movex, int movey, int hdir, int vdir) {

        int targetx = getBoardX() + movex + hdir;
        int targety = getBoardY() + movey + vdir;

        // make sure it is in bounds
        if (!gameBoard.inBounds(targetx, targety)) {
            //System.out.println("out of bounds, line 96");
            return ConnectInfo.makeInvalidConnection(true);
        }

        // get game object at target location
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);

        // can only connect with puzzle pieces
        if (gameObject == null) {
            //System.out.println("null gameObject, line 105");
            //System.out.println("target pos: " + targetx + ", " + targety);
            //System.out.println("move offset: " + movex + ", " + movey);
            //System.out.println("side to get connect info on: " + hdir + ", " + vdir);
            return ConnectInfo.makeInvalidConnection(true);
        }
        if (gameObject.getObjectType() != GameObject.ObjectType.PUZZLE_PIECE && gameObject.getObjectType() != GameObject.ObjectType.PLAYER_PIECE) {
            //System.out.println("gameObject other than puzzle/player, line 109");
            return ConnectInfo.makeInvalidConnection(true);
        }

        // get the other puzzle piece
        PuzzlePiece piece2 = (PuzzlePiece) gameObject;

        // out facing edges can only connect with in facing edges
        // flat edges cannot connect with anything
            // find the direction from self to piece and direction from piece to self
        Direction.Type directionToPiece = Direction.getDirection(hdir, vdir);
        Direction.Type directionToSelf = Direction.getDirection(-hdir, -vdir);
            // find the type of sides that correspond with directions
        Side piece1Side = getSideType(directionToPiece);
        Side piece2Side = piece2.getSideType(directionToSelf);

        //System.out.println("connection info(dir to piece: " + directionToPiece + " | dir to self: " + directionToSelf + " | side: " + piece1Side + " | other side: " + piece2Side + ")");

            // can only connect if one is out edge and one is in edge
        if (PuzzlePiece.isCompatible(piece1Side, piece2Side) && !this.isSideConnected(directionToPiece) && !piece2.isSideConnected(directionToSelf)) {
            //System.out.println("compatible puzzle pieces, line 129");
            return ConnectInfo.makeValidConnection(this, piece2, directionToPiece, directionToSelf, piece1Side, piece2Side);
        }
        //System.out.println("incompatible puzzle pieces, line 132");
        return ConnectInfo.makeInvalidConnection(true);
    }

    // gets all of the connected puzzle pieces
    public ArrayList<PuzzlePiece> getAdjacentConnectedPieces(ConnectionType ownConnectionType, ConnectionType otherConnectionType) {

        //System.out.println("GETTING ADJACENT CONNECTIONS FROM " + this);
        //System.out.println("connection types to look for: ");
        //System.out.println("self has to be " + ownConnectionType);
        //System.out.println("other has to be " + otherConnectionType);

        ArrayList<PuzzlePiece> connectedPiecesList = new ArrayList<PuzzlePiece>();

        for (Direction.Type direction : Direction.DirectionList) {

            //System.out.println("direction: " + direction);

            int hdir = Direction.getDirectionX(direction);
            int vdir = Direction.getDirectionY(direction);

            int targetx = getBoardX() + hdir;
            int targety = getBoardY() + vdir;
            GameObject gameObject = gameBoard.getGameObject(targetx, targety);
            PuzzlePiece puzzlePiece;
            
            //System.out.println("targeted cell: " + targetx + ", " + targety + ", " + gameObject);

            if (gameObject == null) {
                continue;
            }
            if (gameObject.getObjectType() == GameObject.ObjectType.PLAYER_PIECE || gameObject.getObjectType() == GameObject.ObjectType.PUZZLE_PIECE) {
                puzzlePiece = (PuzzlePiece) gameObject;

                Direction.Type oppositeDirection = Direction.getOppositeDirection(direction);

                //System.out.println(puzzlePiece + " is to the " + direction);
                //System.out.println(direction + " side of self: " + getSideType(direction));
                //System.out.println(oppositeDirection + " side of other: " + getSideType(oppositeDirection));
                //System.out.println("connection type of self: " + getSideConnectionType(direction));
                //System.out.println("connection type of other: " + puzzlePiece.getSideConnectionType(oppositeDirection));

                if (ownConnectionType == null  && otherConnectionType == null) {
                    connectedPiecesList.add(puzzlePiece);
                    continue;
                }
                if (getSideConnectionType(direction) == ownConnectionType && puzzlePiece.getSideConnectionType(oppositeDirection) == otherConnectionType) {
                    connectedPiecesList.add(puzzlePiece);
                    continue;
                }
            }
        }
        return connectedPiecesList;
    }
    
    // gets the moveinfo based on all of the attached puzzle pieces
    public MoveInfo getAllMoveInfo(int hdir, int vdir) {
        //System.out.println(Print.PURPLE + "GET ALL MOVE INFO FOR " + this + Print.RESET);
        // make sure movement is in bounds and is valid
        // currently using base GameObject.getMoveInfo
        // need to create PuzzlePiece.getMoveInfo to account for any connected PuzzlePieces

        // get the move info for all four sides (if they are connected) to make sure anything connected can also move
        MoveInfo[] moveInfoList = new MoveInfo[5];

        for (int i=0; i<4; i++) {
            Direction.Type selfToPiece = Direction.getDirection(i);
            Direction.Type pieceToSelf = Direction.getDirection(Direction.getOppositeDirectionIndex(i));

            // only get the move info if 
            //      the side is connected to a puzzle piece and it is a PARENT-CHILD relationship
            //      the side is not in the opposite direction of the current movement (that will cause a logical error)
            if (isSideConnected(selfToPiece) && selfToPiece != Direction.getDirection(-hdir, -vdir)) {
                int offsetx = Direction.getDirectionX(selfToPiece);
                int offsety = Direction.getDirectionY(selfToPiece);
                PuzzlePiece puzzlePiece = (PuzzlePiece) gameBoard.getGameObject(getBoardX() + offsetx, getBoardY() + offsety);
                //System.out.println("offset: " + offsetx + ", " + offsety);
                //System.out.println("target: " + getBoardX() + offsetx + ", " + getBoardY() + offsety);
                //System.out.println("object: " + puzzlePiece);

                // checking for parent child relationship
                if (getSideConnectionType(selfToPiece) == ConnectionType.PARENT && puzzlePiece.getSideConnectionType(pieceToSelf) == ConnectionType.CHILD) {
                    moveInfoList[i] = puzzlePiece.getAllMoveInfo(hdir, vdir);
                }
                //moveInfoList[i] = getMoveInfo(offsetx, offsety, hdir, vdir);
            }
        }

        // also always get move info for base piece with no side offsets
        moveInfoList[4] = getMoveInfo(hdir, vdir);

        //System.out.println("move info list of player: ");
        //for (int i=0; i<5; i++) {
              //System.out.println("side " + i + ": " + moveInfoList[i]);
        //}

        // can only move if all of the moveInfos are valid
        for (int i=0; i<5; i++) {
            if (moveInfoList[i] == null) { continue; }
            if (!moveInfoList[i].canMove()) {
                return MoveInfo.makeInvalidMove();
            }
        }
        return moveInfoList[4];
    }
    // moves itself and any attached puzzle pieces
    // also look for any new connections to attach puzzle pieces
    public void moveAllAttached(MoveInfo moveInfo) {
        //System.out.println(Print.RED + "MOVING ALL ATTACHED FOR: " + this + Print.RESET);
        //System.out.println("getting adjacent connected pieces in moveAllAttached function");
        ArrayList<PuzzlePiece> adjecentConnections = getAdjacentConnectedPieces(ConnectionType.PARENT, ConnectionType.CHILD);
        //System.out.println(Print.BLUE + "puzzle pieces: " + Print.RESET);
        for (PuzzlePiece puzzlePiece : adjecentConnections) {
            //System.out.println("connected piece: " + puzzlePiece);

            // connect puzzle pieces together
            ConnectInfo connectInfo = puzzlePiece.getConnectInfo(moveInfo.getHdir(), moveInfo.getVdir(), moveInfo.getHdir(), moveInfo.getVdir());
            if (connectInfo.canConnect()) {
                PuzzlePiece.connect(connectInfo);
            }

            // move puzzle piece
            puzzlePiece.moveAllAttached(moveInfo);
        }
        move(moveInfo);
    }
    @Override
    public void update(double dt) {}
    @Override
    public void draw(Graphics2D g, int drawx, int drawy) {
        g.setColor(Color.GRAY);
        g.fillRect(drawx, drawy, gameBoard.tileSize, gameBoard.tileSize);

        Color outColor = Color.RED;
        Color inColor = Color.BLUE;
        Color edgeColor = Color.YELLOW;
        Color connectedColor = Color.GREEN;

        int right = drawx + gameBoard.tileSize;
        int bottom = drawy + gameBoard.tileSize;

        Color[] colorList = new Color[4];

        for (int i=0; i<sides.length; i++) {
            if (isConnectionConnected(sideConnectionType[i])) {
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
