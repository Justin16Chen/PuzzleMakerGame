package gameplay.gameObjects.puzzlePiece;

import java.awt.Graphics2D;

import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import utils.drawing.sprites.Sprite;

public class PlayerPiece extends PuzzlePiece {


    public static GameObject loadPlayerPiece(JSONObject jsonObject) {
        return new PlayerPiece(jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData")); 
    }

    private static final String SAD_IMAGE_PATH = "res/textures/playerSad.png", HAPPY_IMAGE_PATH = "res/textures/playerHappy.png";

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

    @Override
    public void update(GameBoard board) {
        if (board.allPuzzlePiecesConnected())
            sprite.setImagePath(HAPPY_IMAGE_PATH);
    }
}
