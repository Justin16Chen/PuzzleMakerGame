package gameplay.gameObjects;

import gameplay.GameBoard;
import input.*;

public abstract class GameObject {

    // game object enum types
    public static enum OBJECT_TYPE {
        PUZZLE_PIECE,
        PLAYER_PIECE,
        WALL
    }
    // get the string name of of the game object type
    public static String getObjectTypeName(OBJECT_TYPE objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return "puzzle piece";
            case PLAYER_PIECE: return "player piece";
            case WALL: return "wall";
            default: return "empty";
        }
    }
    
    // whether or not a game object is movable
    public static boolean getMovable(OBJECT_TYPE objectType) {
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
    private OBJECT_TYPE objectType;
    private int boardx, boardy;
    private boolean movable;
    

    public GameObject(GameBoard gameBoard, OBJECT_TYPE objectType, int boardx, int boardy) {
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
    public OBJECT_TYPE getObjectType() { return objectType; }
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
    public boolean canMove(int hdir, int vdir) {

        // find target position
        int targetx = boardx + hdir;
        int targety = boardy + vdir;

        // cannot move it target is out of bounds
        if (!gameBoard.inBounds(targetx, targety)) { return false; }

        // find out the type of game object at target location
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);

        // empty cell
        if (gameObject == null) { return true; }

        //System.out.println("parent type: " + objectType + " | target x: " + targetx + " | target y: " + targety + " | target type: " + gameObject.getObjectType());
        if (gameObject.getObjectType() == null) { return true; }

        // have to be a movable game object that can also be pushed
        return gameObject.isMovable() && gameObject.canMove(hdir, vdir);      
    }

    // move the game object and any subsequent objects
    public void move(int hdir, int vdir) {

        // find target position
        int targetx = boardx + hdir;
        int targety = boardy + vdir;

        // find out the type of game object at target location
        GameObject gameObject = gameBoard.getGameObject(targetx, targety);

        // move game object at cell
        if (gameObject != null) {
            gameObject.move(hdir, vdir);
        }
        // move self
        moveBoardX(hdir);
        moveBoardY(vdir);
    }

    public abstract void update(double dt);

    @Override
    public String toString() {
        return 
        "Game Object:" + 
        "\n\tname: " + name + 
        "\n\tx: " + boardx + 
        "\n\ty: " + boardy;
    }
}
