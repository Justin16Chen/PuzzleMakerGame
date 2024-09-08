package gameplay;

import java.awt.*;

import javax.swing.*;

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
    public boolean runningGameLoop;
    public double dt;

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
        gameBoard = new GameBoard(this, keyInput, mouseInput);
    }

    // get the size of the window
    public int getWindowWidth() {
        return getWidth();
    }
    public int getWindowHeight() {
        return getHeight();
    }
    
    // start the game
    public void startGame() {

        // create the game loop
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
        startGameLoop();
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
    }

    // swing's built in draw function for UI components
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;

        drawGame(g2);

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
        g.drawString("window size: " + getWindowWidth() + ", " + getWindowHeight(), 20, 35);
    }
}
