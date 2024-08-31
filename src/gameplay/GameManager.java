package gameplay;

import java.awt.*;

import javax.swing.*;

import input.*;

public class GameManager extends JPanel {
    
    // game loop
    private int fps;
    private boolean hasGameLoopInterval;
    private int gameLoopInterval;
    protected Thread gameLoopThread;    // controls the game loop
    public boolean runningGameLoop;

    // input
    KeyInput keyInput;
    MouseInput mouseInput;

    // get the FPS and delay based off of FPS
    public int getFPS() { return fps; }
    public int getGameLoopInterval() {
        if (!hasGameLoopInterval) {
            gameLoopInterval = (int) Math.round(1000 / fps); 
            hasGameLoopInterval = true;
        }
        return gameLoopInterval; 
    }

    // game board
    GameBoard gameBoard;

    public GameManager(int framesPerSecond, KeyInput keyInput, MouseInput mouseInput) {
        this.fps = framesPerSecond;
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;
        gameBoard = new GameBoard();
    }
    
    // start the game
    public void startGame() {

        // create the game loop
        gameLoopThread = new Thread() {
            @Override
            public void run() {
                while (runningGameLoop) {

                    // update input
                    keyInput.update();
                    mouseInput.update();

                    updateGame();                                             // update function
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

    public void updateGame() {
        gameBoard.update();
    }

    // swing's built in draw function for UI components
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        drawGame(g2);

        g2.dispose();
    }

    public void drawGame(Graphics2D g) {
        gameBoard.draw(g);
    }
}
