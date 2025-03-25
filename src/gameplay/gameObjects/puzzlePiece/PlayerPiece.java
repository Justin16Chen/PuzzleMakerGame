package gameplay.gameObjects.puzzlePiece;

import java.awt.Graphics2D;

import org.json.JSONObject;

import gameplay.gameObjects.*;
import utils.SFXPlayer;
import utils.drawing.sprites.Sprite;

public class PlayerPiece extends PuzzlePiece {


    public static GameObject loadPlayerPiece(JSONObject jsonObject) {
        return new PlayerPiece(jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData")); 
    }

    private static final String SAD_IMAGE_PATH = "res/textures/playerSad.png", HAPPY_IMAGE_PATH = "res/textures/playerHappy.png";
    private static final String MOVE_SFX1_PATH = "res/sfx/synth (2).wav", MOVE_SFX2_PATH = "res/sfx/click (1).wav";

    private Sprite faceSprite;
    public PlayerPiece(int boardX, int boardY, String sideData) {
        super(GameObject.ObjectType.PLAYER_PIECE, boardX, boardY, sideData);
    }

    @Override
    public void setup(int x, int y, int width, int height) {
        super.setup(x, y, width, height);
        faceSprite = new Sprite("player face", SAD_IMAGE_PATH, x, y, width, height, "gameObjects2") {
            @Override
            public void draw(Graphics2D g) {
                // reposition image
                setX(sprite.getX());
                setY(sprite.getY());
                setWidth(sprite.getWidth());
                setHeight(sprite.getHeight());

                // draw image
                super.draw(g);
            }
        };
        faceSprite.addTag("accessory");
        sprite.addChild(faceSprite);
    }

    private void playMoveSfx() {
        SFXPlayer.play(MOVE_SFX1_PATH, -8);
        SFXPlayer.play(MOVE_SFX2_PATH, -32);
    }

    @Override
    public void move(GameBoard gameBoard, MoveInfo moveInfo, boolean isMover) {
        if (isMover && !movedThisFrame) 
            playMoveSfx();
        super.move(gameBoard, moveInfo, isMover);
    }

    @Override
    public void update(GameBoard board) {
        if (board.allPuzzlePiecesConnected())
            faceSprite.setImagePath(HAPPY_IMAGE_PATH);
    }
}
