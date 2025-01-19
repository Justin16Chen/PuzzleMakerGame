package gameplay.gameObjects;

import java.util.ArrayList;
import java.util.HashMap;

import java.awt.Font;

import gameplay.GameBoard;
import utils.direction.Direction;
import utils.direction.Directions;
import utils.drawing.InfoBox;
import utils.drawing.sprites.Sprite;
import utils.drawing.sprites.Sprites;
import utils.input.*;
import utils.tween.Ease;
import utils.tween.EaseType;
import utils.tween.Tween;

public abstract class GameObject {
    
    // game object enum types
    public static enum ObjectType {
        WALL,
        BOX,
        PLAYER_PIECE,
        PUZZLE_PIECE
    }
    // get the string name of of the game object type
    public static String getObjectTypeName(ObjectType objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return "puzzlePiece";
            case PLAYER_PIECE: return "playerPiece";
            case WALL: return "wall";
            case BOX: return "box";
            default: throw new IllegalArgumentException(objectType + " is not valid");
        }
    }
    // get the enum given the string name of the object type
    public static ObjectType getObjectType(String objectTypeName) {
        switch (objectTypeName) {
            case "puzzlePiece": return ObjectType.PUZZLE_PIECE;
            case "playerPiece": return ObjectType.PLAYER_PIECE;
            case "wall": return ObjectType.WALL;
            case "box": return ObjectType.BOX;
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
    
    // TODO: refactor this to be in json file
    // whether or not a game object is movable
    public static boolean getMovable(ObjectType objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return true;
            case PLAYER_PIECE: return true;
            case WALL: return false;
            case BOX: return true;
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
    private int boardx, boardy, cellWidth, cellHeight;
    private boolean movable;
    protected boolean movedThisFrame;
    protected Sprite sprite;
    protected InfoBox infoBox;
    public GameObject(GameBoard gameBoard, ObjectType objectType, int boardx, int boardy, int width, int height) {
        this.gameBoard = gameBoard;
        this.keyInput = gameBoard.getKeyInput();
        this.mouseInput = gameBoard.getMouseInput();
        this.objectType = objectType;
        this.objectIndex = GameObject.getObjectIndex(objectType);
        this.boardx = boardx;
        this.boardy = boardy;
        this.cellWidth = width;
        this.cellHeight = height;

        this.movedThisFrame = false;

        this.movable = GameObject.getMovable(objectType);
    }

    // setup function called after all game objects are created
    // supposed to create sprites and tweens here - NOT in constructor
    public abstract void setup();

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

    // can specify move relation ships
    // subclasses can override
    // specifies if gameObject should be considered when moving this
    // used in MoveLogic
    public boolean shouldConsider(GameObject gameObject) {
        return false;
    }

    // getters
    public ObjectType getObjectType() { return objectType; }
    public int getObjectIndex() { return objectIndex; }
    public int getBoardX() { return boardx; }
    public int getBoardY() { return boardy; }
    public int getCellWidth() { return cellWidth; }
    public int getCellHeight() { return cellHeight; }
    public boolean isMovable() { return movable; }
    public boolean movedThisFrame() { return movedThisFrame; }
    public Sprite getSprite() { return sprite; }
    public InfoBox getInfoBox() { return infoBox; }

    // can only move once a frame - this is function to reset to allow new movement
    public void resetMovedThisFrame() {
        movedThisFrame = false;
    }
    // move the game object and also move any subsequent game objects
    public void moveBoardX(int x) {
        boardx += x;

    }
    public void moveBoardY(int y) {
        boardy += y; 
    }

    private int[][] getOffsetsToCheckMovement(int hdir, int vdir) {
        boolean horizontal = vdir == 0;
        int[][] offsets = new int[horizontal ? cellHeight : cellWidth][2];
        for (int i=0; i<offsets.length; i++) 
            if (horizontal) {
                offsets[i][1] = i;
                offsets[i][0] = hdir > 0 ? cellWidth - 1 : 0;
            }
            else {
                offsets[i][0] = i;
                offsets[i][1] = vdir > 0 ? cellHeight - 1: 0;
            }
        return offsets;
    }

    // check if the game object and any subsequent game objects can move in a certain direction
    public MoveInfo getMoveInfo(int hdir, int vdir) {

        // cannot move immovable game objects
        if (!isMovable()) return MoveInfo.makeInvalidMove();
        
        int[][] offsets = getOffsetsToCheckMovement(hdir, vdir);

        MoveInfo validMoveInfo = MoveInfo.makeInvalidMove();
        for (int i=0; i<offsets.length; i++) {
            validMoveInfo = getMoveInfo(hdir, vdir, offsets[i][0], offsets[i][1]);
            if (!validMoveInfo.canMove())
                return MoveInfo.makeInvalidMove();
        }  
        return validMoveInfo;
    }
    private MoveInfo getMoveInfo(int hdir, int vdir, int xOff, int yOff) {

        // cannot move immovable game objects
        if (!isMovable()) return MoveInfo.makeInvalidMove();
        
        // find target position
        int targetx = getBoardX() + xOff + hdir;
        int targety = getBoardY() + yOff + vdir;

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
        if (moveInfo.canMove())
            return MoveInfo.makeValidMove(hdir, vdir);
        return MoveInfo.makeInvalidMove();
        
    }

    // handles back end movement management
    public void move(MoveInfo moveInfo, boolean isMover) {
        if (movedThisFrame)
            return;
        movedThisFrame = true;
        
        customMove(moveInfo);

        if (isMover) {
            gameBoard.updateGameObjectPositions();
            for (GameObject gameObject : gameBoard.getGameObjects())
                gameObject.performAfterMovement(moveInfo);
        }
    }

    // meant to be overridden by any moving objects
    // other gameobjects can create more complex movement behaviors
    protected void customMove(MoveInfo moveInfo) {
        moveSelf(moveInfo);
    }

    // move the game object and any subsequent objects
    protected void moveSelf(MoveInfo moveInfo) {
        //Print.println("GAME OBJECT MOVE FUNCTION", Print.RED);
        //System.out.println("for: " + this);

        //System.out.println(Print.RED + "MOVING GAME OBJECT" + Print.RESET);
        movedThisFrame = true;

        int[][] offsets = getOffsetsToCheckMovement(moveInfo.getHdir(), moveInfo.getVdir());

        for (int[] offset : offsets) {
            // get starting game object to move
            int targetx = getBoardX() + offset[0] + moveInfo.getHdir();
            int targety = getBoardY() + offset[1] + moveInfo.getVdir();
            GameObject gameObject = gameBoard.getGameObject(targetx, targety);
    
            // move game object current object is moving into
            if (gameObject != null) 
                gameObject.move(moveInfo, false);
        }
        
        // move self
        moveBoardX(moveInfo.getHdir());
        moveBoardY(moveInfo.getVdir());

        // tween sprite position
        Tween.createTween("move " + objectType + " x", sprite, "x", sprite.getX(), gameBoard.findGameObjectDrawX(this), MOVE_RATE).setEaseType(new EaseType(Ease.EASE_OUT));
        Tween.createTween("move " + objectType + " y", sprite, "y", sprite.getY(), gameBoard.findGameObjectDrawY(this), MOVE_RATE).setEaseType(new EaseType(Ease.EASE_OUT));
    }

    // allows subclasses to make any checks after everything is done moving
    protected void performAfterMovement(MoveInfo moveInfo) {
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
        return objectType + "((" + boardx + "," + boardy + ")";
    }
}
