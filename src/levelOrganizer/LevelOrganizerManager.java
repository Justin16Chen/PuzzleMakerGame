package levelOrganizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.json.JSONObject;

import utils.Print;
import utils.input.KeyInput;
import utils.input.MouseInput;

public class LevelOrganizerManager extends JPanel {
    
    public static final String LEVELS_FOLDER_PATH = "res/levels/";
    public static final Color BG_COLOR = new Color(230, 230, 230), SELECTED_COLOR = new Color(200, 200, 200), REPLACE_COLOR = new Color(160, 160, 160);
    public static final Font FONT = new Font("Arial", Font.PLAIN, 18);
    public static final int OFFSET_X = 25, OFFSET_Y = 25, DRAG_OFFSET_Y = -15, REPLACE_STROKE_WIDTH = 4;;
    public static final double SPACING_PERCENT = 0.7;

    private JScrollBar scrollBar;
    private KeyInput keyInput;
    private MouseInput mouseInput;

    private ArrayList<String> levelFileNames;
    private int startLevelFileIndex;
    private int selectedIndex, replaceIndex;
    private String selectedFileName;
    private boolean draggingSelectedFile;

    private FontMetrics fontMetrics;
    private int offset, spacing;

    public LevelOrganizerManager(KeyInput keyInput, MouseInput mouseInput) {
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;
        mouseInput.setInsets(new Insets(90, 197, 0, 0));
        levelFileNames = loadLevelFileNames("res/properties/levelInfo.json");

        start();
    }

    public void setScrollBar(JScrollBar scrollBar) {
        this.scrollBar = scrollBar;
    }

    private void start() {
        new Thread() {
            public void run() {

                int fps = 60;
                long sleepInterval = (long) (1000. / fps);

                while (true) {

                    // update input
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


    private void update() {
        if (scrollBar != null)
            scrollBar.setMaximum(levelFileNames.size());
        
        if (spacing == 0)
            return;
        if (mouseInput.getX() >= getWidth() || mouseInput.getY() >= getHeight()) 
            return;
        int index = getFileIndex(mouseInput.getY());
        if (index < -1 || index >= levelFileNames.size())
            return;

        if (mouseInput.clicked() && index >= 0) {
            selectedIndex = index;
            selectedFileName = levelFileNames.get(selectedIndex);
            draggingSelectedFile = true;
        }
        int dragIndex = mouseInput.getY() < OFFSET_Y + spacing * 0.5 ? 0 : getFileIndex(mouseInput.getY() + DRAG_OFFSET_Y) + 1;
        if (!mouseInput.down()) {
            if (draggingSelectedFile) {
                levelFileNames.remove(selectedIndex);
                if (dragIndex >= selectedIndex)
                    dragIndex--;
                levelFileNames.add(dragIndex, selectedFileName);
                selectedIndex = dragIndex;
            }
            draggingSelectedFile = false;
        }
        if (draggingSelectedFile)
            replaceIndex = dragIndex;
        
    }

    // gets the file names of all level files 
    private ArrayList<String> loadLevelFileNames(String levelInfoFilePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(new File(levelInfoFilePath).toURI())));
            JSONObject obj = new JSONObject(content);
            ArrayList<String> fileNames = new ArrayList<String>();
            int startLevel, endLevel;

            startLevel = obj.getInt("startLevel");
            endLevel = obj.getInt("endLevel");

            startLevelFileIndex = startLevel; // bruh bad code

            for (int i = startLevel; i <= endLevel; i++) 
                fileNames.add(i + ".json");
            
            return fileNames;
            
        } catch (IOException e) {
            Print.println(levelInfoFilePath + " not found");
            e.printStackTrace();
        }
        return null;
    }

    // gets the drawY of the file at an index
    public int getFileDrawY(int i) {
        return (int) (offset + spacing * i - spacing * SPACING_PERCENT);
    }
    public int getFileIndex(int y) {
        return (y - offset) / spacing;
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

        // update draw params
        fontMetrics = g2.getFontMetrics(FONT);
        spacing = fontMetrics.getHeight() + 10;
        offset = OFFSET_Y - scrollBar.getValue() * spacing;

        // draw names of file
        drawFileNames(g2);
    }

    private void drawFileNames(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(FONT);
        for (int i = 0; i < levelFileNames.size(); i++) {
            if (i == selectedIndex) {
                g.setColor(SELECTED_COLOR);
                g.fillRect(0, getFileDrawY(i), getWidth(), spacing);

                // if mouse is down on selected file, do not draw in list
                if (!draggingSelectedFile) {
                        g.setColor(Color.BLACK);
                        g.drawString(levelFileNames.get(i), OFFSET_X, offset + spacing * i);
                }
            }
            else {
                if (i == replaceIndex) {
                    g.setColor(REPLACE_COLOR);
                    g.setStroke(new BasicStroke(REPLACE_STROKE_WIDTH));
                    g.drawLine(0, getFileDrawY(i), getWidth(), getFileDrawY(i));
                }
                g.setColor(Color.BLACK);
                g.drawString(levelFileNames.get(i), OFFSET_X, offset + spacing * i);
            }
        }

        // draw selected file being dragged by mouse
        if (draggingSelectedFile) {
            g.setColor(SELECTED_COLOR);
            g.fillRect(0, (int) (mouseInput.getY() - spacing * SPACING_PERCENT + DRAG_OFFSET_Y), fontMetrics.stringWidth(selectedFileName) + OFFSET_X * 2, spacing);
            g.setColor(Color.BLACK);
            g.drawString(selectedFileName, OFFSET_X, mouseInput.getY() + DRAG_OFFSET_Y);
        }
    }

    public void save() {
        // save them + temp so there are no errors w mutliple files w the same name
        for (int i = 0; i < levelFileNames.size(); i++) {
            int levelNum = startLevelFileIndex + i;
            File levelFile = new File(LEVELS_FOLDER_PATH + levelFileNames.get(levelNum));
            levelFile.renameTo(new File(LEVELS_FOLDER_PATH + "temp " + levelNum + ".json"));
        }
        
        for (int i = 0; i < levelFileNames.size(); i++) {
            int levelNum = startLevelFileIndex + i;
            
            File levelFile = new File(LEVELS_FOLDER_PATH + "temp " + levelNum + ".json");
            levelFile.renameTo(new File(LEVELS_FOLDER_PATH + levelNum + ".json"));
        }

        levelFileNames = loadLevelFileNames("res/properties/levelInfo.json");

    }
}
