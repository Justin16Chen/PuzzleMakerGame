package tilemapMaker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import utils.drawing.tilemap.Tilemap;
import utils.input.MouseInput;

public class TilemapMaker extends JFrame {

    private static final double TM_X_OFFSET = 0.5, TM_Y_OFFSET = 0.5, TM_WIDTH_PERCENT = 0.8, TM_SPACING_PERCENT = 0.05;

    private MouseInput mouseInput;
    private Tilemap tilemap;
    private JPanel panel;
    public TilemapMaker(String tilemapName, String imagePath, String jsonDataPath) {
        this.tilemap = new Tilemap(tilemapName, imagePath, jsonDataPath);
    }

    public void setup() {
        mouseInput = new MouseInput(getInsets());
        addMouseListener(mouseInput);
        addMouseMotionListener(mouseInput);

        panel = new JPanel(true) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw((Graphics2D) g);
            }
        };
        panel.setPreferredSize(new Dimension(500, 500));
        add(panel);
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

    }

    protected void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        int cx = (int) (TM_X_OFFSET * getWidth());
        int cy = (int) (TM_Y_OFFSET * getHeight());
        int size = Math.min(getWidth(), getHeight());
        int w = (int) (TM_WIDTH_PERCENT * size);
        int spacing = (int) (TM_SPACING_PERCENT * size);
        tilemap.drawAllTiles(g, cx, cy, w, spacing);
    }

    public static void main(String[] args) {
        TilemapMaker window = new TilemapMaker("puzzlePiece", "res/tilemaps/puzzlePieceTestSpritesheet.png", "res/tilemaps/puzzlePieceTilemap.json");
        window.setup();
        window.start();
    }
}
