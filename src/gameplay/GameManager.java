package gameplay;

import java.awt.*;

import javax.swing.*;

import gameplay.mapLoading.*;
import input.*;
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
    public int currentLevel = 1;

    // debug
    private boolean showDebug;

    // input
    KeyInput keyInput;
    MouseInput mouseInput;

    // get the FPS and delay based off of FPS
    public int getFPS() { return fps; }
    public long getGameLoopInterval() {
        if (!hasGameLoopInterval) {
            gameLoopInterval = (long) Math.round(1000 / fps); 
            hasGameLoopInterval = true;
        }
        return gameLoopInterval; 
    }

    // game board
    GameBoard gameBoard;

    public GameManager(ParentFrame window, int framesPerSecond, KeyInput keyInput, MouseInput mouseInput) {
        this.window = window;
        this.fps = framesPerSecond;
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;
    }

    public void setLevel(int level) {

        // load in the level
        MapInfo mapInfo = GameLoader.getMapInfo(level + ".json", gameBoard);
        gameBoard.setCurrentBoard(mapInfo);
    }

    // start the game
    public void startGame() {

        // create game board
        gameBoard = new GameBoard(this, keyInput, mouseInput);

        // load level
        currentLevel = 1;
        setLevel(currentLevel);

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

        if (gameBoard.allPuzzlePiecesConnected()) {
            System.out.println("new level");
            setLevel(++currentLevel);
        }
    }

    // swing's built in draw function for UI components
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;

        if (createdGameLoop) {
            drawGame(g2);
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
        g.fillRect(0, 0, 200, 50);
        g.setColor(Color.BLACK);
        g.drawString("dt: " + dt, 20, 20);
        g.drawString("window size: " + getWidth() + ", " + getHeight(), 20, 35);
    }
}
