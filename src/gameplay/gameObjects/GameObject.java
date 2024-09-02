package gameplay.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import gameplay.GameBoard;
import input.*;

public abstract class GameObject {

    // game object enum types
    public static enum ObjectType {
        PUZZLE_PIECE,
        PLAYER_PIECE,
        WALL
    }
    // get the string name of of the game object type
    public static String getObjectTypeName(ObjectType objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return "puzzle piece";
            case PLAYER_PIECE: return "player piece";
            case WALL: return "wall";
            default: return "empty";
        }
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

    protected GameBoard gameBoard;
    protected KeyInput keyInput;
    protected MouseInput mouseInput;
    private String name;
    private ObjectType objectType;
    private int boardx, boardy;
    private boolean movable;
    

    public GameObject(GameBoard gameBoard, ObjectType objectType, int boardx, int boardy) {
        this.gameBoard = gameBoard;
        this.keyInput = gameBoard.getKeyInput();
        this.mouseInput = gameBoard.getMouseInput();
        this.objectType = objectType;
        this.boardx = boardx;
        this.boardy = boardy;

        this.name = GameObject.getObjectTypeName(objectType);
        this.movable = GameObject.getMovable(objectType);
    }

    // getters
    public String getName() { return name; }
    public ObjectType getObjectType() { return objectType; }
    public int getBoardX() { return boardx; }
    public int getBoardY() { return boardy; }
    public boolean isMovable() { return movable; }

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
    public MoveInfo getMoveInfo(int xoff, int yoff, int hdir, int vdir) {

        // cannot move immovable game objects
        if (!isMovable()) {
            return MoveInfo.makeInvalidMove();
        }

        // find target position
        int targetx = getBoardX() + xoff + hdir;
        int targety = getBoardY() + yoff + vdir;

        // out of bounds
        if (!gameBoard.inBounds(targetx, targety)) { 
            return MoveInfo.makeInvalidMove();
        }

        // find out the type of game object at target location
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);

        // empty cell
        if (gameObject == null) {
             return MoveInfo.makeValidMove(hdir, vdir);
            }

        //System.out.println("Moving " + objectType + ": offset: " + xoff + ", " + yoff + " | target: " + targetx + ", " + targety + " | target type: " + gameObject.getObjectType());

        // get move info for game object this one is trying to move into
        MoveInfo moveInfo = gameObject.getMoveInfo(hdir, vdir);

        // if that game object can move, this one can move
        if (moveInfo.canMove()) {
            return MoveInfo.makeValidMove(hdir, vdir);
        }
        return MoveInfo.makeInvalidMove();
    }

    // move the game object and any subsequent objects
    public void move(MoveInfo moveInfo) {

        // get starting game object to move
        int targetx = getBoardX() + moveInfo.getHdir();
        int targety = getBoardY() + moveInfo.getVdir();
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);
            
        //System.out.println("Parent: " + gameObject.toString() + ", ");
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

    public void drawName(Graphics2D g, int x, int y) {
        g.setColor(Color.WHITE);
        g.drawString(getName(), x, y);
    }

    public void drawPosition(Graphics2D g, int drawx, int drawy) {
        g.setColor(Color.WHITE);
        g.drawString(getBoardX() + ", " + getBoardY(), drawx, drawy);
    }

    @Override
    public String toString() {
        return "GameObject(name: " + name + " | x: " + boardx + " | y: " + boardy + ")";
    }
}
