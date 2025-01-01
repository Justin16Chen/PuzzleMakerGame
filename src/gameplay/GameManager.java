package gameplay;

import java.util.ArrayList;

import java.awt.*;

import javax.swing.JPanel;

import gameplay.gameObjects.GameObject;
import gameplay.gameObjects.MoveLogic;
import gameplay.gameObjects.puzzlePiece.Side;
import gameplay.mapLoading.LevelLoader;
import gameplay.mapLoading.LevelManager;
import utils.Print;
import utils.drawing.InfoBox;
import utils.drawing.Sprite;
import utils.input.*;
import utils.tween.*;
import window.ParentFrame;

public class GameManager extends JPanel {

    public static Color BG_COLOR = new Color(230, 230, 230);
    
    // window
    private ParentFrame window;
    private Insets contentPaneInsets;
    
    // game loop
    private int fps;
    private boolean hasGameLoopInterval;
    private long gameLoopInterval;
    protected Thread gameLoopThread;    // controls the game loop
    public boolean createdGameLoop;
    public boolean runningGameLoop;
    public double dt;

    // level management
    LevelManager levelManager;

    // debug
    public boolean showDebug;
    private InfoBox debugInfoBox;

    // input
    KeyInput keyInput;
    MouseInput mouseInput;

    // refresh level from json keybind
    final String RELOAD_LEVEL_KEY = "1";
    final String PREV_LEVEL_KEY = "2";
    final String NEXT_LEVEL_KEY = "3";
    final String ALLOW_TRANSITION_KEY = "Space";
    final String PRINT_UPDATABLES_KEY = "U";

    // debug MoveLogic.java IN PROGRESS
    private int hdir = 0, vdir = 0;
    private ArrayList<GameObject> breakpointBoundaries = new ArrayList<>();
    private ArrayList<Side> breakpoints = new ArrayList<>();

    // get the FPS and delay based off of FPS
    public int getFPS() { return fps; }
    public long getGameLoopInterval() {
        if (!hasGameLoopInterval) {
            gameLoopInterval = (long) Math.round(1000 / fps); 
            hasGameLoopInterval = true;
        }
        return gameLoopInterval; 
    }
    
    // get window size
    public int getWindowWidth() {
        return window.getWidth();
    }
    public int getWindowHeight() {
        return window.getHeight();
    }
    public Insets getContentPaneInsets() {
        return contentPaneInsets;
    }
    public void setContentPaneInsets(Insets insets) {
        contentPaneInsets = insets;
    }

    // game board
    GameBoard gameBoard;

    public GameManager(ParentFrame window, int framesPerSecond, KeyInput keyInput, MouseInput mouseInput) {
        this.window = window;
        this.fps = framesPerSecond;
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;
    }

    // start the game
    public void startGame() {

        // setup info boxes
        InfoBox.setGameManager(this);

        // debug info box drawer
        debugInfoBox = InfoBox.createInfoBox();
        debugInfoBox.setPos(0, 0);
        debugInfoBox.setOffsets(0, 0);

        // create game board
        gameBoard = new GameBoard(this, keyInput, mouseInput);

        // setup base level properties
        LevelLoader.getObjectData("requiredObjectData.json");
        
        // load level
        levelManager = new LevelManager(this, gameBoard);
        levelManager.transitionToLevel(0, false, true);

        // hide info boxes to start
        gameBoard.hideObjInfoBoxes();
        
        // create and start the game loop
        createGameLoop();
        startGameLoop();
    }

    // create the game loop
    public void createGameLoop() {
        gameLoopThread = new Thread() {
            @Override
            public void run() {

                long prevTime = System.currentTimeMillis();
                long currentTime = System.currentTimeMillis();

                while (runningGameLoop) {
                    prevTime = currentTime;
                    currentTime = System.currentTimeMillis();
                    dt = (currentTime - prevTime) / 1000.;

                    // update input
                    keyInput.update();
                    mouseInput.update();
                    mouseInput.setInsets(window.getInsets());

                    // update background systems
                    Updatables.updateUpdatables(dt);

                    updateGame(dt);                                             // update function
                    repaint();                                                // draw function

                    // delay between current frame and next frame
                    try {
                        Thread.sleep(getGameLoopInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        stopGameLoop();
                    }
                }
            }
        };
        createdGameLoop = true;
    }
    
    // start the game loop
    public void startGameLoop() {
        runningGameLoop = true;
        gameLoopThread.start();
    }

    // stop the game loop
    public void stopGameLoop() {
        runningGameLoop = false;
    }

    public void updateGame(double dt) {

        // IN PROGRESS: this should be happening in player update function before movement
        int playerx = 0, playery = 0;
        for (GameObject gameObject : gameBoard.getGameObjects()) 
            if (gameObject.getObjectType() == GameObject.ObjectType.PLAYER_PIECE) {
                playerx = gameObject.getBoardX();
                playery = gameObject.getBoardY();
            }
        
        // update game object indecies
        MoveLogic.updateMoveIndecies(gameBoard, playerx, playery);

        gameBoard.update(dt);

        // go to next level
        if (keyInput.keyClicked(ALLOW_TRANSITION_KEY) && gameBoard.allPuzzlePiecesConnected() && !levelManager.transitioningBetweenLevels()) {
            if (levelManager.hasLevel(levelManager.getCurrentLevel() + 1)) 
                levelManager.transitionToNextLevel(true, true);
            
        }

        // IN PROGRESS: testing MoveLogic.java
        if (keyInput.keyClicked("Minus")) {
            if (++hdir == 2) hdir = -1;
        }
        if (keyInput.keyClicked("Equals")) {
            if (++vdir == 2) vdir = -1;
        }
        if (keyInput.keyClicked("P")) {
            boolean canMove = MoveLogic.canObjectsMove(gameBoard, gameBoard.getGameObjects(), hdir, vdir);
            Print.println("can all game objects move: " + canMove, Print.YELLOW);
        }
        // refresh map data from json files
        if (keyInput.keyClicked(RELOAD_LEVEL_KEY)) {
            levelManager.updateGeneralLevelInfo();
            levelManager.updateLevelInfo();
        }

        // keybinds to move through levels
        if (keyInput.keyClicked(PREV_LEVEL_KEY)) {
            levelManager.transitionToLevel(levelManager.getCurrentLevel() - 1, true, true);
        }
        if (keyInput.keyClicked(NEXT_LEVEL_KEY)) {
            levelManager.transitionToNextLevel(true, true);
        }

        if (keyInput.keyClicked("Q")) {
            showDebug = !showDebug;
            if (showDebug) 
                debugInfoBox.show();
            else 
                debugInfoBox.hide();
        }

        Updatables.setAllowPrint(keyInput.keyDown(PRINT_UPDATABLES_KEY));
    }

    // swing's built in draw function for UI components
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;

        if (createdGameLoop) {
            drawGame(g2);
            Sprite.drawSprites(g2);
        }
        if (showDebug) {
            updateDebug(g2);
        }

        InfoBox.drawInfoBoxes(g2);
    }

