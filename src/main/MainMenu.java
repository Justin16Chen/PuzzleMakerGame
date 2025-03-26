package main;

import utils.ParentFrame;
import utils.input.KeyInput;
import utils.input.Mouse;

public class MainMenu extends ParentFrame {
    
    private MainMenuManager mainMenuManager;
    public MainMenu() {
        super("Level Select", 700, 500);
    }

    @Override
    public void setupWindow() {
        
        KeyInput keyInput = new KeyInput();
        Mouse mouse = new Mouse(getInsets());
        this.addKeyListener(keyInput);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

        mainMenuManager = new MainMenuManager(keyInput, mouse);
        contentPane.add(mainMenuManager);

        pack();
    }

    private void start() {
        setupWindow();
        mainMenuManager.start();
    }

    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start();
    }
}
