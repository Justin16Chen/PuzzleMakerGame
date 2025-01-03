package gameplay.gameObjects;

import java.util.ArrayList;
import java.util.HashMap;

import java.awt.Font;

import gameplay.GameBoard;
import utils.direction.Direction;
import utils.direction.Directions;
import utils.drawing.InfoBox;
import utils.drawing.SimpleSprite;
import utils.drawing.Sprites;
import utils.input.*;
import utils.tween.EaseType;
import utils.tween.Tween;

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
    final public static double MOVE_RATE = 0.2;
    
    protected GameBoard gameBoard;
    protected KeyInput keyInput;
    protected MouseInput mouseInput;
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
    protected SimpleSprite sprite;
    protected InfoBox infoBox;
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

        this.movable = GameObject.getMovable(objectType);
        this.checkedSides = new boolean[4];
        this.parents = new ArrayList<>();
    }

    // setup function called after all game objects are created
    // meant to be overridden by subclasses if needed
    public void setup() {
    }

    // gameboard decides when to do this - only after it is properly setup
    public void updateVisualsAtStart() {
        this.infoBox = new InfoBox(0, 0);
        infoBox.setVisible(false);
        infoBox.setFont(new Font("Arial", Font.PLAIN, 10));

        forceToTargetDrawPos();
    }

    public boolean equals(GameObject gameObject) {
        return getBoardX() == gameObject.getBoardX() && 
            getBoardY() == gameObject.getBoardY() && 
            getObjectType() == gameObject.getObjectType();
    }

    // getters
    public ObjectType getObjectType() { return objectType; }
    public int getObjectIndex() { return objectIndex; }
    public int getBoardX() { return boardx; }
    public int getBoardY() { return boardy; }
    public boolean isMovable() { return movable; }
    public boolean movedThisFrame() { return movedThisFrame; }
    public boolean queuedMovedThisFrame() { return queuedMoveThisFrame; }
    public void setQueuedMovedThisFrame(boolean bool) { queuedMoveThisFrame = bool; }
    public SimpleSprite getSprite() { return sprite; }
    public InfoBox getInfoBox() { return infoBox; }

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
    public boolean getCheckedSide(Direction direction) { return checkedSides[Directions.getMoveIndex(direction)]; }
    public void clearCheckedSides() {
        for (int i=0; i<4; i++)
            checkedSides[i] = false;
    }
    public void checkSide(int i) { checkedSides[i] = true; }
    public void checkSide(Direction direction) {checkedSides[Directions.getMoveIndex(direction)] = true; }

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

        if (movedThisFrame) 
            return;

        //System.out.println(Print.RED + "MOVING GAME OBJECT" + Print.RESET);
        movedThisFrame = true;

        // get starting game object to move
        int targetx = getBoardX() + moveInfo.getHdir();
        int targety = getBoardY() + moveInfo.getVdir();
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);

        // move game object current object is moving into
        if (gameObject != null) {
            gameObject.move(moveInfo, false);
        }
        // move self
        moveBoardX(moveInfo.getHdir());
        moveBoardY(moveInfo.getVdir());

        // tween sprite position
        Tween.createTween("move " + objectType + " x", sprite, "x", sprite.getX(), gameBoard.findGameObjectDrawX(this), MOVE_RATE).setEaseType(EaseType.EASE_OUT);
        Tween.createTween("move " + objectType + " y", sprite, "y", sprite.getY(), gameBoard.findGameObjectDrawY(this), MOVE_RATE).setEaseType(EaseType.EASE_OUT);

    }

    public HashMap<Direction, GameObject> getAdjacentGameObjects() {
        HashMap<Direction, GameObject> adjacentGameObjects = new HashMap<>();
        for (Direction direction : Directions.getAllDirections()) {
            int dx = Directions.getDirectionX(direction), dy = Directions.getDirectionY(direction);
            int x = getBoardX() + dx, y = getBoardY() + dy;
            if (!gameBoard.inBounds(x, y) || gameBoard.getGameObject(x, y) == null) continue;
            adjacentGameObjects.put(direction, gameBoard.getGameObject(x, y));
        }
        return adjacentGameObjects;
    }
    public HashMap<Direction, GameObject> getAdjacentGameObjects(ObjectType[] types) {
        HashMap<Direction, GameObject> adjacentGameObjects = new HashMap<>();
        for (Direction direction : Directions.getAllDirections()) {
            int dx = Directions.getDirectionX(direction), dy = Directions.getDirectionY(direction);
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

    public void updateDrawList() {
        ArrayList<String> drawList = new ArrayList<>();
        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() +")");
        infoBox.setDrawList(drawList);
    }

    protected void forceToTargetDrawPos() {
        sprite.setX(gameBoard.findGameObjectDrawX(this));
        sprite.setY(gameBoard.findGameObjectDrawY(this));
    }

    public void deleteSprites() {
        Sprites.deleteSprites(new String[]{sprite.getName(), InfoBox.NAME});
    }

    @Override
    public String toString() {
        return "GameObject((" + boardx + "," + boardy + ")|moveIdx:" + moveIndex + ")";
    }
}
