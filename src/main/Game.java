package main;

import gameplay.*;
import utils.ParentFrame;
import utils.input.*;

public class Game extends ParentFrame {

    private GameManager gameManager;    // controls game
    private int framesPerSecond, startLevel;
    
    public Game(int framesPerSecond, int startLevel) {
        super("puzzle game", 1000, 1000);
        this.framesPerSecond = framesPerSecond;
        this.startLevel = startLevel;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void startGame() {
        setupWindow();
        gameManager.startGame(startLevel);
    }

    public void setupWindow() {
        
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput(getInsets());
        this.addKeyListener(keyInput);
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);

        gameManager = new GameManager(this, framesPerSecond, keyInput, mouseInput);
        contentPane.add(gameManager);
        gameManager.setContentPaneInsets(contentPane.getInsets());
        
        this.pack();
    }

    public static void main(String[] args) {
        Game game = new Game(60, 0);
        game.startGame();
    }
}