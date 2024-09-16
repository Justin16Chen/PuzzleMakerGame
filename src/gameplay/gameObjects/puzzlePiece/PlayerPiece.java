package gameplay.gameObjects.puzzlePiece;

import gameplay.GameBoard;
import gameplay.gameObjects.*;

public class PlayerPiece extends PuzzlePiece {

    public PlayerPiece(GameBoard gameBoard, int boardx, int boardy, String sideData) {
        super(gameBoard, GameObject.ObjectType.PLAYER_PIECE, boardx, boardy, sideData);
    }

    // update the playerPiece
    @Override
    public void update(double dt) {

        if (keyInput.keyClicked("Q")) {
            getAdjacentConnectedPieces(null, null);
        }

        // get player input
        int hdir = keyInput.keyClickedInt("D") - keyInput.keyClickedInt("A");
        int vdir = keyInput.keyClickedInt("S") - keyInput.keyClickedInt("W");

        // make sure there is movement
        if (hdir != 0 || vdir != 0) {

            // first check if movement is valid
            MoveInfo moveInfo = getAllMoveInfo(hdir, vdir);

            if (moveInfo.canMove()) {
                
                // TODO: what if you push a puzzle piece into another puzzle piece that can connect with it?
                // does not account for that yet
                moveAllAttached(moveInfo);
            }
        }
    }
}
