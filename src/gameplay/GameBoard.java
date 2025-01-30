package gameplay;

import java.awt.*;
import java.util.ArrayList;

import gameplay.gameObjects.*;
import gameplay.gameObjects.puzzlePiece.PlayerPiece;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import gameplay.mapLoading.*;
import utils.Print;
import utils.drawing.sprites.Sprite;
import utils.input.*;

public class GameBoard {

    public static Color BOARD_COLOR = new Color(20, 20, 20);

    // input
    private KeyInput keyInput;
    private MouseInput mouseInput;
    public KeyInput getKeyInput() { return keyInput; }
    public MouseInput getMouseInput() { return mouseInput; }
    
    // board properties
    private int tileSize = 32;
    public int getTileSize() { return tileSize; }
    public void setTileSize(int drawWidth, int drawHeight) {
        tileSize = (int) Math.min(drawWidth / width, 
                                  drawHeight / height);
    }
    private int width = 10;
    public int getBoardWidth() { return width; }
    private int height = 10;
    public int getBoardHeight() { return height; }

    // list of all game objects represented with a 2D grid
    private GameObject[][] board;

    // list of all game objects
    private ArrayList<GameObject> gameObjects;

    private Sprite boardSprite;
    public Sprite getBoardSprite() { return boardSprite; }
    
    public GameBoard(KeyInput keyInput, MouseInput mouseInput) {
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;

        gameObjects = new ArrayList<>();
        board = new GameObject[1][1];
    }

    public void setup() {
        boardSprite = new Sprite("board", 0, 0, 1, 1, "gameBoard") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(BOARD_COLOR);
                g.fillRect(getX(), getY(), getWidth(), getHeight());
                //g.setFont(new Font("Arial", Font.BOLD, 15));
                //g.drawString("Level " + gameManager.getLevelManager().getCurrentLevel(), getX(), getY() - 10);
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

    public void showObjInfoBoxes() {
        for (GameObject gameObject : gameObjects) 
            gameObject.getInfoBox().setVisible(true);
    }
    public void hideObjInfoBoxes() {
        for (GameObject gameObject : gameObjects) 
            gameObject.getInfoBox().setVisible(false);
    }

    // check if a position is in the board boundaries
    public boolean inBounds(int boardX, int boardY) {
        return boardX >= 0 && boardX < width && boardY >= 0 && boardY < height;
    }

    // gets all the game objects on the board
    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }
    // get the game object at a certain position
    public GameObject getGameObject(int boardX, int boardY) {
        if (!inBounds(boardX, boardY))
            throw new RuntimeException(boardX + ", " + boardY + " is out of bounds");
        return board[boardY][boardX];
    }

    // get the game object type at a certain position
    public GameObject.ObjectType getGameObjectType(int boardX, int boardY) {
        if (!inBounds(boardX, boardY)) { return GameObject.ObjectType.WALL; }
        GameObject gameObject = getGameObject(boardX, boardY);
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
        board = new GameObject[height][width];
        
        // set the board for the current level
        gameObjects = levelInfo.getGameObjects();
        for (int i=0; i<levelInfo.getGameObjects().size(); i++) {
            GameObject gameObject = levelInfo.getGameObjects().get(i);
            // instiate game object on all cells that it covers
            for (int y=0; y<gameObject.getCellHeight(); y++) 
                for (int x=0; x<gameObject.getCellWidth(); x++)
                    board[gameObject.getBoardY() + y][gameObject.getBoardX() + x] = gameObject;
        }
    }

    public void deleteGameObject(GameObject gameObject) {
        for (GameObject go : gameObjects)
            if (go.equals(gameObject)) {
                go.deleteSprites();
                gameObjects.remove(go);
                return;
            }
    }
    public void addGameObject(GameObject gameObject) {
        gameObject.updateVisualsToBoard(this);
        gameObjects.add(gameObject);
    }

    public void setupGameObjectVisuals() {
        for (GameObject gameObject : gameObjects) {
            gameObject.updateVisualsAtStart(this); // make sure gameobjects start in correct draw position

            // check for any puzzle pieces already connected and update that
            if (!PuzzlePiece.isPuzzlePiece(gameObject)) 
                continue;
            
            PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;
            puzzlePiece.checkForConnections(this, MoveInfo.makeValidMove(0, 0), false);
        }
    }

