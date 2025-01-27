package levelEditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.gameObjects.GameObject;
import utils.drawing.sprites.Sprites;
import utils.input.KeyInput;
import utils.input.MouseInput;

public class LevelEditorManager extends JPanel {

    // private static ArrayList<GameObject> getDistinctGameObjects() {
    //     try {
    //         String content = new String(Files.readAllBytes(Paths.get(new File("res/levels/gameObjectData.json").toURI())));
    //         JSONObject obj = new JSONObject(content);
    //     } catch (IOException e) {
    //         System.out.println();
    //     }
    // }
    private KeyInput keyInput;
    private MouseInput mouseInput;
    private GameBoard board;

    public LevelEditorManager(KeyInput keyInput, MouseInput mouseInput) {
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;
        board = new GameBoard(KeyInput.NOTHING_INPUT, MouseInput.NOTHING_INPUT);

        start();
    }

    private void start() {
        setup();
        new Thread() {
            public void run() {

                int fps = 60;
                long sleepInterval = (long) (1000. / fps);

                while (true) {

                    keyInput.update();
                    mouseInput.update();

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
        Sprites.addLayer("default", 0);
        Sprites.addLayer("gameBoard", 1);
        Sprites.addLayer("gameObjects1", 2);
        Sprites.addLayer("gameObjects2", 3);
        Sprites.addLayer("gameObjects3", 4);
        Sprites.addLayer("effects", 5);
        Sprites.addLayer("ui", 6);
        Sprites.addLayer("debug", 6);
    }
    private void update() {

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Sprites.drawSprites(g2);
    }
}
