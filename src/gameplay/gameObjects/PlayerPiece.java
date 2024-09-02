package gameplay.gameObjects;

import gameplay.GameBoard;

public class PlayerPiece extends PuzzlePiece {

    public PlayerPiece(GameBoard gameBoard, int boardx, int boardy, String sideData) {
        super(gameBoard, GameObject.ObjectType.PLAYER_PIECE, boardx, boardy, sideData);
    }

    // update the player piece
    @Override
    public void update(double dt) {

        // get player input
        int hdir = keyInput.keyClickedInt("D") - keyInput.keyClickedInt("A");
        int vdir = keyInput.keyClickedInt("S") - keyInput.keyClickedInt("W");

        // make sure there is movement
        if (hdir != 0 || vdir != 0) {

            // first check if movement is valid
            MoveInfo[] moveInfoList = getAllMoveInfo(hdir, vdir);

            System.out.println("length of move info list: " + moveInfoList.length);

            if (moveInfoList.length > 1) {
                
                // then check if connections are valid
                ConnectInfo connectInfo = getConnectInfo(hdir, vdir, hdir, vdir);
                //System.out.println(connectInfo.toString());

                // if moving normally is ok and considering the puzzle pieces side data, move
                if (connectInfo.canMove()) {
                    move(moveInfoList[4]);

                    // connect puzzle pieces together
                    if (connectInfo.canConnect()) {
                        connect(connectInfo);
                    }
                }
            }
        }
    }

    public MoveInfo[] getAllMoveInfo(int hdir, int vdir) {
        // make sure movement is in bounds and is valid
        // currently using base GameObject.getMoveInfo
        // need to create PuzzlePiece.getMoveInfo to account for any connected PuzzlePieces

        // get the move info for all four sides (if they are connected) to make sure anything connected can also move
        MoveInfo[] moveInfoList = new MoveInfo[5];

        moveInfoList[0] = getMoveInfo( 0, -1, hdir, vdir);       // up
        moveInfoList[1] = getMoveInfo(-1,  0, hdir, vdir);       // left
        moveInfoList[2] = getMoveInfo( 0,  1, hdir, vdir);       // down
        moveInfoList[3] = getMoveInfo( 1,  0, hdir, vdir);       // right

        // ignore if side is not connected
        for (int i=0; i<4; i++) {
            if (!connectedSides[i]) {
                moveInfoList[i] = null;
            }
        }

        // ignore if side is on opposite side of movement (will collide with player piece)
        moveInfoList[getSideIndex(getDirection(-hdir, -vdir))] = null;

        // also always get move info for base piece with no side offsets
        moveInfoList[4] = getMoveInfo(hdir, vdir);

        System.out.println("move info list of player: ");
        for (int i=0; i<5; i++) {
            System.out.println("side " + i + ": " + moveInfoList[i]);
        }

        // can only move if all of the moveInfos are valid
        for (int i=0; i<4; i++) {
            if (moveInfoList[i] == null) { continue; }
            if (!moveInfoList[i].canMove()) {
                return MoveInfo.makeInvalidMoveList();
            }
        }
        return moveInfoList;
    }
}
