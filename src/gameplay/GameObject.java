package gameplay;

public abstract class GameObject {

    public static enum OBJECT_TYPE {
        EMPTY,
        PUZZLE_PIECE,
        PLAYER_PIECE
    }
    public static String getObjectTypeName(OBJECT_TYPE objectType) {
        switch (objectType) {
            case PUZZLE_PIECE: return "puzzle piece";
            case PLAYER_PIECE: return "player piece";
            case EMPTY: return "empty";
            default: return "empty";
        }
    }

    private GameBoard gameBoard;
    private String name;
    private OBJECT_TYPE objectType;
    private int boardx, boardy;
    
    public GameObject(GameBoard gameBoard, OBJECT_TYPE objectType, int boardx, int boardy) {
        this.gameBoard = gameBoard;
        this.name = GameObject.getObjectTypeName(objectType);
        this.objectType = objectType;
        this.boardx = boardx;
        this.boardy = boardy;
    }

    public String getName() { return name; }
    public OBJECT_TYPE getObjectType() { return objectType; }
    public int getBoardX() { return boardx; }
    public int getBoardY() { return boardy; }
    public void setBoardX(int x) { boardx = x; }
    public void setBoardY(int y) { boardx = y; }
    public void moveBoardX(int x) { boardx += x; }
    public void moveBoardY(int y) { boardy += y; }

    public abstract void update();
}
