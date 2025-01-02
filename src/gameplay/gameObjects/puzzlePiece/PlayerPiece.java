package gameplay.gameObjects.puzzlePiece;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import utils.direction.Direction;
import utils.direction.Directions;
import utils.tween.Tween;
import utils.tween.Updatable;
import utils.tween.Updatables;

public class PlayerPiece extends PuzzlePiece {

    public static int BRIGHT_OUTLINE = 200, DIM_OUTLINE = 100;
    public static double OCILLATION_TIME = 1.8;
    public static int STROKE_WIDTH = 2, STROKE_INSET = -2;

    private double outlineBrightness;
    private Tween outlineTween;

    public PlayerPiece(GameBoard gameBoard, int boardx, int boardy, String sideData, String baseStrengthString) {
        super(gameBoard, GameObject.ObjectType.PLAYER_PIECE, boardx, boardy, sideData, baseStrengthString);
    }

    @Override
    public void setup() {
        outlineTween = Tween.createTween("playerOutline", this, "outlineBrightness", DIM_OUTLINE, BRIGHT_OUTLINE, OCILLATION_TIME).pingPong().setLoopCount(5).setPrint(Updatable.PrintType.ON_LOOP);
        System.out.println("updatables in player setup: \n" + Updatables.getUpdatablesToString(4));
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
    
    @Override
    public void draw(Graphics2D g) {
        updateCurrentDrawPosToTarget(); // allow for smooth movement

        // draw fill
        g.setColor(hasConnectedSide() ? getHighlightedColor() : getColor());
        g.fillRect(getCurrentDrawx(), getCurrentDrawy(), gameBoard.tileSize, gameBoard.tileSize);

        // draw outline
        int brightness = (int) outlineBrightness;
        g.setColor(new Color(brightness, brightness, brightness));
        g.setStroke(new BasicStroke(STROKE_WIDTH));
        g.drawRect(getCurrentDrawx() + STROKE_INSET, getCurrentDrawy() + STROKE_INSET, gameBoard.tileSize - STROKE_INSET * 2, gameBoard.tileSize - STROKE_INSET * 2);

        for (Direction direction : Directions.getAllDirections())
            getSide(direction).draw(g, getCurrentDrawx(), getCurrentDrawy(), gameBoard.tileSize);
    }

    @Override
    public void updateInfoList(Graphics2D g, int drawcx, int drawbottomy) {
        ArrayList<String> drawList = new ArrayList<String>();

        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() + ")");
        drawList.add("own outlineTween: " + outlineTween);
        drawList.add("updatables: " + Arrays.toString(Updatables.getUpdatables().toArray()));
        drawList.add("outlineTween in list: " + Updatables.getUpdatable(outlineTween.getName()));

        setInfoList(g, drawcx, drawbottomy, drawList);
    }
}
