package gameplay;

import java.util.ArrayList;

import java.awt.*;

import javax.swing.JPanel;

import gameplay.mapLoading.LevelLoader;
import gameplay.mapLoading.LevelManager;
import utils.drawing.InfoBox;
import utils.drawing.Sprite;
import utils.input.*;
import utils.tween.*;
import window.ParentFrame;

public class GameManager extends JPanel {
    
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
    private boolean showDebug;
    private InfoBox debugInfoBox;

    // input
    KeyInput keyInput;
    MouseInput mouseInput;

    // refresh level from json keybind
    final String RELOAD_LEVEL_KEY = "1";
    final String PREV_LEVEL_KEY = "2";
    final String NEXT_LEVEL_KEY = "3";
    final String ALLOW_TRANSITION_KEY = "Space";

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
        LevelLoader.getRequiredObjectData("requiredObjectData.json");
        
        // load level
        levelManager = new LevelManager(this, gameBoard);
        levelManager.transitionToLevel(1, false, true);

        // hide info boxes to start
        debugInfoBox.hide();
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
                    Updatable.updateUpdatables(dt);

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
        gameBoard.update(dt);

        // go to next level
        if (keyInput.keyClicked(ALLOW_TRANSITION_KEY) && gameBoard.allPuzzlePiecesConnected() && !levelManager.transitioningBetweenLevels()) {
            if (levelManager.hasLevel(levelManager.getCurrentLevel() + 1)) {
                levelManager.transitionToNextLevel(true, true);
            }
            else {
                levelManager.transitionToLevel(levelManager.getCurrentLevel(), true, true);
            }
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
    }

    // swing's built in draw function for UI components
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;

        if (createdGameLoop) {
            drawGame(g2);
            Sprite.drawSprites(g2);
        }

        if (keyInput.keyClicked("Q")) {
            showDebug = !showDebug;
            if (showDebug) {
                debugInfoBox.show();
                gameBoard.showObjInfoBoxes();
            }
            else {
                debugInfoBox.hide();
                gameBoard.hideObjInfoBoxes();
            }
        }
        if (showDebug) {
            updateDebug(g2);
        }

        InfoBox.drawInfoBoxes(g2);
    }

    public void drawGame(Graphics2D g) {
        gameBoard.draw(g);
    }

    private void updateDebug(Graphics2D g) {
        ArrayList<String> drawList = new ArrayList<>();
        drawList.add("===GENERAL===");
        drawList.add("dt: " + dt);
        drawList.add("window size: (" + getWidth() + ", " + getHeight() + ")");
        drawList.add("mouse pos: (" + mouseInput.getX() + ", " + mouseInput.getY() + ")");
        drawList.add("gameManager insets: " + getInsets().left + ", " + getInsets().top);
        drawList.add("contentPane insets: " + getContentPaneInsets().left + ", " + getContentPaneInsets().top);
        drawList.add("main insets: " + window.getInsets().left + ", " + window.getInsets().top);
        drawList.add("===LEVEL===");
        drawList.add("Map Size: (" + gameBoard.getBoardWidth() + ", " + gameBoard.getBoardHeight() + ")");
        drawList.add("level succeeded: " + gameBoard.allPuzzlePiecesConnected());
        drawList.add("transitioning: " + levelManager.transitioningBetweenLevels());
        drawList.add("tile size: " + gameBoard.tileSize);
        drawList.add("game board draw pos: (" + gameBoard.getDrawX() + ", " + gameBoard.getDrawY() + ")");

        debugInfoBox.clearDrawList();
        debugInfoBox.setDrawList(drawList);
    }
}
