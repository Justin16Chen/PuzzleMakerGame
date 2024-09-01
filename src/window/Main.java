package window;

import java.awt.*;
import javax.swing.*;

import gameplay.*;
import input.*;

public class Main extends ParentFrame {

    GameManager gameManager;    // controls game
    
    
    public Main(int framePerSecond) {
        super("puzzle game", 960, 640);
    }

    // setup the window
    public void setupWindow() {

        // setup content pane for window
        contentPane = this.getContentPane();
        contentPane.setPreferredSize(new Dimension(width, height));

        setTitle(title);                                                        // title of window

        // setup input
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput();
        this.addKeyListener(keyInput);
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);

        // setup and add the game manager (JPanel)
        gameManager = new GameManager(60, keyInput, mouseInput);
        contentPane.add(gameManager);

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