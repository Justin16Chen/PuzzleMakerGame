package main;

import gameplay.*;
import utils.ParentFrame;
import utils.input.*;

public class Game extends ParentFrame {
    private GameManager gameManager;    // controls game
    private int framesPerSecond, startLevel;
    
    public Game(int framesPerSecond, int startLevel) {
        super("puzzle game", 800, 800);
        this.framesPerSecond = framesPerSecond;
        this.startLevel = startLevel;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void startGame(GameManager.GameState gameState) {
        setupWindow();
        gameManager.startEverything(startLevel, gameState);
    }

    public void setupWindow() {
        KeyInput keyInput = new KeyInput();
        Mouse mouse = new Mouse(getInsets());
        this.addKeyListener(keyInput);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

        gameManager = new GameManager(this, framesPerSecond, keyInput, mouse);
        contentPane.add(gameManager);
        gameManager.setContentPaneInsets(contentPane.getInsets());
        
        this.pack();
    }

    public static void main(String[] args) {
        Game game = new Game(60, 0);
        game.startGame(GameManager.GameState.MAIN_MENU);
    }
}