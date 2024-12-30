package gameplay.gameObjects.puzzlePiece;

import java.awt.Color;
import java.util.ArrayList;

import gameplay.GameBoard;
import gameplay.gameObjects.*;

public class PlayerPiece extends PuzzlePiece {

    public PlayerPiece(GameBoard gameBoard, int boardx, int boardy, String sideData, String baseStrengthString) {
        super(gameBoard, GameObject.ObjectType.PLAYER_PIECE, boardx, boardy, sideData, baseStrengthString);
        setColor(Color.LIGHT_GRAY);
    }

    // update the playerPiece
    @Override
    public void update(double dt) {

        // get player input
        int hdir = keyInput.keyClickedInt("D") - keyInput.keyClickedInt("A");
        int vdir = keyInput.keyClickedInt("S") - keyInput.keyClickedInt("W");

        // make sure there is movement
        if (hdir != 0 || vdir != 0) {

            // first check if movement is valid
            ArrayList<GameObject> selfList = new ArrayList<GameObject>(); // keeps track of what has already moved
            MoveInfo moveInfo = getAllMoveInfo(selfList, hdir, vdir);
            if (moveInfo.canMove()) {

                // find any potential breakpoint boundaries for movement
                ArrayList<GameObject> breakpointBoundaries = MoveLogic.findBreakpointBoundaries(gameBoard, getBoardX(), getBoardY(), hdir, vdir);
        
                // find breakpoints given the breakpoint boundaries (IN PROGRESS - not fully tested, may be some bugs)
                ArrayList<Side> breakpointSides = ConnectionLogic.findBreakpoints(gameBoard, breakpointBoundaries, hdir, vdir);

                // TO DO: disconnect breakpoints before movement
                ConnectionLogic.disconnectBreakpoints(breakpointSides);

                // move all connected pieces
                move(moveInfo, true);
            }
        }
    }
}
