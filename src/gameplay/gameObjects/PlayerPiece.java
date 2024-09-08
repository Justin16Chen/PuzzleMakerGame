package gameplay.gameObjects;

import java.util.ArrayList;

import gameplay.GameBoard;
import utils.Direction;

public class PlayerPiece extends PuzzlePiece {

    public PlayerPiece(GameBoard gameBoard, int boardx, int boardy, String sideData) {
        super(gameBoard, GameObject.ObjectType.PLAYER_PIECE, boardx, boardy, sideData);
    }

    // update the player piece
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
                
                // then check if connections are valid
                ConnectInfo connectInfo = getConnectInfo(hdir, vdir, hdir, vdir);
                //System.out.println(connectInfo.toString());

                // if moving normally is ok and considering the puzzle pieces side data, move
                if (connectInfo.canMove()) {

                    moveAllAttached(moveInfo);

                    // connect puzzle pieces together
                    if (connectInfo.canConnect()) {
                        PuzzlePiece.connect(connectInfo);
                    }
                }
            }
        }
    }
}
