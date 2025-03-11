package gameplay.gameObjects;

import java.awt.*;
import java.util.ArrayList;

import gameplay.gameObjects.puzzlePiece.PlayerPiece;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import gameplay.gameObjects.puzzlePiece.Side;
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
    public void setTileSize(int drawWidth, int drawHeight) { tileSize = (int) Math.min(drawWidth / board[0].length, drawHeight / board.length); }
    public int getBoardColumns() { return board[0].length; }
    public int getBoardRows() { return board.length; }

    // list of all game objects represented with a 2D grid
    private GameObject[][] board;

    // list of all game objects
    private ArrayList<GameObject> gameObjects;

    private Sprite boardSprite;
    private boolean showGridLines;
    public Sprite getBoardSprite() { return boardSprite; }
    
    public GameBoard(KeyInput keyInput, MouseInput mouseInput) {
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;

        gameObjects = new ArrayList<>();
        board = new GameObject[1][1];
        showGridLines = false;
    }

    public void setup() {
        boardSprite = new Sprite("board", 0, 0, 1, 1, "gameBoard") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(BOARD_COLOR);
                g.fillRect(getX(), getY(), getWidth(), getHeight());

                if (showGridLines) {
                    g.setColor(Color.BLACK);
                    g.setStroke(new BasicStroke(3));
                    // draw horizontal lines
                    for (int y=0; y<board.length; y++) {
                        g.drawLine(getDrawX(0), getDrawY(y), getDrawX(board[0].length), getDrawY(y));
                    }
                    // draw vertical lines
                    for (int x=0; x<board[0].length; x++) {
                        g.drawLine(getDrawX(x), getDrawY(0), getDrawX(x), getDrawY(board[0].length));
                    }
                }
            }
        };
    }

    public void toggleGridVisible() {
        showGridLines = !showGridLines;
    }

    public void printBoard() {
        System.out.print("GAME BOARD");
        for (int y=0; y<board.length; y++) {
            System.out.println();
            for (int x=0; x<board[0].length; x++) {
                if (board[y][x] != null)
                    Print.println(board[y][x] + " ", Print.BLUE);
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
        return boardX >= 0 && boardX < board[0].length && boardY >= 0 && boardY < board.length;
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
        board = new GameObject[levelInfo.getMapRows()][levelInfo.getMapCols()];
        
        // set the board for the current level
        gameObjects = levelInfo.getGameObjects();
        for (int i = 0; i < levelInfo.getGameObjects().size(); i++) {
            GameObject gameObject = levelInfo.getGameObjects().get(i);
            // instiate game object on all cells that it covers
            for (int y=0; y<gameObject.getCellHeight(); y++) 
                for (int x=0; x<gameObject.getCellWidth(); x++)
                    board[gameObject.getBoardY() + y][gameObject.getBoardX() + x] = gameObject;
        }
    }

    public void deleteGameObject(int x, int y) {
        if (!inBounds(x, y))
            throw new IllegalArgumentException(x + " " + y + " is out of bounds");
        if (board[y][x] != null)
            board[y][x].deleteSprites();
        board[y][x] = null;
        for (GameObject gameObject : gameObjects)
            if (gameObject.getBoardX() <= x && gameObject.getBoardX() + gameObject.getCellWidth() > x
            && gameObject.getBoardY() <= y && gameObject.getBoardY() + gameObject.getCellHeight() > y) {
                System.out.println("removing " + gameObject + " at position " + x + " " + y);
                gameObjects.remove(gameObject);
                break;
            }
    }
    public void printGameObjects() {
        System.out.println(gameObjects);
    }
    public void addGameObject(GameObject gameObject) {
        gameObject.updateSpriteSizeToBoard(this);
        gameObjects.add(gameObject);
        if (!inBounds(gameObject.getBoardX(), gameObject.getBoardY()))
            throw new IllegalArgumentException(gameObject + " is not in bounds of board");
        board[gameObject.getBoardY()][gameObject.getBoardX()] = gameObject;
    }

    public void setupGameObjectVisuals() {

        // update all puzzle piece connections
        for (GameObject gameObject : gameObjects) {
            if (!PuzzlePiece.isPuzzlePiece(gameObject))
                continue;
            
                PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;
                puzzlePiece.checkForConnections(this, MoveInfo.makeValidMove(0, 0), Side.ConnectType.FORCE_NO_ANIMATION);
                puzzlePiece.updateRequired(this);
        }

        // make sure gameobjects start in correct draw position
        for (GameObject gameObject : gameObjects) 
            gameObject.updateVisualsAtStart(this);
        
    }

    public void resizeAndRepositionGameObjects() {
        for (GameObject gameObject : gameObjects)
            gameObject.updateSpriteSizeToBoard(this);
    }

    public void updateBoardVisuals(int centerx, int centery, int width, int height) {
            setTileSize(width, height);
            boardSprite.setWidth(tileSize * board[0].length);
            boardSprite.setHeight(tileSize * board.length);
            boardSprite.setCenterX(centerx);
            boardSprite.setCenterY(centery);
    
            // update game object visuals once game board is set
            setupGameObjectVisuals();
    }
    // update loop
    public void update(double dt) {
        if (!allPuzzlePiecesConnected())
            checkForInput();
        
        for (GameObject gameObject : gameObjects)
            gameObject.update(this);
        
        updateDebugInfoBoxes();

    }

    private void checkForInput() {
        // get player input
        int hdir = 0;
        int vdir = 0;
        if (keyInput.keyClicked("D") || keyInput.keyClicked("Right"))
            hdir++;
        if (keyInput.keyClicked("A") || keyInput.keyClicked("Left"))
            hdir--;
        if (keyInput.keyClicked("S") || keyInput.keyClicked("Down"))
            vdir++;
        if (keyInput.keyClicked("W") || keyInput.keyClicked("Up"))
            vdir--;
        
        //int hdir = keyInput.keyClickedInt("D") - keyInput.keyClickedInt("A");
        //int vdir = keyInput.keyClickedInt("S") - keyInput.keyClickedInt("W");

        // make sure there is movement
        if (hdir != 0 || vdir != 0) {
            //Print.println("START OF ALL MOVEMENT", Print.GREEN);

            for (GameObject gameObject : gameObjects)
                gameObject.resetMovedThisFrame();
            
            // make sure game objects are positioned correctly in 2d board
            updateGameObjectPositions();
            
            // move players
            for (GameObject gameObject : gameObjects) {
                if (gameObject.getObjectType() != GameObject.ObjectType.PLAYER_PIECE)
                    continue;
                
                PlayerPiece player = (PlayerPiece) gameObject;
    
                //Print.println("start of mover movement", Print.GREEN);
                // first check if movement is valid
                ArrayList<GameObject> selfList = new ArrayList<GameObject>(); // keeps track of what has already moved
                MoveInfo moveInfo = player.getMoveInfo(this, selfList, hdir, vdir);
                
                // don't move if player already moved by something else
                if (moveInfo.canMove() && !player.movedThisFrame()) {
    
                    // disconnect any breakpoints
                    ArrayList<GameObject[]> breakpoints = MoveLogic.findBreakpoints(this, player, moveInfo);
                    MoveLogic.disconnectBreakpoints(breakpoints);
    
                    // move all connected pieces
                    //System.out.println("before pre movement check");
                    //printGameObjects();
                    player.move(this, moveInfo, true);
                }
            }
            
            // update game objects after all movement is finished
            for (GameObject gameObject : gameObjects)
                gameObject.performAfterMovement(this, MoveInfo.makeValidMove(0, 0));
            updateGameObjectPositions();
           for (GameObject gameObject : gameObjects)
                gameObject.updateTilemapToBoard(this);
        }
        if (allPuzzlePiecesConnected())
            // close all weak connections when level finished
            for (GameObject gameObject : gameObjects)
                if (PuzzlePiece.isPuzzlePiece(gameObject))
                    ((PuzzlePiece) gameObject).checkForConnections(this, MoveInfo.makeValidMove(0, 0), Side.ConnectType.FORCE_ANIMATION);
    }

    private void updateDebugInfoBoxes() {
        for (int i = 0; i < gameObjects.size(); i++) {
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
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);

            // instiate game object on all cells that it covers
            for (int y=0; y<gameObject.getCellHeight(); y++)
                for (int x=0; x<gameObject.getCellWidth(); x++)
                    newBoard[gameObject.getBoardY() + y][gameObject.getBoardX() + x] = gameObject;
        }
        return newBoard;
    }
    public void updateGameObjectPositions() {
        // System.out.println("update game object positions");
        board = updateGameObjectPositions(board);
    }

    // checks if all of the puzzle pieces are connected (win condition)
    public boolean allPuzzlePiecesConnected() {
        for (GameObject gameObject : gameObjects) {
            if (!PuzzlePiece.isPuzzlePiece(gameObject)) 
                continue;
            PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;
            if (!puzzlePiece.allSidesConnected())
                return false;
        }
        return true;
    }

    // converts board x and y to screen x and y
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
