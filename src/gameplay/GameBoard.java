package gameplay;

import java.awt.*;

import gameplay.gameObjects.*;
import input.*;

public class GameBoard {

    // input
    private KeyInput keyInput;
    private MouseInput mouseInput;
    public KeyInput getKeyInput() { return keyInput; }
    public MouseInput getMouseInput() { return mouseInput; }
    
    // board properties
    public final int TILE_SIZE = 32;
    public final int WIDTH = 10;
    public final int HEIGHT = 10;

    // list of all game objects represented with a 2D grid
    private GameObject[][] board;
    
    
    public GameBoard(KeyInput keyInput, MouseInput mouseInput) {
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;

        board = createEmptyBoard(WIDTH, HEIGHT);

        // TEMP fill the board with some game objects
        board = fillBoard(board);
    }

    // check if a position is in the board boundaries
    public boolean inBounds(int boardx, int boardy) {
        return boardx >= 0 && boardx < WIDTH && boardy >= 0 && boardy < HEIGHT;
    }

    // get the game object at a certain position
    public GameObject getGameObject(int boardx, int boardy) {
        return board[boardy][boardx];
    }

    // get the game object type at a certain position
    public GameObject.ObjectType getGameObjectType(int boardx, int boardy) {
        if (!inBounds(boardx, boardy)) { return GameObject.ObjectType.WALL; }
        GameObject gameObject = getGameObject(boardx, boardy);
        return gameObject == null ? null : gameObject.getObjectType();
    }

    private GameObject[][] createEmptyBoard(int boardWidth, int boardHeight) {
        GameObject[][] emptyBoard = new GameObject[boardHeight][boardWidth];
        return emptyBoard;
    }

    private GameObject[][] fillBoard(GameObject[][] emptyBoard) {
        GameObject[][] newBoard = new GameObject[emptyBoard.length][emptyBoard[0].length];
        for (int y=0; y<emptyBoard.length; y++) {
            for (int x=0; x<emptyBoard[0].length; x++) {
                if      (x == 2 && y == 2) { newBoard[y][x] = new PuzzlePiece(this, x, y, "0200"); }
                else if (x == 2 && y == 3) { newBoard[y][x] = new PuzzlePiece(this, x, y, "0200"); }
                else if (x == 2 && y == 4) { newBoard[y][x] = new PlayerPiece(this, x, y, "0001"); } 
            }
        }
        return newBoard;
    }

    // update loop
    public void update(double dt) {

        for (GameObject[] row : board) {
            for (GameObject gameObject : row) {
                if (gameObject == null) { continue; }
                gameObject.update(dt);
            }
        }
        //printBoard(board);
        board = updateGameObjectPositions(board);
        //printBoard(board);
    }

    private GameObject[][] updateGameObjectPositions(GameObject[][] board) {
        GameObject[][] newBoard = createEmptyBoard(board[0].length, board.length);
        for (int y=0; y<HEIGHT; y++) {
            for (int x=0; x<WIDTH; x++) {
                GameObject gameObject = board[y][x];
                if (gameObject == null) { continue; }
                newBoard[gameObject.getBoardY()][gameObject.getBoardX()] = gameObject;
            }
        }
        return newBoard;
    }

    public void printBoard(GameObject[][] board) {
        for (int y=0; y<board.length; y++) {
            for (int x=0; x<board.length; x++) {
                if (board[y][x] == null) { continue; }
                System.out.println(board[y][x].toString());
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

        // loop through game board and draw game objects
        for (int y=0; y<HEIGHT; y++) {
            for (int x=0; x<WIDTH; x++) {
                GameObject gameObject = board[y][x];
                if (gameObject == null) { continue; }
                drawGameObject(g, gameObject);
            }
        }
    }

    // TEMP draw function to draw game object
    private void drawGameObject(Graphics2D g, GameObject gameObject) {
        int x = getDrawX() + gameObject.getBoardX() * TILE_SIZE;
        int y = getDrawY() + gameObject.getBoardY() * TILE_SIZE;

        g.setColor(getGameObjectColor(gameObject));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        //drawGameObjectName(g, gameObject, x, y);
    }

    private void drawGameObjectName(Graphics2D g, GameObject gameObject, int x, int y) {

        if (gameObject.getObjectType() != null) {
            g.setColor(Color.WHITE);
            g.drawString(gameObject.getName(), x, y);
        }
    }

    // TEMP get the color of a game object
    private Color getGameObjectColor(GameObject gameObject) {
        switch (gameObject.getObjectType()) {
            case PUZZLE_PIECE: return Color.BLUE;
            case PLAYER_PIECE: return Color.GREEN;
            default: return new Color(0, 0, 0, 0);
        }
    }
}
