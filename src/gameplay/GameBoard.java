package gameplay;

import java.awt.*;
import java.util.*;

public class GameBoard {
    
    // board properties
    public final int TILE_SIZE = 32;
    public final int WIDTH = 10;
    public final int HEIGHT = 10;

    // list of all game objects represented with a 2D grid
    private ArrayList<ArrayList<GameObject>> board;
    
    // CONSTRUCTOR
    public GameBoard() {
        board = new ArrayList<>();

        // TEMP fill the board with some game objects
        createBoard();
    }

    private void createBoard() {
        System.out.println("width: " + WIDTH + " | height: " + HEIGHT);
        for (int y=0; y<HEIGHT; y++) {
            ArrayList<GameObject> row = new ArrayList<>();
            for (int x=0; x<WIDTH; x++) {
                if (x == 2 && y == 3) { row.add(new PuzzlePiece(this, x, y)); } 
                else                  { row.add(new EmptyObject(this, x, y)); }
            }
            board.add(row);
        }
    }

    // update loop
    public void update() {
        for (ArrayList<GameObject> row : board) {
            for (GameObject gameObject : row) {
                gameObject.update();
            }
        }
    }

    // drawing getters
    public int getDrawX() { return 100; }
    public int getDrawY() { return 100; }
    public int getDrawWidth() { return WIDTH * TILE_SIZE; }
    public int getDrawHeight() { return HEIGHT * TILE_SIZE; }

    // draw the board and all of the game objects on the board
    public void draw(Graphics2D g) {
        g.fillRect(getDrawX(), getDrawY(), getDrawWidth(), getDrawHeight());

        try {
            // loop through game board and draw game objects
            for (int y=0; y<HEIGHT; y++) {
                ArrayList<GameObject> row = board.get(y);
                for (int x=0; x<WIDTH; x++) {
                    GameObject gameObject = row.get(x);
                    drawGameObject(g, gameObject);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println(board.size());
        }
    }

    // TEMP draw function to draw game object
    private void drawGameObject(Graphics2D g, GameObject gameObject) {
        int x = getDrawX() + gameObject.getBoardX() * TILE_SIZE;
        int y = getDrawY() + gameObject.getBoardY() * TILE_SIZE;

        g.setColor(getGameObjectColor(gameObject));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        if (gameObject.getObjectType() != GameObject.OBJECT_TYPE.EMPTY) {
            g.setColor(Color.WHITE);
            g.drawString(gameObject.getName(), x, y);
        }
    }

    // TEMP get the color of a game object
    private Color getGameObjectColor(GameObject gameObject) {
        switch (gameObject.getObjectType()) {
            case PUZZLE_PIECE: return Color.BLUE;
            case PLAYER_PIECE: return Color.GREEN;
            case EMPTY: return new Color(0, 0, 0, 0);
            default: return new Color(0, 0, 0, 0);
        }
    }
}
