package gameplay.gameObjects;

import gameplay.GameBoard;

public class PlayerPiece extends PuzzlePiece {

    public PlayerPiece(GameBoard gameBoard, int boardx, int boardy) {
        super(gameBoard, GameObject.OBJECT_TYPE.PLAYER_PIECE, boardx, boardy);
    }

    // update the player piece
    @Override
    public void update(double dt) {

        // get player input
        int hdir = keyInput.keyClickedInt("D") - keyInput.keyClickedInt("A");
        int vdir = keyInput.keyClickedInt("S") - keyInput.keyClickedInt("W");

        // make sure there is movement
        if (hdir != 0 || vdir != 0) {
            int targetx = getBoardX() + hdir;
            int targety = getBoardY() + vdir;
            // make sure movement is in bounds and is valid
            if (gameBoard.inBounds(targetx, targety) && canMove(hdir, vdir)) {
                move(hdir, vdir);
            }
        }
    }
}
