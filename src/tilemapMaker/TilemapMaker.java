package tilemapMaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import utils.drawing.tilemap.Tile;
import utils.input.KeyInput;
import utils.input.MouseInput;

public class TilemapMaker extends JFrame {

    private static final double TM_X_OFFSET = 0.5, TM_Y_OFFSET = 0.5, TM_WIDTH_PERCENT = 0.8, TM_SPACING_PERCENT = 0.05;
    private static final String ANYTHING_KEY = "A", FILLED_KEY = "F", EMPTY_KEY = "E";
    private static final String[] SAVE_KEYS = {"Ctrl", "S" };
    private MouseInput mouseInput;
    private KeyInput keyInput;
    private TilemapWrapper tilemap;
    private JPanel panel;
    private int tilemapCenterX, tilemapCenterY, tilemapWidth, tilemapSpacing;
    private Tile.Type currentRuleType;
    private String jsonDataPath;
    public TilemapMaker(String imagePath, String jsonDataPath) {
        this.tilemap = new TilemapWrapper(imagePath, jsonDataPath);
        this.jsonDataPath = jsonDataPath;
        currentRuleType = Tile.Type.EMPTY;
    }

    public void setup() {

        panel = new JPanel(true) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw((Graphics2D) g);
            }
        };
        panel.setPreferredSize(new Dimension(500, 500));
        add(panel);
        
        mouseInput = new MouseInput(new Insets(25, 5, 0, 0));
        addMouseListener(mouseInput);
        addMouseMotionListener(mouseInput);

        keyInput = new KeyInput();
        addKeyListener(keyInput);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public void start() {
        new Thread() {
            public void run() {

                int fps = 60;
                long sleepInterval = (long) (1000. / fps);

                while (true) {

                    // update input
                    mouseInput.update();
                    keyInput.update();

                    update();
                    SwingUtilities.invokeLater(panel::repaint);

                    try {
                        Thread.sleep(sleepInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void update() {
        tilemapCenterX = (int) (TM_X_OFFSET * getWidth());
        tilemapCenterY = (int) (TM_Y_OFFSET * getHeight());
        int size = Math.min(getWidth(), getHeight());
        tilemapWidth = (int) (TM_WIDTH_PERCENT * size);
        tilemapSpacing = (int) (TM_SPACING_PERCENT * size);

        if (keyInput.keyDown(ANYTHING_KEY))
            currentRuleType = Tile.Type.ANYTHING;
        else if (keyInput.keyDown(FILLED_KEY))
            currentRuleType = Tile.Type.FILLED;
        else if (keyInput.keyDown(EMPTY_KEY))
            currentRuleType = Tile.Type.EMPTY;
        
        if (keyInput.keyDown(SAVE_KEYS[0]) && keyInput.keyClicked(SAVE_KEYS[1]))
            saveToJsonFile(jsonDataPath);

        if (mouseInput.down())
            tilemap.setSelectedTileRule(currentRuleType, mouseInput.getX(), mouseInput.getY());
        

    }

    protected void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        tilemap.drawAllTiles(g, tilemapCenterX, tilemapCenterY, tilemapWidth, tilemapSpacing);

        g.setColor(Color.BLACK);
        g.drawString("Current rule type: " + currentRuleType, 20, 20);
        int r = 4;
        g.setColor(Color.BLACK);
        g.fillRect(mouseInput.getX() - r, mouseInput.getY() - 4, r * 2, r * 2);
        g.setColor(Color.WHITE);
        g.drawRect(mouseInput.getX() - r, mouseInput.getY() - 4, r * 2, r * 2);
    }

    private void saveToJsonFile(String filePath) {
        File file = new File(filePath);
        try {
            String fileString = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonFile = new JSONObject(fileString);
            
            jsonFile.put("rules", tilemap.getRules());
            Files.write(Paths.get(filePath), jsonFile.toString(4).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TilemapMaker window = new TilemapMaker("res/tilemaps/puzzlePieceBorderTilemap.png", "res/tilemaps/puzzlePieceBorderTilemapData.json");
        window.setup();
        window.start();
    }
}
