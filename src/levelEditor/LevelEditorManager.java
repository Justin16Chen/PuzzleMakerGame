package levelEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.gameObjects.GameObject;
import gameplay.mapLoading.LevelInfo;
import gameplay.mapLoading.LevelLoader;
import utils.Print;
import utils.drawing.sprites.Sprites;
import utils.input.KeyInput;
import utils.input.MouseInput;
import utils.tween.Updatables;

public class LevelEditorManager extends JPanel {

    private final static int BOARD_X = 350, BOARD_Y = 300, BOARD_SIZE = 350;
    private final static int PANEL_WIDTH = 120, PANEL_HEIGHT = 600;
    private final static Color PANEL_COLOR = new Color(30, 30, 30);
    private KeyInput keyInput;
    private MouseInput mouseInput;
    private GameBoard board;
    private Panel panel;
    private ArrayList<GameObject> gameObjectOptions;

    public LevelEditorManager(KeyInput keyInput, MouseInput mouseInput) {
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;
        panel = new Panel(0, 0, PANEL_WIDTH, PANEL_HEIGHT, PANEL_COLOR, mouseInput);
        board = new GameBoard(KeyInput.NOTHING_INPUT, MouseInput.NOTHING_INPUT);

        // get what the user can place down
        gameObjectOptions = getDistinctGameObjects();
    }

    private ArrayList<GameObject> getDistinctGameObjects() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(new File("res/levels/gameObjectData.json").toURI())));
            JSONObject obj = new JSONObject(content);
            JSONArray distinctJsonGameObjects = obj.getJSONArray("defaultGameObjects");
            ArrayList<GameObject> distinctGameObjects = new ArrayList<GameObject>();
            for (int i=0; i<distinctJsonGameObjects.length(); i++) 
                distinctGameObjects.add(LevelLoader.createGameObject(distinctJsonGameObjects.getJSONObject(i), null));
            
            return distinctGameObjects;
        } catch (IOException e) {
            Print.println("CANNOT FIND FILE " + "res/levels/gameObjectData.json", Print.RED);
        }
        return null;
    }

    public void start() {
        setup();
        new Thread() {
            public void run() {

                int fps = 60;
                long sleepInterval = (long) (1000. / fps);

                long prevTime = System.currentTimeMillis();
                long currentTime = System.currentTimeMillis();
                double dt = 0;

                while (true) {
                    prevTime = currentTime;
                    currentTime = System.currentTimeMillis();
                    dt = (currentTime - prevTime) / 1000.;

                    keyInput.update();
                    mouseInput.update();

                    Updatables.updateUpdatables(dt);
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
        Sprites.addLayer("ui2", 7);
        Sprites.addLayer("debug", 8);

        board.setup();
        board.setCurrentBoard(new LevelInfo(10, 10, new ArrayList<GameObject>()));
        board.updateBoardVisuals(BOARD_X, BOARD_Y, BOARD_SIZE, BOARD_SIZE);
        
        panel.setOptions(gameObjectOptions);
        panel.setup();
    }
    private void update() {

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, getWidth(), getHeight());
        Sprites.drawSprites(g2);
        
        // draw cursor
        // g2.setColor(Color.BLACK);
        // g2.drawArc(mouseInput.getX(), mouseInput.getY(), 5, 5, 0, 360);
    }
}
