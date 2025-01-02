package gameplay;

import java.awt.*;
import java.util.ArrayList;

import gameplay.gameObjects.*;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import gameplay.mapLoading.*;
import utils.Print;
import utils.drawing.InfoBox;
import utils.drawing.SimpleSprite;
import utils.drawing.Sprites;
import utils.input.*;

public class GameBoard {

    public static Color BOARD_COLOR = new Color(20, 20, 20);

    // window
    private GameManager gameManager;

    // input
    private KeyInput keyInput;
    private MouseInput mouseInput;
    public KeyInput getKeyInput() { return keyInput; }
    public MouseInput getMouseInput() { return mouseInput; }
    
    // board properties
    private int tileSize = 32;
    private int width = 10;
    private int height = 10;
    public int getTileSize() { return tileSize; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // visuals
    private double screenSizeRatio = 0.7;
    public double getScreenSizeRatio() { return screenSizeRatio; }

    // list of all game objects represented with a 2D grid
    private GameObject[][] board;

    // the game board the next frame
    private GameObject[][] nextBoard;
    // list of all game objects
    private ArrayList<GameObject> gameObjects;

    private SimpleSprite boardSprite;
    
    public GameBoard(GameManager gameManager, KeyInput keyInput, MouseInput mouseInput) {
        this.gameManager = gameManager;
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;

        gameObjects = new ArrayList<>();
        board = new GameObject[1][1];
        nextBoard = new GameObject[1][1];

        boardSprite = new SimpleSprite("board", getDrawX(), getDrawY(), getDrawWidth(), getDrawHeight(), "gameBoard") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(BOARD_COLOR);
                g.fillRect(getX(), getY(), getWidth(), getHeight());
            }
        };
    }

    public void printBoard() {
        System.out.print("GAME BOARD");
        for (int y=0; y<board.length; y++) {
            System.out.println();
            for (int x=0; x<board[0].length; x++) {
                if (board[y][x] == null)
                    System.out.print("0 ");
                else
                    Print.print(board[y][x].getObjectIndex() + " ", Print.BLUE);
            }
        }
        System.out.println();
    }

    // get the size of the game board
    public int getBoardWidth() { return width; }
    public int getBoardHeight() { return height; }

    public void showObjInfoBoxes() {
        for (GameObject gameObject : gameObjects) 
            gameObject.getInfoBox().setVisible(true);
    }
    public void hideObjInfoBoxes() {
        for (GameObject gameObject : gameObjects) 
            gameObject.getInfoBox().setVisible(false);
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
        
        // map size and board setup
        width = levelInfo.getMapWidth();
        height = levelInfo.getMapHeight();
        tileSize = (int) Math.min(gameManager.getWidth() * screenSizeRatio / width, 
                                  gameManager.getHeight() * screenSizeRatio / height);
        board = new GameObject[height][width];
        nextBoard = new GameObject[height][width];

        // update board sprite
        boardSprite.setX(getDrawX());
        boardSprite.setY(getDrawY());
        boardSprite.setWidth(getDrawWidth());
        boardSprite.setHeight(getDrawHeight());
        
        // set the board for the current level
        gameObjects = levelInfo.getGameObjects();
        for (int i=0; i<levelInfo.getGameObjects().size(); i++) {
            GameObject gameObject = levelInfo.getGameObjects().get(i);
            board[gameObject.getBoardY()][gameObject.getBoardX()] = gameObject;
            gameObject.setup();
            gameObject.updateVisualsAtStart(); // make sure gameobjects start in correct draw position
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

            gameObject.getInfoBox().setX(gameObject.getSprite().getX());
            gameObject.getInfoBox().setBottom(gameObject.getSprite().getY() - 5);
            gameObject.updateDrawList();

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

    public int findGameObjectDrawX(GameObject gameObject) {
        return getDrawX() + gameObject.getBoardX() * tileSize;
    }
    public int findGameObjectDrawY(GameObject gameObject) {
        return getDrawY() + gameObject.getBoardY() * tileSize;
    }

    public GameObject getPlayerPiece() {
        for (GameObject gameObject : gameObjects) 
            if (gameObject.getObjectType() == GameObject.ObjectType.PLAYER_PIECE) 
                return gameObject;
        return null;
    }

    // removes all game object sprites 
    public void clearGameObjects() {
        for (GameObject gameObject : gameObjects) 
            gameObject.deleteSprites();
    }
}