    public void resizeAndRepositionGameObjects() {
        for (GameObject gameObject : gameObjects)
            gameObject.updateVisualsToBoard(this);
    }

    public void updateBoardVisuals(int centerx, int centery, int width, int height) {
            setTileSize(width, height);
            boardSprite.setWidth(tileSize * this.width);
            boardSprite.setHeight(tileSize * this.height);
            boardSprite.setCenterX(centerx);
            boardSprite.setCenterY(centery);
    
            // update game object visuals once game board is set
            setupGameObjectVisuals();
    }
    // update loop
    public void update(double dt) {
        checkForInput();
        updateDebugInfoBoxes();
    }

    private void checkForInput() {
        // get player input
        int hdir = keyInput.keyClickedInt("D") - keyInput.keyClickedInt("A");
        int vdir = keyInput.keyClickedInt("S") - keyInput.keyClickedInt("W");

        // make sure there is movement
        if (hdir != 0 || vdir != 0) {

            for (GameObject gameObject : gameObjects)
                gameObject.resetMovedThisFrame();
            
            for (GameObject playerGameObject : gameObjects) {
                if (playerGameObject.getObjectType() != GameObject.ObjectType.PLAYER_PIECE)
                    continue;
                
                PlayerPiece player = (PlayerPiece) playerGameObject;
    
                // first check if movement is valid
                ArrayList<GameObject> selfList = new ArrayList<GameObject>(); // keeps track of what has already moved
                MoveInfo moveInfo = player.getMoveInfo(this, selfList, hdir, vdir);
                
                if (moveInfo.canMove()) {
    
                    // disconnect any breakpoints
                    ArrayList<GameObject[]> breakpoints = MoveLogic.findBreakpoints(this, player, hdir, vdir);
                    MoveLogic.disconnectBreakpoints(breakpoints);
    
                    // move all connected pieces
                    player.move(this, moveInfo, true);
                }
            }
        }
    }

    private void updateDebugInfoBoxes() {
        for (int i=0; i<gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);

            // update game object info box
            if (mouseInput.clicked()
                && mouseInput.isOver(gameObject.getSprite().getX(), gameObject.getSprite().getY(), gameObject.getSprite().getWidth(), gameObject.getSprite().getHeight())) 
                gameObject.getInfoBox().setVisible(!gameObject.getInfoBox().isVisible());
            if (gameObject.getInfoBox().isVisible()) {
                gameObject.getInfoBox().setX(gameObject.getSprite().getX());
                gameObject.getInfoBox().setBottom(gameObject.getSprite().getY() - 5);
                gameObject.updateDrawList();
            }
        }
    }

    // update the positions of the game objects on the 2d grid to match their instance data
    private GameObject[][] updateGameObjectPositions(GameObject[][] board) {
        GameObject[][] newBoard = createEmptyBoard(board[0].length, board.length);
        for (int i=0; i<gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);

            // instiate game object on all cells that it covers
            for (int y=0; y<gameObject.getCellHeight(); y++)
                for (int x=0; x<gameObject.getCellWidth(); x++)
                    newBoard[gameObject.getBoardY() + y][gameObject.getBoardX() + x] = gameObject;
        }
        return newBoard;
    }
    public void updateGameObjectPositions() {
        board = updateGameObjectPositions(board);
    }

    // checks if all of the puzzle pieces are connected (win condition)
    public boolean allPuzzlePiecesConnected() {
        for (GameObject gameObject : gameObjects) {
            if (!PuzzlePiece.isPuzzlePiece(gameObject)) 
                continue;
            PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;
            if (!puzzlePiece.areAllSidesConnected())
                return false;
        }
        return true;
    }

    public int getDrawX(int boardX) {
        return boardSprite.getX() + boardX * tileSize;
    }
    public int getDrawY(int boardY) {
        return boardSprite.getY() + boardY * tileSize;
    }
    public int getBoardX(int screenX) {
        return (screenX - boardSprite.getX()) / tileSize;
    }
    public int getBoardY(int screenY) {
        return (screenY - boardSprite.getY()) / tileSize;
    }
    // removes all game object sprites 
    public void clearGameObjects() {
        for (GameObject gameObject : gameObjects) 
            gameObject.deleteSprites();
    }
}
