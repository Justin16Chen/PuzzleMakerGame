package gameplay.gameObjects.puzzlePiece;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.ArrayList;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import utils.*;

public class PuzzlePiece extends GameObject {

    public static boolean isPuzzlePiece(GameObject gameObject) {
        if (gameObject == null) {
            return false;
        }
        return gameObject.getObjectType() == GameObject.ObjectType.PLAYER_PIECE || gameObject.getObjectType() == GameObject.ObjectType.PUZZLE_PIECE;
    }

    // whether this piece can connect to another puzzle piece or not
    public static boolean canConnect(ConnectInfo connectInfo) {
        Side side1 = connectInfo.getPiece1Side();
        Side side2 = connectInfo.getPiece2Side();
        return Side.isCompatible(side1, side2);
    }

    // connect two puzzle pieces
    public static void connect(ConnectInfo connectInfo) {
        connectInfo.getPiece1().connectSide(connectInfo.getPiece1Direction(), Side.Hierarchy.PARENT);
        connectInfo.getPiece2().connectSide(connectInfo.getPiece2Direction(), Side.Hierarchy.CHILD);

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
    public static void disconnect(DisconnectInfo disconnectInfo) {
        PuzzlePiece piece1 = disconnectInfo.getPiece1();
        PuzzlePiece piece2 = disconnectInfo.getPiece2();
        piece1.getSide(disconnectInfo.getPiece1Direction()).setConnected(false);
        piece2.getSide(disconnectInfo.getPiece2Direction()).setConnected(false);
    }

    private Side[] sides;

    public PuzzlePiece(GameBoard gameBoard, int boardx, int boardy, String sideString, String baseStrengthString) {
        super(gameBoard, GameObject.ObjectType.PUZZLE_PIECE, boardx, boardy);
        this.sides = Side.getSideData(sideString, baseStrengthString);
    }
    public PuzzlePiece(GameBoard gameBoard, GameObject.ObjectType objectType, int boardx, int boardy, String sideString, String baseStrengthString) {
        super(gameBoard, objectType, boardx, boardy);
        this.sides = Side.getSideData(sideString, baseStrengthString);
    }

    // gets the type of side based on a direction
    public Side getSide(Direction.Type direction) {
        return sides[Direction.getIndex(direction)];
    }
    // all of the sides are connected
    public boolean hasConnectedSide() {
        Side.Strength[] baseStrengthList = {
            Side.Strength.STRONG,
            Side.Strength.WEAK
        };
        return hasConnectedSide(baseStrengthList);
    }
    // has a strong connected side
    public boolean hasConnectedSide(Side.Strength[] baseStrengths) {
        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            if (getSide(direction).isConnected()) {
                Side.Strength sideStrength = getSide(direction).getStrength();
                for (Side.Strength baseStrength : baseStrengths) {
                    if (baseStrength == sideStrength) return true;
                }
            }
        }
        return false;
    }
    // get all sides that match the parameters
    public ArrayList<Side> getSides(boolean connected, Side.Strength[] baseStrengths) {
        ArrayList<Side> sides = new ArrayList<>();
        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            Side side = getSide(direction);
            if (side.isConnected() == connected) {
                for (Side.Strength baseStrength : baseStrengths) {
                    if (side.getStrength() == baseStrength) sides.add(side);
                }
            }
        }
        return sides;
    }
    // connect a side
    public void connectSide(Direction.Type direction, Side.Hierarchy hierarchy) {
        getSide(direction).setConnected(true); 
        getSide(direction).setHierarchy(hierarchy);
    }
    
    // get the connection info between two puzzle pieces
    public ConnectInfo getConnectInfo(int movex, int movey, int hdir, int vdir) {
        Print.println("GET CONNECT INFO", Print.RED);
        System.out.println("for " + this);

        int movePosx = getBoardX() + movex;
        int movePosy = getBoardY() + movey;
        int targetx = getBoardX() + movex + hdir;
        int targety = getBoardY() + movey + vdir;

        // make sure it is in bounds
        if (!gameBoard.inBounds(targetx, targety) || !gameBoard.inBounds(movePosx, movePosy)) {
            //System.out.println("out of bounds, line 96");
            return ConnectInfo.makeInvalidConnection("out of bounds");
        }

        // make sure no blocks are in the way
        GameObject gameObject = gameBoard.getGameObject(movePosx, movePosy);
        if (gameObject != null) {
            return ConnectInfo.makeInvalidConnection("block is in the way");
        }

        // get game object at target location
        gameObject = gameBoard.getGameObject(targetx, targety);

        // can only connect with puzzle pieces
        if (gameObject == null) {
            //System.out.println("null gameObject, line 105");
            //System.out.println("target pos: " + targetx + ", " + targety);
            //System.out.println("move offset: " + movex + ", " + movey);
            //System.out.println("side to get connect info on: " + hdir + ", " + vdir);
            return ConnectInfo.makeInvalidConnection("empty cell");
        }
        if (gameObject.getObjectType() != GameObject.ObjectType.PUZZLE_PIECE && gameObject.getObjectType() != GameObject.ObjectType.PLAYER_PIECE) {
            //System.out.println("gameObject other than puzzle/player, line 109");
            return ConnectInfo.makeInvalidConnection("not a puzzle piece");
        }

        // get the other puzzle piece
        PuzzlePiece piece2 = (PuzzlePiece) gameObject;

        // out facing edges can only connect with in facing edges
        // flat edges cannot connect with anything
            // find the direction from self to piece and direction from piece to self
        Direction.Type directionToPiece = Direction.getDirection(hdir, vdir);
        Direction.Type directionToSelf = Direction.getDirection(-hdir, -vdir);
            // find the type of sides that correspond with directions
        Side piece1Side = getSide(directionToPiece);
        Side piece2Side = piece2.getSide(directionToSelf);

        //System.out.println("connection info(dir to piece: " + directionToPiece + " | dir to self: " + directionToSelf + " | side: " + piece1Side + " | other side: " + piece2Side + ")");

            // can only connect if one is out edge and one is in edge
        if (Side.isCompatible(piece1Side, piece2Side) && !piece1Side.isConnected() && !piece2Side.isConnected()) {
            //System.out.println("compatible puzzle pieces, line 129");
            //System.out.println("puzzle piece sides: " + piece1Side.getType() + " " + piece2Side.getType());
            System.out.println("piece1: " + this + " piece 2: " + piece2);
            return ConnectInfo.makeValidConnection(this, piece2, directionToPiece);
        }
        //System.out.println("incompatible puzzle pieces, line 132");
        return ConnectInfo.makeInvalidConnection("incompatible puzzle pieces");
    }

    // gets all of the connected puzzle pieces
    public ArrayList<PuzzlePiece> getAdjacentConnectedPieces(Side.Hierarchy ownHierarchy, Side.Hierarchy otherHierarchy, Side.Strength[] connectionStrengths) {

        //System.out.println("GETTING ADJACENT CONNECTIONS FROM " + this);
        //System.out.println("connection types to look for: ");
        //System.out.println("self has to be " + ownHierarchy);
        //System.out.println("other has to be " + otherHierarchy);

        ArrayList<PuzzlePiece> connectedPiecesList = new ArrayList<PuzzlePiece>();

        for (Direction.Type direction : Direction.DirectionList) {

            // skip non-connected sides
            if (!getSide(direction).isConnected()) {
                continue;
            }

            // find adjacent cell location
            int hdir = Direction.getDirectionX(direction);
            int vdir = Direction.getDirectionY(direction);
            int targetx = getBoardX() + hdir;
            int targety = getBoardY() + vdir;

            // out of bounds
            if (!gameBoard.inBounds(targetx, targety)) {
                continue;
            }

            // game object at target cell location (cannot be null b/c side is connected)
            GameObject gameObject = gameBoard.getGameObject(targetx, targety);
            //System.out.println("targeted cell: " + targetx + ", " + targety + ", " + gameObject);

            // puzzle piece
            if (isPuzzlePiece(gameObject)) {
                PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;

                Direction.Type oppositeDirection = Direction.getOppositeDirection(direction);

                //System.out.println(puzzlePiece + " is to the " + direction);
                //System.out.println(direction + " side of self: " + getSideType(direction));
                //System.out.println(oppositeDirection + " side of other: " + getSideType(oppositeDirection));
                //System.out.println("connection type of self: " + getSideConnectionType(direction));
                //System.out.println("connection type of other: " + puzzlePiece.getSideConnectionType(oppositeDirection));

                // only allow puzzle pieces with connection types according to the Side.Strength[] param
                Side.Strength connectionStrength = Side.getConnectionStrength(getSide(direction).getStrength(), getSide(oppositeDirection).getStrength());

                // if connection strength param is null, accept all connection strengths
                boolean hasConnectionStrength = connectionStrengths == null;

                // else, only allow connection strengths in list
                for (Side.Strength baseStrength : connectionStrengths) {
                    if (baseStrength == connectionStrength) {
                        hasConnectionStrength = true;
                        break;
                    }
                }

                // only add sides that are specified in parameter
                if (!hasConnectionStrength) {
                    continue;
                }

                // if params are null, disregard hierarchical status when searching for connected puzzle pieces
                // otherwise, only accept puzzle pieces with correct hierarchical status
                boolean p1HierarchyValid = ownHierarchy == null || getSide(direction).getHierarchy() == ownHierarchy;
                boolean p2HierarchyValid = otherHierarchy == null || puzzlePiece.getSide(oppositeDirection).getHierarchy() == otherHierarchy;

                // if both sides fit all requirements, add to list
                if (p1HierarchyValid && p2HierarchyValid) {
                    connectedPiecesList.add(puzzlePiece);
                }
            }
        }
        return connectedPiecesList;
    }
    
    // gets the moveinfo based on all of the attached puzzle pieces
    // only checks puzzle pieces that have strong connections or are in the direction of movement
    // and that have the correct hierarchy structure
    public MoveInfo getAllMoveInfo(int hdir, int vdir) {
        Print.println("GET ALL MOVE INFO", Print.PURPLE);
        System.out.println("for " + this);
        System.out.println("dir of motion: " + hdir + ", " + vdir);
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
            //      the connection type is STRONG (weak connection types can be broken, do not need to consider when computing valid move data)
            if (!getSide(selfToPiece).isConnected() || selfToPiece == Direction.getDirection(-hdir, -vdir)) continue;
            
            int offsetx = Direction.getDirectionX(selfToPiece);
            int offsety = Direction.getDirectionY(selfToPiece);
            PuzzlePiece puzzlePiece = (PuzzlePiece) gameBoard.getGameObject(getBoardX() + offsetx, getBoardY() + offsety);
            Side selfSide = getSide(selfToPiece);
            Side otherSide = puzzlePiece.getSide(pieceToSelf);
            
            //System.out.println("offset: " + offsetx + ", " + offsety);
            //System.out.println("target: " + getBoardX() + offsetx + ", " + getBoardY() + offsety);
            //System.out.println("object: " + puzzlePiece);

            // checking for parent child relationship
            if (selfSide.getHierarchy() == Side.Hierarchy.PARENT && otherSide.getHierarchy() == Side.Hierarchy.CHILD) {

                // checking for strong connection or if direction is in direction of motion
                // FIX THIS CODE
                if (Side.getConnectionStrength(selfSide.getStrength(), otherSide.getStrength()) == Side.Strength.STRONG || offsetx == hdir && offsety == vdir) {
                    moveInfoList[i] = puzzlePiece.getAllMoveInfo(hdir, vdir);
                }
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
            if (moveInfoList[i] != null && !moveInfoList[i].canMove()) {
                return MoveInfo.makeInvalidMove();
            }
        }
        return moveInfoList[4];
    }
    // moves itself and any attached puzzle pieces
    // also look for any new connections to attach puzzle pieces
    public void moveAllAttached(MoveInfo moveInfo) {
        Print.println("MOVE ALL ATTACHED", Print.BLUE);
        System.out.println("for " + this);
        System.err.println("direction: " + moveInfo.getHdir() + " " + moveInfo.getVdir());

        if(movedThisFrame()) return;

        boolean canMove = getMoveInfo(moveInfo.getHdir(), moveInfo.getVdir()).canMove();

        // find strong connection perpendicular to direction of motion
        int[] xOffset = new int[2];
        int[] yOffset = new int[2];
        xOffset[0] =  moveInfo.getVdir();
        xOffset[1] = -moveInfo.getVdir();
        yOffset[0] =  moveInfo.getHdir();
        yOffset[1] = -moveInfo.getHdir();

        // only one of the connections has to be strong for the puzzle piece to have to move
        Side.Strength strong[] = { Side.Strength.STRONG };
        boolean hasStrongConnection = hasConnectedSide(strong);
        
        ArrayList<PuzzlePiece> adjecentConnections = getAdjacentConnectedPieces(Side.Hierarchy.PARENT, Side.Hierarchy.CHILD, Side.Strength.values());
        for (PuzzlePiece puzzlePiece : adjecentConnections) {
            Print.println("connected puzzle piece to move: " + puzzlePiece, Print.YELLOW);
            // move puzzle piece
            puzzlePiece.moveAllAttached(moveInfo);
        }
        
        if (canMove) {
            move(moveInfo);
        }
        else {
            System.out.println(this + " cannot move - has strong connection: " + hasStrongConnection);
            if (!hasStrongConnection) {
                // disconnect the sides perpendicular to the direction of motion
                int[] xOffList = {moveInfo.getVdir(), -moveInfo.getVdir()};
                int[] yOffList = {moveInfo.getHdir(), -moveInfo.getHdir()};

                for (int i=0; i<xOffList.length; i++) {
                    int xOff = xOffList[i], yOff = yOffList[i];
                    Direction.Type direction = Direction.getDirection(xOff, yOff);
                    if (!getSide(direction).isConnected()) continue;

                    System.out.println(xOff + " " + yOff);
                    PuzzlePiece puzzlePiece = (PuzzlePiece) gameBoard.getGameObject(getBoardX() + xOff, getBoardY() + yOff);
                    disconnect(new DisconnectInfo(this, puzzlePiece, direction));
                    System.out.println("disconnecting " + this + " and " + puzzlePiece);
                }
            }
        }
    }

    @Override
    public void move(MoveInfo moveInfo) {

        if (movedThisFrame()) {
            //System.out.println(getName() + " already moved, overriding movement");
            return;
        }

        // check if it can connect with other puzzle pieces
        ConnectInfo connectInfo = getConnectInfo(moveInfo.getHdir(), moveInfo.getVdir(), moveInfo.getHdir(), moveInfo.getVdir());
        //Print.println(" PUZZLE PIECE MOVE FUNCTION: " + connectInfo, Print.PURPLE);
        if (connectInfo.canConnect()) {
            PuzzlePiece.connect(connectInfo);
        }

        // move
        super.move(moveInfo);
    }
    @Override
    public void update(double dt) {}
    @Override
    public void draw(Graphics2D g, int drawx, int drawy) {

        g.setColor(Color.GRAY);
        if (hasConnectedSide()) {
            g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRect(drawx, drawy, gameBoard.tileSize, gameBoard.tileSize);

        Color negColor = Color.RED;
        Color posColor = Color.BLUE;
        Color neutralColor = Color.LIGHT_GRAY;
        Color connectedColor = Color.GREEN;
        int strongLineWidth = 7;
        int weakLineWidth = 3;

        int right = drawx + gameBoard.tileSize;
        int bottom = drawy + gameBoard.tileSize;

        Color[] colorList = new Color[4];

        for (int i=0; i<sides.length; i++) {
            Direction.Type direction = Direction.getDirection(i);
            if (getSide(direction).isConnected()) {
                colorList[i] = connectedColor;
                continue;
            }
            switch (getSide(direction).getType()) {
                case POSITIVE: colorList[i] = posColor; break;
                case NEGATIVE: colorList[i] = negColor; break;
                case NEUTRAL: colorList[i] = neutralColor; break;
            }
        }
        int[] x1List = {drawx, drawx, drawx, right};
        int[] x2List = {right, drawx, right, right};
        int[] y1List = {drawy, drawy, bottom, drawy};
        int[] y2List = {drawy, bottom, bottom, bottom};
        for (int i=0; i<4; i++) {
            int x1 = x1List[i], y1 = y1List[i], x2 = x2List[i], y2 = y2List[i];

            g.setColor(colorList[i]);
            g.setStroke(new BasicStroke(getSide(Direction.getDirection(i)).getStrength() == Side.Strength.STRONG ? strongLineWidth : weakLineWidth));
            g.drawLine(x1, y1, x2, y2);
        }
    }
}
