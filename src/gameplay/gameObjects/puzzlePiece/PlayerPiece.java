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
import utils.drawing.sprites.Sprite;
import utils.drawing.sprites.Sprites;
import utils.tween.Tween;

public class PlayerPiece extends PuzzlePiece {

    public static GameObject loadPlayerPiece(JSONObject jsonObject, GameBoard gameBoard) {
        return new PlayerPiece(gameBoard, jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData")); 
    }

    public static int BRIGHT_OUTLINE = 200, DIM_OUTLINE = 100;
    public static double OCILLATION_TIME = 1.8;
    public static int STROKE_WIDTH = 2, STROKE_INSET = -2;

    private Sprite outlineSprite;
    private double outlineColor;

    public PlayerPiece(GameBoard gameBoard, int boardx, int boardy, String sideData) {
        super(gameBoard, GameObject.ObjectType.PLAYER_PIECE, boardx, boardy, sideData);
    }

    @Override
    public void setup() {
        super.setup();

        outlineSprite = new Sprite("playerOutline", sprite.getX(), sprite.getY(), gameBoard.getTileSize(), gameBoard.getTileSize(), "effects") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(new Color((int) outlineColor, (int) outlineColor, (int) outlineColor));
                g.setStroke(new BasicStroke(STROKE_WIDTH));
                g.drawRect(sprite.getX() + STROKE_INSET, sprite.getY() + STROKE_INSET, gameBoard.getTileSize() - STROKE_INSET * 2, gameBoard.getTileSize() - STROKE_INSET * 2);
            }
        };

        Tween.createTween("playerOutline", this, "outlineColor", DIM_OUTLINE, BRIGHT_OUTLINE, OCILLATION_TIME).pingPong().setLoopCount(-1);
    }

    @Override
    public void deleteSprites() {
        Sprites.deleteSprites(new String[]{sprite.getName(), outlineSprite.getName(), InfoBox.NAME});
    }
}
