package gameplay;

import java.awt.*;
import java.util.ArrayList;

import gameplay.gameObjects.*;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import gameplay.mapLoading.*;
import utils.drawing.InfoBox;
import utils.input.*;

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

    // the game board the next frame
    private GameObject[][] nextBoard;
    // list of all game objects
    private ArrayList<GameObject> gameObjects;
    
    
    public GameBoard(GameManager gameManager, KeyInput keyInput, MouseInput mouseInput) {
        this.gameManager = gameManager;
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;

        gameObjects = new ArrayList<>();
        board = new GameObject[1][1];
        nextBoard = new GameObject[1][1];
    }

    // get the size of the game board
    public int getBoardWidth() { return width; }
    public int getBoardHeight() { return height; }

    public void showObjInfoBoxes() {
        for (GameObject gameObject : gameObjects) {
            gameObject.getInfoBox().show();
        }
    }
    public void hideObjInfoBoxes() {
        for (GameObject gameObject : gameObjects) {
            gameObject.getInfoBox().hide();
        }
    }

    // check if a position is in the board boundaries
    public boolean inBounds(int boardx, int boardy) {
        return boardx >= 0 && boardx < width && boardy >= 0 && boardy < height;
    }

    // gets all the game objects on the board
    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }
    // get the game object at a certain position
    public GameObject getGameObject(int boardx, int boardy) {
        if (!inBounds(boardx, boardy)) { 
            throw new RuntimeException(boardx + ", " + boardy + " is out of bounds");
        }

        // first check next board
        if (nextBoard[boardy][boardx] != null) {
            return nextBoard[boardy][boardx];
        }
        // empty cell or if the object at old board moved, cell must be empty
        if (board[boardy][boardx] == null || board[boardy][boardx].movedThisFrame()) return null;
        
        // object at old board has not moved
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
    public void setCurrentBoard(LevelInfo levelInfo) {
        
        // map size
        width = levelInfo.getMapWidth();
        height = levelInfo.getMapHeight();
        tileSize = (int) Math.min(
            gameManager.getWidth() * screenSizeRatio / width, 
            gameManager.getHeight() * screenSizeRatio / height
        );

        for (GameObject gameObject : gameObjects) {
            InfoBox.removeInfoBox(gameObject.getInfoBox());
        }

        // game objects
        gameObjects = levelInfo.getGameObjects();
        board = new GameObject[height][width];
        nextBoard = new GameObject[height][width];

        // set the board for the current frame
        for (int i=0; i<levelInfo.getGameObjects().size(); i++) {
            GameObject gameObject = levelInfo.getGameObjects().get(i);
            board[gameObject.getBoardY()][gameObject.getBoardX()] = gameObject;
        }
    }

    // update loop
    public void update(double dt) {

        // clear the next board
        nextBoard = createEmptyBoard(width, height);

        // update current board
        for (int i=0; i<gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.resetMovedThisFrame();
            gameObject.resetQueuedMovedThisFrame();
            gameObject.update(dt);

            // put updated object into next board
            nextBoard[gameObject.getBoardY()][gameObject.getBoardX()] = gameObject;
        }
        // set the old board to the new board for next frame
        board = updateGameObjectPositions(board);
    }

    private GameObject[][] updateGameObjectPositions(GameObject[][] board) {
        GameObject[][] newBoard = createEmptyBoard(board[0].length, board.length);
        for (int i=0; i<gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
                newBoard[gameObject.getBoardY()][gameObject.getBoardX()] = gameObject;
        }
        return newBoard;
    }

    // checks if all of the puzzle pieces are connected (win condition)
    public boolean allPuzzlePiecesConnected() {
        for (int i=0; i<gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);

            if (gameObject.getObjectType() == GameObject.ObjectType.PUZZLE_PIECE) {
                PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;

                if (!puzzlePiece.hasConnectedSide()) {
                    return false;
                }
            }
        }
        return true;
    }

    // drawing getters
    public int getDrawX() { 
        return (int) ((gameManager.getWidth() - getDrawWidth()) * 0.5); 
    }
    public int getDrawY() { 
        return (int) ((gameManager.getHeight() - getDrawHeight()) * 0.5); 
    }
    public int getDrawWidth() { return width * tileSize; }
    public int getDrawHeight() { return height * tileSize; }
    public int getDrawCenterX() { return getDrawX() + (int) (getDrawWidth() * 0.5); }
    public int getDrawCenterY() { return getDrawY() + (int) (getDrawHeight() * 0.5); }

    // draw the board and all of the game objects on the board
    public void draw(Graphics2D g) {
        // game board
        g.fillRect(getDrawX(), getDrawY(), getDrawWidth(), getDrawHeight());

        // loop through game board and draw game objects
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                GameObject gameObject = board[y][x];
                if (gameObject == null) { continue; }
                
                int drawx = getDrawX() + gameObject.getBoardX() * tileSize;
                int drawy = getDrawY() + gameObject.getBoardY() * tileSize;
                gameObject.draw(g, drawx, drawy);
                gameObject.updateInfoList(g, drawx + tileSize / 2, drawy - 10);
                if (mouseInput.clicked()) {
                    if (mouseInput.isOver(drawx, drawy, tileSize, tileSize)) {
                        if (!gameObject.getInfoBox().isVisible()) gameObject.getInfoBox().show();
                        else gameObject.getInfoBox().hide();
                    }
                }
                g.setColor(Color.BLACK);
                g.drawString("(" + x + ", " + y + ")", drawx + tileSize / 4, drawy + tileSize / 2);
            }
        }
    }
}
