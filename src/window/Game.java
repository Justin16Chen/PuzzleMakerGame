package window;

import javax.swing.*;

import gameplay.*;
import utils.input.*;

public class Game extends ParentFrame {

    private GameManager gameManager;    // controls game
    private int framesPerSecond, startLevel;
    
    public Game(int framesPerSecond, int startLevel) {
        super("puzzle game", 600, 600);
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

    // setup the window
    public void setupWindow() {

        
        
        // setup input
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput(getInsets());
        this.addKeyListener(keyInput);
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);

        // setup and add the game manager (JPanel)
        gameManager = new GameManager(this, framesPerSecond, keyInput, mouseInput);
        contentPane.add(gameManager);
        gameManager.setContentPaneInsets(contentPane.getInsets());
        
        // setup how to close the window
        this.pack();                                                           // show window
    }

    public static void main(String[] args) {
        Game game = new Game(60, 0);
        game.startGame();
    }
}