    public void drawGame(Graphics2D g) {
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, getWindowWidth(), getWindowHeight());
        gameBoard.draw(g);
    }

    private void updateDebug(Graphics2D g) {

    ArrayList<String> drawList = new ArrayList<>();
        addDebugGeneral(drawList);
        //addDebugInput(drawList); // more advanced stuff, separated
        addDebugControls(drawList);
        addDebugUpdatables(drawList);
        addDebugLevel(drawList);
        //addDebugGameObjects(drawList);
        //addDebugMovement(drawList);

        debugInfoBox.clearDrawList();
        debugInfoBox.setDrawList(drawList);
    }

    private void addDebugGeneral(ArrayList<String> drawList) {
        drawList.add("===GENERAL===");
        drawList.add("dt: " + dt);
        drawList.add("window size: (" + getWidth() + ", " + getHeight() + ")");
         drawList.add("mouse pos: (" + mouseInput.getX() + ", " + mouseInput.getY() + ")");
        drawList.add("gameManager insets: " + getInsets().left + ", " + getInsets().top);
        drawList.add("contentPane insets: " + getContentPaneInsets().left + ", " + getContentPaneInsets().top);
        drawList.add("main insets: " + window.getInsets().left + ", " + window.getInsets().top);
    }
    private void addDebugInput(ArrayList<String> drawList) {
        drawList.add("===INPUT===");
        InputBase.State[] states = { 
            InputBase.State.DOWN, 
            InputBase.State.CLICKED, 
            InputBase.State.RELEASED 
        };
        for (InputBase.State state : states) {
            ArrayList<String> keyList = keyInput.getAllKeys(state);
            if (keyList.size() > 0) {
                String keyString = "";
                for (String keyName : keyList) keyString += keyName + ", ";
                keyString = keyString.substring(0, keyString.length() - 2);
                drawList.add("keys " + state.toString().toLowerCase() + ": " + keyString);
            } else {
                drawList.add("keys " + state.toString().toLowerCase() + ": none");
            }
        }
    }
    private void addDebugUpdatables(ArrayList<String> drawList) {
        drawList.add("===UPDATABLES===");
        drawList.add("amount of updatables: " + Updatables.getUpdatableAmount());
        for (Updatable updatable : Updatables.getUpdatables()) 
            drawList.add("" + updatable.getName());
        
    }
    private void addDebugControls(ArrayList<String> drawList) {
        drawList.add("===CONTROLS===");
        drawList.add("Q: toggle debug info");
        drawList.add("U: print updatables");
        drawList.add("1: refresh level json file");
        drawList.add("2: go to prev lvl");
        drawList.add("3: go to next lvl");
        drawList.add("Space: transition to next level once completed");
    }
    private void addDebugLevel(ArrayList<String> drawList) {
        drawList.add("===LEVEL===");
        drawList.add("current level: " + levelManager.getCurrentLevel());
        drawList.add("Map Size: (" + gameBoard.getBoardWidth() + ", " + gameBoard.getBoardHeight() + ")");
        drawList.add("level succeeded: " + gameBoard.allPuzzlePiecesConnected());
        drawList.add("transitioning: " + levelManager.transitioningBetweenLevels());
        drawList.add("tile size: " + gameBoard.tileSize);
        drawList.add("game board draw pos: (" + gameBoard.getDrawX() + ", " + gameBoard.getDrawY() + ")");
    }
    private void addDebugGameObjects(ArrayList<String> drawList) {
        drawList.add("===GAME OBJECTS===");
        drawList.add("number: " + gameBoard.getGameObjects().size());
        for (GameObject gameObject : gameBoard.getGameObjects()) {
            drawList.add("object: " + gameObject.getName() + " | pos: (" + gameObject.getBoardX() + ", " + gameObject.getBoardY() + ")");
        }
    }
    private void addDebugMovement(ArrayList<String> drawList) {
        drawList.add("===MOVEMENT===");
        drawList.add("dir to check: (" + hdir + ", " + vdir + ")");
        drawList.add("breakpoint boundaries: ");
        for (GameObject gameObject : breakpointBoundaries)
            drawList.add("index: " + gameObject.getMoveIndex() + " | game object: " + gameObject);
        if (breakpoints != null) {
            drawList.add("breakpoints: ");
            for (Side side : breakpoints)
                drawList.add("breakpoint side: " + side);
        }
        else 
            drawList.add("breakpoints: none - invalid movement");
    }
}
