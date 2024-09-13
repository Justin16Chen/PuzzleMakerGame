package gameplay;

import java.awt.*;

import gameplay.gameObjects.*;
import gameplay.mapLoading.*;
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
    public int tileSize = 32;
    public int width = 10;
    public int height = 10;

    // visuals
    public double screenSizeRatio = 0.7;

    // list of all game objects represented with a 2D grid
    private GameObject[][] board;
    
    
    public GameBoard(GameManager gameManager, KeyInput keyInput, MouseInput mouseInput) {
        this.gameManager = gameManager;
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;
    }

    // check if a position is in the board boundaries
    public boolean inBounds(int boardx, int boardy) {
        return boardx >= 0 && boardx < width && boardy >= 0 && boardy < height;
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

    // create a new game board given the map info
    public void setCurrentBoard(MapInfo mapInfo) {
        width = mapInfo.getMapWidth();
        height = mapInfo.getMapHeight();
        tileSize = (int) Math.min(
            gameManager.getWidth() * screenSizeRatio / width, 
            gameManager.getHeight() * screenSizeRatio / height
        );
        board = new GameObject[height][width];

        for (int i=0; i<mapInfo.getGameObjects().size(); i++) {
            GameObject gameObject = mapInfo.getGameObjects().get(i);
            board[gameObject.getBoardY()][gameObject.getBoardX()] = gameObject;
        }
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
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                GameObject gameObject = board[y][x];
                if (gameObject == null) { continue; }
                newBoard[gameObject.getBoardY()][gameObject.getBoardX()] = gameObject;
            }
        }
        return newBoard;
    }

    // checks if all of the puzzle pieces are connected (win condition)
    public boolean allPuzzlePiecesConnected() {
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                GameObject gameObject = getGameObject(y, x);
                if (gameObject == null) { continue; }
                if (gameObject.getObjectType() == GameObject.ObjectType.PUZZLE_PIECE) {
                    PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;

                    if (!puzzlePiece.hasConnection()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
    public int getDrawX() { return (int) ((gameManager.getWidth() - getDrawWidth()) * 0.5); }
    public int getDrawY() { return (int) ((gameManager.getHeight() - getDrawHeight()) * 0.5); }
    public int getDrawWidth() { return width * tileSize; }
    public int getDrawHeight() { return height * tileSize; }

    // draw the board and all of the game objects on the board
    public void draw(Graphics2D g) {
        g.fillRect(getDrawX(), getDrawY(), getDrawWidth(), getDrawHeight());

        // loop through game board and draw game objects
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                GameObject gameObject = board[y][x];
                if (gameObject == null) { continue; }
                //drawGameObject(g, gameObject);
                int drawx = getDrawX() + gameObject.getBoardX() * tileSize;
                int drawy = getDrawY() + gameObject.getBoardY() * tileSize;
                gameObject.draw(g, drawx, drawy);
                gameObject.drawPosition(g, drawx + 4, drawy + (int) (tileSize * 0.5));
            }
        }
    }
}
