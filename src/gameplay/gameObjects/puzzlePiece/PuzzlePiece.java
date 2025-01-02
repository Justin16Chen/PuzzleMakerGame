package gameplay.gameObjects.puzzlePiece;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import utils.*;
import utils.direction.Direction;
import utils.direction.Directions;

public class PuzzlePiece extends GameObject {

    public static Color COLOR = new Color(72, 72, 72);
    public static Color OUTLINE_COLOR = COLOR;

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
        connectInfo.getPiece1().connectSide(connectInfo.getPiece1Direction(), connectInfo);
        connectInfo.getPiece2().connectSide(connectInfo.getPiece2Direction(), connectInfo);

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
        Print.println("disconnecting " + disconnectInfo, Print.YELLOW);
        Side side1 = disconnectInfo.getPiece1().getSide(disconnectInfo.getPiece1Direction());
        Side side2 = disconnectInfo.getPiece2().getSide(disconnectInfo.getPiece2Direction());

        side1.setConnected(false);
        side2.setConnected(false);
    }

    private Side[] sides;
    private Color color;

    public PuzzlePiece(GameBoard gameBoard, int boardx, int boardy, String sideString, String baseStrengthString) {
        super(gameBoard, GameObject.ObjectType.PUZZLE_PIECE, boardx, boardy);
        this.sides = Side.getSideData(this, sideString, baseStrengthString);
        this.color = COLOR;
    }
    public PuzzlePiece(GameBoard gameBoard, GameObject.ObjectType objectType, int boardx, int boardy, String sideString, String baseStrengthString) {
        super(gameBoard, objectType, boardx, boardy);
        this.sides = Side.getSideData(this, sideString, baseStrengthString);
        this.color = COLOR;
    }

    public boolean equals(GameObject gameObject) {
        if (gameObject == null) return false;
        if (gameObject.getObjectType() != getObjectType()) return false;
        PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;

        boolean hasSameSides = true;
        for (Direction direction : Directions.getAllDirections()) {
            if (!getSide(direction).equals(puzzlePiece.getSide(direction))) {
                hasSameSides = false;
                break;
            }
        }
        return super.equals(puzzlePiece) && hasSameSides;
    }

    // gets the type of side based on a direction
    public Side getSide(Direction direction) {
        return sides[Directions.getMoveIndex(direction)];
    }
    // all of the sides are connected
    public boolean hasConnectedSide() {
        for (Direction direction : Directions.getAllDirections()) {
            if (getSide(direction).isConnected())
                return true;
        }
        return false;
    }
    // connect a side
    public void connectSide(Direction direction, ConnectInfo connectInfo) {
        getSide(direction).setConnected(true); 
        getSide(direction).setConnectInfo(connectInfo);
        // System.out.println("CONNECTING SIDES");
        // System.out.println(connectInfo);
    }
    
    // get the connection info between two puzzle pieces
    public ConnectInfo getConnectInfo(int movex, int movey, int hdir, int vdir) {
        // Print.println("GET CONNECT INFO", Print.RED);
        // System.out.println("for " + this);

        int movePosx = getBoardX() + movex;
        int movePosy = getBoardY() + movey;
        int targetx = movePosx + hdir;
        int targety = movePosy + vdir;

        // System.out.println("look for connect pos: " + targetx + ", " + targety);

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
        Direction directionToPiece = Directions.getDirection(hdir, vdir);
        Direction directionToSelf = Directions.getDirection(-hdir, -vdir);
            // find the type of sides that correspond with directions
        Side piece1Side = getSide(directionToPiece);
        Side piece2Side = piece2.getSide(directionToSelf);

        //System.out.println("connection info(dir to piece: " + directionToPiece + " | dir to self: " + directionToSelf + " | side: " + piece1Side + " | other side: " + piece2Side + ")");

            // can only connect if one is out edge and one is in edge
        if (Side.isCompatible(piece1Side, piece2Side) && !piece1Side.isConnected() && !piece2Side.isConnected()) {
            //System.out.println("compatible puzzle pieces, line 129");
            //System.out.println("puzzle piece sides: " + piece1Side.getType() + " " + piece2Side.getType());
            // System.out.println("piece1: " + this + " piece 2: " + piece2);
            // System.out.println("connect info" + ConnectInfo.makeValidConnection(this, piece2, directionToPiece));
            return ConnectInfo.makeValidConnection(this, piece2, directionToPiece);
        }
        //System.out.println("incompatible puzzle pieces, line 132");
        return ConnectInfo.makeInvalidConnection("incompatible puzzle pieces");
    }

    // gets the moveinfo based on all of the attached puzzle pieces
    // that are in the direction of movement
    // or only checks puzzle pieces that have strong connections and that have the correct hierarchy structure
    public MoveInfo getAllMoveInfo(ArrayList<GameObject> callerList, int hdir, int vdir) {
        callerList.add(this);
        // Print.println("GET ALL MOVE INFO", Print.PURPLE);
        // System.out.println("for " + this);
        // System.out.println("dir of motion: " + hdir + ", " + vdir);
        //System.out.println(Print.PURPLE + "GET ALL MOVE INFO FOR " + this + Print.RESET);
        // make sure movement is in bounds and is valid
        // currently using base GameObject.getMoveInfo
        // need to create PuzzlePiece.getMoveInfo to account for any connected PuzzlePieces

        // get the move info for all four sides (if they are connected) to make sure anything connected can also move
        MoveInfo[] moveInfoList = new MoveInfo[5];

        for (int i=0; i<4; i++) {
            Direction selfToPiece = Directions.getDirection(i);
            Direction pieceToSelf = Directions.getDirection(Directions.getOppositeDirectionIndex(i));
            
            int offsetx = Directions.getDirectionX(selfToPiece);
            int offsety = Directions.getDirectionY(selfToPiece);

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

                if (selfSide.isConnected() && Side.getConnectionType(selfSide.getType(), otherSide.getType()) == Side.Type.STRONG) {
                    moveInfoList[i] = puzzlePiece.getAllMoveInfo(callerList, hdir, vdir);
                }
            }
        }

        // System.out.println("finished checking");

        /*
        [valid, valid, disconnect, valid]
        */

        // also always get move info for base piece with no side offsets
        moveInfoList[4] = getMoveInfo(hdir, vdir);

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
    public void move(MoveInfo moveInfo, boolean isMover) {
        if(movedThisFrame() || queuedMovedThisFrame()) return;
        setQueuedMovedThisFrame(true);
        Print.println("MOVE ALL ATTACHED", Print.BLUE);
        System.out.println("for " + this);
        System.out.println("direction: (" + moveInfo.getHdir() + ", " + moveInfo.getVdir() + ")");

        // check if this puzzle piece can move
        boolean canMove = getMoveInfo(moveInfo.getHdir(), moveInfo.getVdir()).canMove();
        
        if (canMove) {
            // move connected puzzle pieces or disconnect them
            for (Direction direction : Directions.getAllDirections()) {
                Side side = getSide(direction);
                if (side.isConnected())
                        side.getPiece2().move(moveInfo, false);
            }
            // move self
            moveSelf(moveInfo);
        }
    }
    // move first, then check for connections
    @Override
    public void moveSelf(MoveInfo moveInfo) {

        if (movedThisFrame()) {
            // System.out.println(getName() + " already moved, overriding movement");
            return;
        }

        // Print.println("MOVING " + this, Print.BLUE);

        // move
        super.moveSelf(moveInfo);

        // check if it can connect with other puzzle pieces
        ConnectInfo[] connectInfoList = new ConnectInfo[4];

        for (int i=0; i<4; i++) {
            Direction direction = Directions.getDirection(i);
            if (getSide(direction).isConnected() || !getSide(direction).canConnect()) continue;
            int connectDirx = Directions.getDirectionX(direction), connectDiry = Directions.getDirectionY(direction);
            if (connectDirx == -moveInfo.getHdir() && connectDiry == -moveInfo.getVdir()) continue;
            connectInfoList[i] = getConnectInfo(0, 0, connectDirx, connectDiry);
            // System.out.println(connectInfoList[i]);
        }
        //Print.println(" PUZZLE PIECE MOVE FUNCTION: " + connectInfo, Print.PURPLE);
        for (ConnectInfo connectInfo : connectInfoList) {
            if (connectInfo == null) continue;
            if (connectInfo.canConnect()) 
                PuzzlePiece.connect(connectInfo);
        }
    }
    @Override
    public void update(double dt) {}

    public Color getHighlightedColor() {
        int highlightAmount = 60;
        return new Color(
            Math.min(255, color.getRed() + highlightAmount), 
            Math.min(255, color.getGreen() + highlightAmount), 
            Math.min(255, color.getBlue() + highlightAmount)
        );
    }
    public Color getColor() {
        return color;
    }


    @Override
    public void draw(Graphics2D g) {
        updateCurrentDrawPosToTarget(); // allow for smooth movement

        g.setColor(hasConnectedSide() ? getHighlightedColor() : color);
        g.fillRect((int) getCurrentDrawx(), (int) getCurrentDrawy(), gameBoard.tileSize, gameBoard.tileSize);

        for (int i=0; i<4; i++)
            getSide(Directions.getDirection(i)).draw(g, getCurrentDrawx(), getCurrentDrawy(), gameBoard.tileSize);
    }

    @Override
    public void updateInfoList(Graphics2D g, int drawcx, int drawbottomy) {
        ArrayList<String> drawList = new ArrayList<String>();

        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() + ")");
        drawList.add("current draw pos: (" + getCurrentDrawx() + "," + getCurrentDrawy() + ")");
        drawList.add("target draw pos: (" + getTargetDrawx() + "," + getTargetDrawy() + ")");
        drawList.add("index: " + getMoveIndex());
        drawList.add("must move: " + mustCheck());
        drawList.add("sides: ");
        for (int i=0; i<4; i++) {
            Direction direction = Directions.getDirection(i);
            drawList.add(direction + ": " + getSide(direction));
        }
        drawList.add("parents: ");
        for (GameObject parent: getParents()) {
            drawList.add(parent.toString());
        }

        setInfoList(g, drawcx, drawbottomy, drawList);
    }
}
