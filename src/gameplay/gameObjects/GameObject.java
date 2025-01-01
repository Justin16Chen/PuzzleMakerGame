package gameplay.gameObjects;

import java.util.ArrayList;
import java.util.HashMap;

import java.awt.Graphics2D;
import java.awt.Font;

import gameplay.GameBoard;
import utils.Direction;
import utils.drawing.InfoBox;
import utils.input.*;

public abstract class GameObject {
    
    // game object enum types
    public static enum ObjectType {
        WALL,
        PLAYER_PIECE,
        PUZZLE_PIECE
    }
    // get the string name of of the game object type
    public static String getObjectTypeName(ObjectType objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return "puzzlePiece";
            case PLAYER_PIECE: return "playerPiece";
            case WALL: return "wall";
            default: throw new IllegalArgumentException(objectType + " is not valid");
        }
    }
    // get the enum given the string name of the object type
    public static ObjectType getObjectType(String objectTypeName) {
        switch (objectTypeName) {
            case "puzzlePiece": return ObjectType.PUZZLE_PIECE;
            case "playerPiece": return ObjectType.PLAYER_PIECE;
            case "wall": return ObjectType.WALL;
            default: throw new IllegalArgumentException(objectTypeName + " is not valid");
        }
    }
    // get the identifierIndex of an object given the object type
    public static int getObjectIndex(ObjectType objectType) {
        for (int i=0; i<ObjectType.values().length; i++) {
            if (objectType == ObjectType.values()[i])
                return i + 1;
        }
        return -1;
    }
    
    // whether or not a game object is movable
    public static boolean getMovable(ObjectType objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return true;
            case PLAYER_PIECE: return true;
            case WALL: return false;
            default: return false;
        }
    }

    // rate that gameobjects move between cells at
    final public static double MOVE_RATE = 0.4;
    
    public static final double FINISH_MOVE_THRESHOLD = 0.5;
    
    protected GameBoard gameBoard;
    protected KeyInput keyInput;
    protected MouseInput mouseInput;
    private String name;
    private ObjectType objectType;
    private int objectIndex;
    private int boardx, boardy;
    private boolean movable;
    private boolean queuedMoveThisFrame;
    private boolean movedThisFrame;
    private int moveIndex = 0;
    private boolean mustCheck = false;
    private boolean[] checkedSides;
    private ArrayList<GameObject> parents;
    private InfoBox infoBox;
    private double currentDrawx, currentDrawy;
    private int targetDrawx, targetDrawy;

    public GameObject(GameBoard gameBoard, ObjectType objectType, int boardx, int boardy) {
        this.gameBoard = gameBoard;
        this.keyInput = gameBoard.getKeyInput();
        this.mouseInput = gameBoard.getMouseInput();
        this.objectType = objectType;
        this.objectIndex = GameObject.getObjectIndex(objectType);
        this.boardx = boardx;
        this.boardy = boardy;

        this.movedThisFrame = false;
        this.queuedMoveThisFrame = false;

        this.name = GameObject.getObjectTypeName(objectType);
        this.movable = GameObject.getMovable(objectType);
        this.checkedSides = new boolean[4];
        this.parents = new ArrayList<>();

        this.infoBox = InfoBox.createInfoBox();
        infoBox.hide();
        infoBox.setFont(new Font("Arial", Font.PLAIN, 10));
    }

    // setup function called after all game objects are created
    // meant to be overridden by subclasses if needed
    public void setup() {}

    // gameboard decides when to do this - only after it is properly setup
    public void updateVisualsAtStart() {
        updateTargetDrawPos();
        forceToTargetDrawPos();
    }

    public boolean equals(GameObject gameObject) {
        return getBoardX() == gameObject.getBoardX() && 
            getBoardY() == gameObject.getBoardY() && 
            getObjectType() == gameObject.getObjectType();
    }

    // getters
    public String getName() { return name; }
    public ObjectType getObjectType() { return objectType; }
    public int getObjectIndex() { return objectIndex; }
    public int getBoardX() { return boardx; }
    public int getBoardY() { return boardy; }
    public boolean isMovable() { return movable; }
    public boolean movedThisFrame() { return movedThisFrame; }
    public boolean queuedMovedThisFrame() { return queuedMoveThisFrame; }
    public void setQueuedMovedThisFrame(boolean bool) { queuedMoveThisFrame = bool; }
    public InfoBox getInfoBox() { return infoBox; }
    public int getCurrentDrawx() { return (int) currentDrawx; }
    public int getCurrentDrawy() { return (int) currentDrawy; }
    public int getTargetDrawx() { return targetDrawx; }
    public int getTargetDrawy() { return targetDrawy; }

    // whether or not the puzzle piece is the mover (initiates the motion)
    public boolean isMover() { return moveIndex == 0; }
    
    // get the index of the puzzle piece in relation to the mover
    public int getMoveIndex() { return moveIndex; }
    public void setMoveIndex(int moveIndex) { this.moveIndex = moveIndex; }

    // if the game object must move
    public boolean mustCheck() { return mustCheck; }
    public void setMustCheck(boolean mustCheck) { this.mustCheck = mustCheck; }

    // get if MoveLogic has checked the side yet
    public boolean[] getCheckedSides() { return checkedSides; }
    public boolean getCheckedSide(int i) { return checkedSides[i]; }
    public boolean getCheckedSide(Direction.Type direction) { return checkedSides[Direction.getMoveIndex(direction)]; }
    public void clearCheckedSides() {
        for (int i=0; i<4; i++)
            checkedSides[i] = false;
    }
    public void checkSide(int i) { checkedSides[i] = true; }
    public void checkSide(Direction.Type direction) {checkedSides[Direction.getMoveIndex(direction)] = true; }

    // add a parent
    public void addParent(GameObject gameObject) { parents.add(gameObject); }
    public void clearParents() { parents.clear(); }
    public ArrayList<GameObject> getParents() { return parents; }

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
    // other gameobjects can create more complex movement behaviors
    public void move(MoveInfo moveInfo, boolean isMover) {
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
            gameObject.move(moveInfo, false);
        }
        // move self
        moveBoardX(moveInfo.getHdir());
        moveBoardY(moveInfo.getVdir());

        // force previous movement tween to be done before starting the next one
        forceToTargetDrawPos();
        updateTargetDrawPos();
    }

    public HashMap<Direction.Type, GameObject> getAdjacentGameObjects() {
        HashMap<Direction.Type, GameObject> adjacentGameObjects = new HashMap<>();
        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            int dx = Direction.getDirectionX(direction), dy = Direction.getDirectionY(direction);
            int x = getBoardX() + dx, y = getBoardY() + dy;
            if (!gameBoard.inBounds(x, y) || gameBoard.getGameObject(x, y) == null) continue;
            adjacentGameObjects.put(direction, gameBoard.getGameObject(x, y));
        }
        return adjacentGameObjects;
    }
    public HashMap<Direction.Type, GameObject> getAdjacentGameObjects(ObjectType[] types) {
        HashMap<Direction.Type, GameObject> adjacentGameObjects = new HashMap<>();
        for (int i=0; i<4; i++) {
            Direction.Type direction = Direction.getDirection(i);
            int dx = Direction.getDirectionX(direction), dy = Direction.getDirectionY(direction);
            int x = getBoardX() + dx, y = getBoardY() + dy;
            if (!gameBoard.inBounds(x, y) || gameBoard.getGameObject(x, y) == null) continue;
            GameObject gameObject = gameBoard.getGameObject(x, y);
            boolean inTypes = false;
            for (ObjectType type : types)
                if (gameObject.getObjectType() == type) {
                    inTypes = true;
                    break;
                }
            adjacentGameObjects.put(direction, inTypes ? gameObject : null);
        }
        return adjacentGameObjects;
    }

    public abstract void update(double dt);
    public abstract void draw(Graphics2D g);

    private void updateTargetDrawPos() {
        this.targetDrawx = gameBoard.findGameObjectDrawX(this);
        this.targetDrawy = gameBoard.findGameObjectDrawY(this);
    }
    private void forceToTargetDrawPos() {
        currentDrawx = targetDrawx;
        currentDrawy = targetDrawy;
    }
    public void updateCurrentDrawPosToTarget() {
        int xDist = (int) (targetDrawx - currentDrawx);
        int yDist = (int) (targetDrawy - currentDrawy);
        if (Math.abs(xDist) < FINISH_MOVE_THRESHOLD && Math.abs(yDist) < FINISH_MOVE_THRESHOLD)
            forceToTargetDrawPos();
        else
            currentDrawx += xDist * MOVE_RATE;
            currentDrawy += yDist * MOVE_RATE;

    }

    public void updateInfoList(Graphics2D g, int drawcx, int drawbottomy) {
        ArrayList<String> drawList = new ArrayList<String>();
        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() + ")");
        setInfoList(g, drawcx, drawbottomy, drawList);
    }
    public void setInfoList(Graphics2D g, int drawcx, int drawbottomy, ArrayList<String> drawList) {
        infoBox.clearDrawList();
        infoBox.setDrawList(drawList);
        infoBox.setPos(drawcx, drawbottomy);
        infoBox.setOffsets(0.5, 1);
    }

    @Override
    public String toString() {
        return "GameObject(" + name + "|(" + boardx + "," + boardy + ")|moveIdx:" + moveIndex + ")";
    }
}
