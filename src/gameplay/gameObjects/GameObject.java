package gameplay.gameObjects;

import java.util.ArrayList;

import java.awt.Graphics2D;
import java.awt.Font;

import gameplay.GameBoard;
import utils.drawing.InfoBox;
import utils.input.*;

public abstract class GameObject {
    
    // game object enum types
    public static enum ObjectType {
        PUZZLE_PIECE,
        PLAYER_PIECE,
        WALL,
        OUT_OF_BOUNDS,
        EMPTY
    }
    // get the string name of of the game object type
    public static String getObjectTypeName(ObjectType objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return "puzzlePiece";
            case PLAYER_PIECE: return "playerPiece";
            case WALL: return "wall";
            case OUT_OF_BOUNDS: return "outOfBounds";
            case EMPTY: return "empty";
            default: return "empty";
        }
    }
    // get the enum given the string name of the object type
    public static ObjectType getObjectType(String objectTypeName) {
        switch (objectTypeName) {
            case "puzzlePiece": return ObjectType.PUZZLE_PIECE;
            case "playerPiece": return ObjectType.PLAYER_PIECE;
            case "wall": return ObjectType.WALL;
            case "outOfBounds": return ObjectType.OUT_OF_BOUNDS;
            case "empty": return ObjectType.EMPTY;
            default: return ObjectType.EMPTY;
        }
    }
    
    // whether or not a game object is movable
    public static boolean getMovable(ObjectType objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return true;
            case PLAYER_PIECE: return true;
            case WALL: return false;
            case OUT_OF_BOUNDS: return false;
            case EMPTY: return true;
            default: return false;
        }
    }

    protected GameBoard gameBoard;
    protected KeyInput keyInput;
    protected MouseInput mouseInput;
    private String name;
    private ObjectType objectType;
    private int boardx, boardy;
    private boolean movable;
    private boolean queuedMoveThisFrame;
    private boolean movedThisFrame;
    private InfoBox infoBox;
    

    public GameObject(GameBoard gameBoard, ObjectType objectType, int boardx, int boardy) {
        this.gameBoard = gameBoard;
        this.keyInput = gameBoard.getKeyInput();
        this.mouseInput = gameBoard.getMouseInput();
        this.objectType = objectType;
        this.boardx = boardx;
        this.boardy = boardy;

        this.movedThisFrame = false;
        this.queuedMoveThisFrame = false;

        this.name = GameObject.getObjectTypeName(objectType);
        this.movable = GameObject.getMovable(objectType);

        this.infoBox = InfoBox.createInfoBox();
        infoBox.hide();
        infoBox.setFont(new Font("Arial", Font.PLAIN, 10));
    }
    public GameObject(ObjectType objectType) {
        this.objectType = objectType;

        this.name = GameObject.getObjectTypeName(objectType);
        this.movable = GameObject.getMovable(objectType);
    }

    public boolean equals(GameObject gameObject) {
        return getBoardX() == gameObject.getBoardX() && 
            getBoardY() == gameObject.getBoardY() && 
            getObjectType() == gameObject.getObjectType();
    }

    // getters
    public String getName() { return name; }
    public ObjectType getObjectType() { return objectType; }
    public int getBoardX() { return boardx; }
    public int getBoardY() { return boardy; }
    public boolean isMovable() { return movable; }
    public boolean movedThisFrame() { return movedThisFrame; }
    public boolean queuedMovedThisFrame() { return queuedMoveThisFrame; }
    public void setQueuedMovedThisFrame(boolean bool) { queuedMoveThisFrame = bool; }
    public InfoBox getInfoBox() { return infoBox; }

    // can only move once a frame - this is function to reset to allow new movement
    public void resetMovedThisFrame() {
        movedThisFrame = false;
    }
    public void resetQueuedMovedThisFrame() {
        queuedMoveThisFrame = false;
    }
    // move the game object and also move any subsequent game objects
    public void moveBoardX(int x) {
        boardx += x;

    }
    public void moveBoardY(int y) {
        boardy += y; 
    }

    // check if the game object and any subsequent game objects can move in a certain direction
    public MoveInfo getMoveInfo(int hdir, int vdir) {
        return getMoveInfo(0, 0, hdir, vdir);
    }
    public MoveInfo getMoveInfo(int xOff, int yOff, int hdir, int vdir) {

        // cannot move immovable game objects
        if (!isMovable()) return MoveInfo.makeInvalidMove();

        // find target position
        int targetx = getBoardX() + hdir;
        int targety = getBoardY() + vdir;

        // out of bounds
        if (!gameBoard.inBounds(targetx, targety)) return MoveInfo.makeInvalidMove();

        // find out the type of game object at target location
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);

        // empty cell
        if (gameObject == null) return MoveInfo.makeValidMove(hdir, vdir);

        //System.out.println("Moving " + objectType + ": offset: " + xoff + ", " + yoff + " | target: " + targetx + ", " + targety + " | target type: " + gameObject.getObjectType());

        // get move info for game object this one is trying to move into
        MoveInfo moveInfo = gameObject.getMoveInfo(hdir, vdir);

        // if that game object can move, this one can move
        if (moveInfo.canMove()) return MoveInfo.makeValidMove(hdir, vdir);
        return MoveInfo.makeInvalidMove();
    }

    // meant to be overridden by any moving objects
    public void move(MoveInfo moveInfo) {
        queuedMoveThisFrame = true;
        moveSelf(moveInfo);
    }

    // move the game object and any subsequent objects
    public void moveSelf(MoveInfo moveInfo) {
        //Print.println("GAME OBJECT MOVE FUNCTION", Print.RED);
        //System.out.println("for: " + this);

        if (movedThisFrame) {
            System.out.println(getName() + " already moved, overriding movement");
            return;
        }

        //System.out.println(Print.RED + "MOVING GAME OBJECT" + Print.RESET);
        movedThisFrame = true;

        // get starting game object to move
        int targetx = getBoardX() + moveInfo.getHdir();
        int targety = getBoardY() + moveInfo.getVdir();
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);
        
        //System.out.println(this + " moving by (" + moveInfo.getHdir() + ", " + moveInfo.getVdir() + ") to (" + targetx + ", " + targety + ")");
        //System.out.println(moveInfo.toString());

        // move game object current object is moving into
        if (gameObject != null) {
            gameObject.move(moveInfo);
        }
        // move self
        moveBoardX(moveInfo.getHdir());
        moveBoardY(moveInfo.getVdir());
    }

    public abstract void update(double dt);
    public abstract void draw(Graphics2D g, int drawx, int drawy);

    public void updateInfoList(Graphics2D g, int drawcx, int drawbottomy) {
        ArrayList<String> drawList = new ArrayList<String>();
        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() + ")");
        updateInfoList(g, drawcx, drawbottomy, drawList);
    }
    public void updateInfoList(Graphics2D g, int drawcx, int drawbottomy, ArrayList<String> drawList) {
        infoBox.clearDrawList();
        infoBox.setDrawList(drawList);
        infoBox.setPos(drawcx, drawbottomy);
        infoBox.setOffsets(0.5, 1);
    }

    @Override
    public String toString() {
        return "GameObject(name: " + name + " | x: " + boardx + " | y: " + boardy + ")";
    }
}
