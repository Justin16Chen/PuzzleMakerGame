package gameplay;

import java.awt.*;

import javax.swing.JPanel;

import input.*;
import utils.drawing.Sprite;
import utils.tween.*;
import window.ParentFrame;

public class GameManager extends JPanel {
    
    // window
    private ParentFrame window;
    
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

    // input
    KeyInput keyInput;
    MouseInput mouseInput;

    // refresh level from json keybind
    private boolean refreshLevel = false;
    final String RELOAD_LEVEL_KEY = "1";
    private Timer refreshTimer;

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

        // create game board
        gameBoard = new GameBoard(this, keyInput, mouseInput);

        // load level
        levelManager = new LevelManager(this, gameBoard);
        levelManager.transitionToLevel(1, false, true);

        // create timer to refresh levels from json
        refreshTimer = Timer.createCallTimer("refresh level from json", levelManager, 1, "updateLevelInfo", -1);
        refreshTimer.setPrint(Updatable.PrintType.ON_LOOP);
        refreshTimer.setPaused(true);
        
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
        if (gameBoard.allPuzzlePiecesConnected() && !levelManager.transitioningBetweenLevels()) {
            levelManager.transitionToNextLevel(true, true);
        }

        // refresh map data from json files
        if (keyInput.keyClicked(RELOAD_LEVEL_KEY)) {
            refreshLevel = !refreshLevel;
            refreshTimer.setPaused(!refreshLevel);
            refreshTimer.setElapsedTime(refreshTimer.getDuration());
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
        }
        if (showDebug) {
            drawDebug(g2);
        }
    }

    public void drawGame(Graphics2D g) {
        gameBoard.draw(g);
    }

    private void drawDebug(Graphics2D g) {
        g.setColor(new Color(180, 180, 180, 140));
        g.fillRect(0, 0, 200, 300);
        g.setColor(Color.BLACK);
        g.drawString("===GENERAL===", 20, 20);
        g.drawString("dt: " + dt, 20, 40);
        g.drawString("window size: (" + getWidth() + ", " + getHeight() + ")", 20, 55);
        g.drawString("===LEVEL===", 20, 70);
        g.drawString("Map Size: (" + gameBoard.getBoardWidth() + ", " + gameBoard.getBoardHeight() + ")", 20, 90);
        g.drawString("level succeeded: " + gameBoard.allPuzzlePiecesConnected(), 20, 105);
        g.drawString("transitioning: " + levelManager.transitioningBetweenLevels(), 20, 120);
        g.drawString("refreshing level: " + refreshLevel, 20, 135);
        for (int i=0; i<gameBoard.getGameObjects().size(); i++) {

        }
    }
}
