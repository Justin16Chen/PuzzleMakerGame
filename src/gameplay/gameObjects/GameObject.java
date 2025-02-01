package gameplay.gameObjects;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import java.awt.Font;

import gameplay.GameBoard;
import utils.direction.Direction;
import utils.direction.Directions;
import utils.drawing.InfoBox;
import utils.drawing.sprites.Sprite;
import utils.drawing.sprites.Sprites;
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

    // rate that gameobjects move between cells at
    final public static double MOVE_RATE = 0.2;
    
    private ObjectType objectType;
    private int boardX, boardY, cellWidth, cellHeight;
    private boolean movable;
    protected boolean movedThisFrame;
    protected Sprite sprite;
    protected InfoBox infoBox;
    public GameObject(ObjectType objectType, int boardX, int boardY, int width, int height) {
        this.objectType = objectType;
        this.boardX = boardX;
        this.boardY = boardY;
        this.cellWidth = width;
        this.cellHeight = height;

        this.movedThisFrame = false;

        this.movable = GameObjectData.isMovable(objectType);
    }

    // setup function called after all game objects are created
    // supposed to create sprites and tweens here - NOT in constructor
    public abstract void setup(int x, int y, int width, int height);

    public JSONObject toJSONObject() {
        JSONObject jsonGameObject = new JSONObject();
        jsonGameObject.put("name", GameObjectData.objectTypeToName(objectType));
        jsonGameObject.put("x", boardX);
        jsonGameObject.put("y", boardY);
        return jsonGameObject;
    }

    // gameboard decides when to do this - only after it is properly setup
    public void updateVisualsAtStart(GameBoard gameBoard) {
        if (sprite == null)
            setup(0, 0, 1, 1);
        sprite.addTag("main");
        this.infoBox = new InfoBox(0, 0);
        infoBox.setVisible(false);
        infoBox.setFont(new Font("Arial", Font.PLAIN, 10));

        sprite.setX(gameBoard.getDrawX(boardX));
        sprite.setY(gameBoard.getDrawY(boardY));
        sprite.setWidth(cellWidth * gameBoard.getTileSize());
        sprite.setHeight(cellHeight * gameBoard.getTileSize());
    }

    public void updateVisualsToBoard(GameBoard board) {
        sprite.setX(board.getDrawX(boardX));
        sprite.setY(board.getDrawY(boardY));
        sprite.setWidth(cellWidth * board.getTileSize());
        sprite.setHeight(cellHeight * board.getTileSize());
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

    // can specify if a game object must move with another game object
    // used in MoveLogic
    public boolean mustMoveWith(GameObject gameObject) {
        return false;
    }

    // getters
    public ObjectType getObjectType() { return objectType; }
    public int getBoardX() { return boardX; }
    public int getBoardY() { return boardY; }
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
        boardX += x;

    }
    public void moveBoardY(int y) {
        boardY += y; 
    }

    // subclasses can override
    public void update(GameBoard board) {}

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
    // can be overridden to create more complex behaviors
    public MoveInfo getMoveInfo(GameBoard gameBoard, ArrayList<GameObject> callerList, int hdir, int vdir) {

        // cannot move immovable game objects
        if (!isMovable()) return MoveInfo.makeInvalidMove();
        
        int[][] offsets = getOffsetsToCheckMovement(hdir, vdir);

        MoveInfo validMoveInfo = MoveInfo.makeInvalidMove();
        for (int i=0; i<offsets.length; i++) {
            validMoveInfo = getMoveInfo(gameBoard, callerList, hdir, vdir, offsets[i][0], offsets[i][1]);
            if (!validMoveInfo.canMove())
                return MoveInfo.makeInvalidMove();
        }  
        return validMoveInfo;
    }
    private MoveInfo getMoveInfo(GameBoard gameBoard, ArrayList<GameObject> callerList, int hdir, int vdir, int xOff, int yOff) {

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
        MoveInfo moveInfo = gameObject.getMoveInfo(gameBoard, callerList, hdir, vdir);

        // if that game object can move, this one can move
        if (moveInfo.canMove())
            return MoveInfo.makeValidMove(hdir, vdir);
        return MoveInfo.makeInvalidMove();
        
    }

    // handles back end movement management
    public void move(GameBoard gameBoard, MoveInfo moveInfo, boolean isMover) {
        //System.out.println(this + " moved this frame: " + movedThisFrame);
        if (movedThisFrame)
            return;
        movedThisFrame = true;
        
        if (isMover) {
            gameBoard.updateGameObjectPositions();
            for (GameObject gameObject : gameBoard.getGameObjects())
                gameObject.performBeforeMovement(gameBoard, moveInfo);
            gameBoard.updateGameObjectPositions();
            //System.out.println("after pre movement check");
            //gameBoard.printGameObjects();
        }

        customMove(gameBoard, moveInfo);

        if (isMover) {
            gameBoard.updateGameObjectPositions();
            for (GameObject gameObject : gameBoard.getGameObjects())
                gameObject.performAfterMovement(gameBoard, moveInfo);
            gameBoard.updateGameObjectPositions();
            //System.out.println("after post movement check");
           // gameBoard.printGameObjects();
        }
    }

    // meant to be overridden by any moving objects
    // other gameobjects can create more complex movement behaviors
    protected void customMove(GameBoard gameBoard, MoveInfo moveInfo) {
        moveSelf(gameBoard, moveInfo);
    }

    // move the game object and any subsequent objects
    protected void moveSelf(GameBoard gameBoard, MoveInfo moveInfo) {
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
                gameObject.move(gameBoard, moveInfo, false);
        }
        
        // move self
        moveBoardX(moveInfo.getHdir());
        moveBoardY(moveInfo.getVdir());

        // tween sprite position
        Tween.createTween("move " + objectType + " x", sprite, "x", sprite.getX(), gameBoard.getDrawX(boardX), MOVE_RATE).setEaseType(new EaseType(Ease.EASE_OUT));
        Tween.createTween("move " + objectType + " y", sprite, "y", sprite.getY(), gameBoard.getDrawY(boardY), MOVE_RATE).setEaseType(new EaseType(Ease.EASE_OUT));
    }

    // allows subclasses to make any checks right before everything has moved
    protected void performBeforeMovement(GameBoard gameBoard, MoveInfo moveInfo) {}
    // allows subclasses to make any checks after everything is done moving
    protected void performAfterMovement(GameBoard gameBoard, MoveInfo moveInfo) {}


    public void updateDrawList() {
        ArrayList<String> drawList = new ArrayList<>();
        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() +")");
        infoBox.setDrawList(drawList);
    }

    public void deleteSprites() {
        Sprites.deleteSprites(new String[]{sprite.getName(), InfoBox.NAME});
    }

    @Override
    public String toString() {
        return objectType + "((" + boardX + "," + boardY + ")";
    }
}
