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
        connectInfo.getPiece1().connectSide(connectInfo.getPiece1Direction(), Side.Hierarchy.PARENT, connectInfo);
        connectInfo.getPiece2().connectSide(connectInfo.getPiece2Direction(), Side.Hierarchy.CHILD, connectInfo);

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

    private boolean isMover = false;
    private boolean connectedToMover = false;
    private int index = 0;
    private boolean hasStrongConnection = false;
    private Side[] sides;

    public PuzzlePiece(GameBoard gameBoard, int boardx, int boardy, String sideString, String baseStrengthString) {
        super(gameBoard, GameObject.ObjectType.PUZZLE_PIECE, boardx, boardy);
        this.sides = Side.getSideData(this, sideString, baseStrengthString);
    }
    public PuzzlePiece(GameBoard gameBoard, GameObject.ObjectType objectType, int boardx, int boardy, String sideString, String baseStrengthString) {
        super(gameBoard, objectType, boardx, boardy);
        this.sides = Side.getSideData(this, sideString, baseStrengthString);
    }

    public boolean equals(GameObject gameObject) {
        if (gameObject == null) return false;
        if (gameObject.getObjectType() != getObjectType()) return false;
        PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;

        boolean hasSameSides = true;
        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            if (!getSide(direction).equals(puzzlePiece.getSide(direction))) {
                hasSameSides = false;
                break;
            }
        }
        return super.equals(puzzlePiece) && hasSameSides;
    }

    // get the index of the puzzle piece in relation to the mover
    public int getIndex() { return index; }

    // whether or not the puzzle piece is the mover (initiates the motion)
    public boolean isMover() { return isMover; }
    public void setMover(boolean isMover) { this.isMover = isMover; }
    
    // whether the puzzle piece has a strong connection path to the mover
    public boolean connectedToMover() { return connectedToMover; }

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
        return hasConnectedSide(baseStrengthList, baseStrengthList);
    }
    // has a strong connected side
    public boolean hasConnectedSide(Side.Strength[] strengths1, Side.Strength[] strengths2) {
        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            if (getSide(direction).isConnected()) {
                boolean hasStrength1 = false, hasStrength2 = false; 
                Side side2 = getSide(direction).getPiece2Side();
                if (side2 == null) continue;
                Side.Strength sideStrength1 = getSide(direction).getStrength(), sideStrength2 = side2.getStrength();
                for (Side.Strength baseStrength : strengths1) {
                    if (baseStrength == sideStrength1) hasStrength1 = true;
                }
                for (Side.Strength baseStrength : strengths2) {
                    if (baseStrength == sideStrength2) hasStrength2 = true;
                }
                if (hasStrength1 && hasStrength2) return true;
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
    public void connectSide(Direction.Type direction, Side.Hierarchy hierarchy, ConnectInfo connectInfo) {
        getSide(direction).setConnected(true); 
        getSide(direction).setHierarchy(hierarchy);
        getSide(direction).setConnectInfo(connectInfo);
        System.out.println("CONNECTING SIDES");
        System.out.println(connectInfo);
    }
    
    // get the connection info between two puzzle pieces
    public ConnectInfo getConnectInfo(int movex, int movey, int hdir, int vdir) {
        Print.println("GET CONNECT INFO", Print.RED);
        System.out.println("for " + this);

        int movePosx = getBoardX() + movex;
        int movePosy = getBoardY() + movey;
        int targetx = movePosx + hdir;
        int targety = movePosy + vdir;

        System.out.println("look for connect pos: " + targetx + ", " + targety);

        // make sure it is in bounds
        if (!gameBoard.inBounds(targetx, targety) || !gameBoard.inBounds(movePosx, movePosy)) {
            //System.out.println("out of bounds, line 96");
            return ConnectInfo.makeInvalidConnection("out of bounds");
        }

        // make sure no blocks are in the way
        if (movex != 0 && movey != 0) {
            GameObject gameObject = gameBoard.getGameObject(movePosx, movePosy);
            if (gameObject != null) return ConnectInfo.makeInvalidConnection("block is in the way");
        }

        // get game object at target location
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);

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
            System.out.println("connect info" + ConnectInfo.makeValidConnection(this, piece2, directionToPiece));
            return ConnectInfo.makeValidConnection(this, piece2, directionToPiece);
        }
        //System.out.println("incompatible puzzle pieces, line 132");
        return ConnectInfo.makeInvalidConnection("incompatible puzzle pieces");
    }

    // gets all of the connected puzzle pieces
    public ArrayList<PuzzlePiece> getAdjacentConnectedPieces(Side.Strength[] connectionStrengths) {
        ArrayList <PuzzlePiece> list = new ArrayList<>();

        //System.out.println("GETTING ADJACENT CONNECTIONS FROM " + this);
        //System.out.println("connection types to look for: ");
        //System.out.println("self has to be " + ownHierarchy);
        //System.out.println("other has to be " + otherHierarchy);

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

            // game object at target cell location (cannot be null or out of bounds b/c side is connected)
            GameObject gameObject = gameBoard.getGameObject(targetx, targety);
            //System.out.println("targeted cell: " + targetx + ", " + targety + ", " + gameObject);

            // skip if already in caller list
            boolean inCallerList = false;
            for (PuzzlePiece caller : list) {
                if (caller.equals(gameObject)) {
                    inCallerList = true;
                    break;
                }
            }
            if (inCallerList) continue;

            // puzzle piece
            if (isPuzzlePiece(gameObject)) {
                PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;

                Direction.Type oppositeDirection = Direction.getOppositeDirection(direction);

                //System.out.println(puzzlePiece + " is to the " + direction);
                //System.out.println(direction + " side of self: " + getSideType(direction));
                //System.out.println(oppositeDirection + " side of other: " + getSideType(oppositeDirection));
                //System.out.println("connection type of self: " + getSideConnectionType(direction));
                //System.out.println("connection type of other: " + puzzlePiece.getSideConnectionType(oppositeDirection));


                // if connection strength param is null, accept all connection strengths
                boolean hasConnectionStrength = connectionStrengths == null;

                if (!hasConnectionStrength) {
                    // only allow puzzle pieces with connection types according to the Side.Strength[] param
                    Side.Strength connectionStrength = Side.getConnectionStrength(getSide(direction).getStrength(), getSide(oppositeDirection).getStrength());
                    // else, only allow connection strengths in list
                    for (Side.Strength baseStrength : connectionStrengths) {
                        if (baseStrength == connectionStrength) {
                            hasConnectionStrength = true;
                            break;
                        }
                    }
                }

                // only add sides that are specified in parameter
                if (hasConnectionStrength) {
                    list.add(puzzlePiece);
                }

            }
        }
        return list;
    }
    
    // gets the moveinfo based on all of the attached puzzle pieces
    // that are in the direction of movement
    // or only checks puzzle pieces that have strong connections and that have the correct hierarchy structure
    public MoveInfo getAllMoveInfo(ArrayList<GameObject> callerList, int hdir, int vdir) {
        callerList.add(this);
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
            if (selfToPiece == Direction.getDirection(-hdir, -vdir)) continue;
            
            int offsetx = Direction.getDirectionX(selfToPiece);
            int offsety = Direction.getDirectionY(selfToPiece);

            if (!gameBoard.inBounds(getBoardX() + offsetx, getBoardY() + offsety)) continue;

            GameObject gameObject = gameBoard.getGameObject(getBoardX() + offsetx, getBoardY() + offsety);

            // empty cell
            if (gameObject == null) continue;

            // already found moveInfo, ignore
            boolean inCallerList = false;
            for (GameObject caller : callerList) {
                if (caller.equals(gameObject)) {
                    inCallerList = true;
                    break;
                }
            }
            if (inCallerList) continue;

            // get the move info if it is in the direction of movement
            // or if the object is a strong connected puzzle piece
            if (offsetx == hdir && offsety == vdir) {
                // call the proper function
                if (isPuzzlePiece(gameObject)) moveInfoList[i] = ((PuzzlePiece) gameObject).getAllMoveInfo(callerList, hdir, vdir);
                else moveInfoList[i] = gameObject.getMoveInfo(hdir, vdir);
            }
            else if (isPuzzlePiece(gameObject)) {
                PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;
                Side selfSide = getSide(selfToPiece), otherSide = puzzlePiece.getSide(pieceToSelf);

                if (selfSide.isConnected() && Side.getConnectionStrength(selfSide.getStrength(), otherSide.getStrength()) == Side.Strength.STRONG) {
                    moveInfoList[i] = puzzlePiece.getAllMoveInfo(callerList, hdir, vdir);
                }
            }
        }

        System.out.println("finished checking");

        /*
        [valid, valid, disconnect, valid]
        */

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
    @Override
    public void move(MoveInfo moveInfo) {
        if(movedThisFrame() || queuedMovedThisFrame()) return;
        setQueuedMovedThisFrame(true);
        Print.println("MOVE ALL ATTACHED", Print.BLUE);
        System.out.println("for " + this);
        System.err.println("direction: " + moveInfo.getHdir() + " " + moveInfo.getVdir());

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
        hasStrongConnection = hasConnectedSide(strong, strong);
        
        System.out.println("has strong connection: " + hasStrongConnection);

        ArrayList<PuzzlePiece> adjacentConnections = getAdjacentConnectedPieces(Side.Strength.values());
        for (PuzzlePiece puzzlePiece : adjacentConnections) {
            // move puzzle piece
            puzzlePiece.move(moveInfo);
        }
        System.out.println(this + " finished moving " + adjacentConnections.size() + " attached");

        System.out.println("can move: " + canMove);
        
        if (canMove) {
            moveSelf(moveInfo);
        }
        else {
            if (!hasStrongConnection) {
                // disconnect the sides perpendicular to the direction of motion
                int[] xOffList = {moveInfo.getVdir(), -moveInfo.getVdir()};
                int[] yOffList = {moveInfo.getHdir(), -moveInfo.getHdir()};

                for (int i=0; i<xOffList.length; i++) {
                    int xOff = xOffList[i], yOff = yOffList[i];
                    Direction.Type direction = Direction.getDirection(xOff, yOff);
                    if (!getSide(direction).isConnected()) continue;

                    Print.println("DISCONNECTING ", Print.RED);
                    System.out.println("look dir: " + xOff + " " + yOff);
                    System.out.println("target pos: " + (getBoardX() + xOff) + ", " + (getBoardY() + yOff));

                    //PROBLEM
                    // the position looks at the new board and then the old board
                    // returns null b/c obj at old board position has already moved
                    // prob need to change moving, connecting, and disconnecting order

                    PuzzlePiece puzzlePiece = (PuzzlePiece) gameBoard.getGameObject(getBoardX() + xOff, getBoardY() + yOff);
                    System.out.println(puzzlePiece);

                    disconnect(new DisconnectInfo(this, puzzlePiece, direction));
                }
            }
        }
    }
    // move first, then check for connections
    @Override
    public void moveSelf(MoveInfo moveInfo) {

        if (movedThisFrame()) {
            //System.out.println(getName() + " already moved, overriding movement");
            return;
        }

        Print.println("MOVING " + this, Print.BLUE);

        // move
        super.moveSelf(moveInfo);

        // check if it can connect with other puzzle pieces
        ConnectInfo[] connectInfoList = new ConnectInfo[4];

        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            if (getSide(direction).isConnected() || !getSide(direction).canConnect()) continue;
            int connectDirx = Direction.getDirectionX(direction), connectDiry = Direction.getDirectionY(direction);
            if (connectDirx == -moveInfo.getHdir() && connectDiry == -moveInfo.getVdir()) continue;
            connectInfoList[i] = getConnectInfo(0, 0, connectDirx, connectDiry);
            System.out.println(connectInfoList[i]);
        }
        //Print.println(" PUZZLE PIECE MOVE FUNCTION: " + connectInfo, Print.PURPLE);
        for (ConnectInfo connectInfo : connectInfoList) {
            if (connectInfo == null) continue;
            if (connectInfo.canConnect()) {
                PuzzlePiece.connect(connectInfo);
            }
        }
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
                default: break;
            }
        }
        int[] x1List = {drawx, drawx, drawx, right};
        int[] x2List = {right, drawx, right, right};
        int[] y1List = {drawy, drawy, bottom, drawy};
        int[] y2List = {drawy, bottom, bottom, bottom};
        for (int i=0; i<4; i++) {
            if (colorList[i] == null) continue;
            int x1 = x1List[i], y1 = y1List[i], x2 = x2List[i], y2 = y2List[i];

            g.setColor(colorList[i]);
            g.setStroke(new BasicStroke(getSide(Direction.getDirection(i)).getStrength() == Side.Strength.STRONG ? strongLineWidth : weakLineWidth));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    @Override
    public void updateInfoList(Graphics2D g, int drawcx, int drawbottomy) {
        ArrayList<String> drawList = new ArrayList<String>();

        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() + ")");
        drawList.add("hasStrongConnection: " + hasStrongConnection);
        drawList.add("sides: ");
        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            drawList.add(direction + ": " + getSide(direction));
        }

        updateInfoList(g, drawcx, drawbottomy, drawList);
    }
}
