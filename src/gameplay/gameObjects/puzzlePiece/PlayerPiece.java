package gameplay.gameObjects.puzzlePiece;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import utils.direction.Direction;
import utils.direction.Directions;
import utils.drawing.InfoBox;
import utils.drawing.SimpleSprite;
import utils.drawing.Sprites;
import utils.tween.Tween;

public class PlayerPiece extends PuzzlePiece {

    public static ArrayList<GameObject> loadPlayerPiece(JSONObject jsonObject, GameBoard gameBoard) {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        gameObjects.add(new PlayerPiece(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData"))); 
        return gameObjects;
    }

    public static int BRIGHT_OUTLINE = 200, DIM_OUTLINE = 100;
    public static double OCILLATION_TIME = 1.8;
    public static int STROKE_WIDTH = 2, STROKE_INSET = -2;

    private SimpleSprite outlineSprite;
    private double outlineColor;

    public PlayerPiece(GameBoard gameBoard, int boardx, int boardy, String sideData) {
        super(gameBoard, GameObject.ObjectType.PLAYER_PIECE, boardx, boardy, sideData);
    }

    @Override
    public void setup() {
        
        sprite = new SimpleSprite("puzzlePieceSprite", gameBoard.findGameObjectDrawX(this), gameBoard.findGameObjectDrawY(this), gameBoard.getTileSize(), gameBoard.getTileSize(), "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
        
                // draw fill
                g.setColor(areAllSidesConnected() ? getHighlightedColor() : PuzzlePiece.COLOR);
                g.fillRect(sprite.getX(), sprite.getY(), gameBoard.getTileSize(), gameBoard.getTileSize());
        
                for (Direction direction : Directions.getAllDirections())
                    getSide(direction).draw(g, sprite.getX(), sprite.getY(), gameBoard.getTileSize());
            }
        };

        outlineSprite = new SimpleSprite("playerOutline", sprite.getX(), sprite.getY(), gameBoard.getTileSize(), gameBoard.getTileSize(), "effects") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(new Color((int) outlineColor, (int) outlineColor, (int) outlineColor));
                g.setStroke(new BasicStroke(STROKE_WIDTH));
                g.drawRect(sprite.getX() + STROKE_INSET, sprite.getY() + STROKE_INSET, gameBoard.getTileSize() - STROKE_INSET * 2, gameBoard.getTileSize() - STROKE_INSET * 2);
            }
        };

        Tween.createTween("playerOutline", this, "outlineColor", DIM_OUTLINE, BRIGHT_OUTLINE, OCILLATION_TIME).pingPong().setLoopCount(-1);

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

                // update the move indecies of all game objects
                MoveLogic.updateMoveIndecies(gameBoard, getBoardX(), getBoardY());

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
    public void deleteSprites() {
        Sprites.deleteSprites(new String[]{sprite.getName(), outlineSprite.getName(), InfoBox.NAME});
    }
}
