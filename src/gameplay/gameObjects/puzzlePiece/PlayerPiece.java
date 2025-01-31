package gameplay.gameObjects.puzzlePiece;

import java.awt.Graphics2D;

import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import utils.direction.Directions;
import utils.drawing.sprites.Sprite;

public class PlayerPiece extends PuzzlePiece {

    public static GameObject loadPlayerPiece(JSONObject jsonObject) {
        return new PlayerPiece(jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData")); 
    }

    private static final String SAD_IMAGE_PATH = "res/textures/playerSad.png", HAPPY_IMAGE_PATH = "res/textures/playerHappy.png";

    private String currentImagePath;
    public PlayerPiece(int boardX, int boardY, String sideData) {
        super(GameObject.ObjectType.PLAYER_PIECE, boardX, boardY, sideData);
    }

    @Override
    public void setup(int x, int y, int width, int height) {
        sprite = new Sprite("puzzlePieceSprite", SAD_IMAGE_PATH, x, y, width, height, "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                
                super.draw(g);

                for (int i=0; i<4; i++)
                    getSide(Directions.getDirection(i)).draw(g, getX(), getY(), getWidth());

            }
        };
    }

    @Override
    public void update(GameBoard board) {
        if (board.allPuzzlePiecesConnected())
            sprite.setImagePath(HAPPY_IMAGE_PATH);
    }
}
