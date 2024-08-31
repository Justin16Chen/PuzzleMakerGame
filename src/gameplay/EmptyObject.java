package gameplay;

public class EmptyObject extends GameObject {

    public EmptyObject(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.OBJECT_TYPE.EMPTY, boardx, boardy);
    }

    @Override
    public void update() {}
    
}
