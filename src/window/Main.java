package window;

import java.awt.*;
import javax.swing.*;

import gameplay.*;
import utils.input.*;

public class Main extends ParentFrame {

    GameManager gameManager;    // controls game
    int framesPerSecond;
    
    
    public Main(int framesPerSecond) {
        super("puzzle game", 600, 600);
        this.framesPerSecond = framesPerSecond;
    }

    // setup the window
    public void setupWindow() {

        // setup content pane for window
        contentPane = this.getContentPane();
        contentPane.setPreferredSize(new Dimension(width, height));

        setResizable(false);
        setTitle(title);                                                        // title of window
        setAlwaysOnTop(true);
        
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

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                    // setup how to close the window
        this.pack();                                                            //  make sure window size is valid
        this.setVisible(true);                                                // show window
    }

    public static void main(String[] args) {
        Main main = new Main(60);
        main.setupWindow();
        main.gameManager.startGame();
    }
}