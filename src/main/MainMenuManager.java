package main;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JPanel;

import utils.drawing.sprites.Sprites;
import utils.input.KeyInput;
import utils.input.Mouse;

public class MainMenuManager extends JPanel {
    
    public static final Color BG_COLOR = new Color(230, 230, 230);

    private KeyInput keyInput;
    private Mouse mouse;

    public MainMenuManager(KeyInput keyInput, Mouse mouse) {
        this.keyInput = keyInput;
        this.mouse = mouse;
        mouse.setInsets(new Insets(90, 197, 0, 0));
    }

    protected void start() {
        setup();
        new Thread() {
            public void run() {

                int fps = 60;
                long sleepInterval = (long) (1000. / fps);

                while (true) {

                    // update input
                    keyInput.update();
                    mouse.update();

                    update();
                    repaint();

                    try {
                        Thread.sleep(sleepInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void setup() {
        // add necessary layers
        Sprites.addLayer("bg", 0);
        Sprites.addLayer("buttons", 1);

        // create background

        // create buttons
        for (int i=0; i<3; i++) {
            for (int j=0; j<5; j++) {
                
            }
        }
    }

    private void update() {
    }

     // swing's built in draw function for UI components
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;

        // clear screen
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
