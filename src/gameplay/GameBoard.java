package gameplay;

import java.awt.*;

import gameplay.gameObjects.*;
import input.*;

public class GameBoard {

    // window
    private GameManager gameManager;

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
    
    
    public GameBoard(GameManager gameManager, KeyInput keyInput, MouseInput mouseInput) {
        this.gameManager = gameManager;
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
        if (!inBounds(boardx, boardy)) { return GameObject.OUT_OF_BOUNDS; }
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
                if      (x == 5 && y == 6) { newBoard[y][x] = new Wall(this, x, y); }
                else if (x == 2 && y == 3) { newBoard[y][x] = new PuzzlePiece(this, x, y, "0201"); }
                else if (x == 4 && y == 3) { newBoard[y][x] = new PuzzlePiece(this, x, y, "0200"); }
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
                gameObject.resetMovedThisFrame();
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
                //System.out.println(board[y][x].toString());
            }
        }
    }

    // drawing getters
    public int getDrawX() { return (int) ((gameManager.getWidth() - getDrawWidth()) * 0.5); }
    public int getDrawY() { return (int) ((gameManager.getHeight() - getDrawHeight()) - 30); }
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
                //drawGameObject(g, gameObject);
                int drawx = getDrawX() + gameObject.getBoardX() * TILE_SIZE;
                int drawy = getDrawY() + gameObject.getBoardY() * TILE_SIZE;
                gameObject.draw(g, drawx, drawy);
                gameObject.drawPosition(g, drawx + 4, drawy + (int) (TILE_SIZE * 0.5));
            }
        }
    }
}
