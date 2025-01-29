package levelEditor;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import utils.input.KeyInput;
import utils.input.MouseInput;

public class LevelEditor extends JFrame {

    LevelEditorManager levelEditorManager;

    public void setupWindow() {
        // Toolkit toolkit = Toolkit.getDefaultToolkit();
        // Image transparentImage = toolkit.createImage(new byte[0]);
        // Cursor hiddenCursor = toolkit.createCustomCursor(transparentImage, new Point(0, 0), "HiddenCursor");

        // Apply the hidden cursor to the frame
        // setCursor(hiddenCursor);
        
        setTitle("Level Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);

        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        // setup input
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput(new Insets(30, 8, 0, 0));
        addKeyListener(keyInput);
        addMouseListener(mouseInput);
        addMouseMotionListener(mouseInput);

        levelEditorManager = new LevelEditorManager(keyInput, mouseInput);
        levelEditorManager.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(levelEditorManager, JLayeredPane.DEFAULT_LAYER);
        
        JTextField boardWidthTextBox = new JTextField("5");
        boardWidthTextBox.setBounds(5, 20, 30, 15);
        layeredPane.add(boardWidthTextBox, JLayeredPane.PALETTE_LAYER);
        JTextField boardHeightTextBox = new JTextField("5");
        boardHeightTextBox.setBounds(50, 20, 30, 15);
        layeredPane.add(boardHeightTextBox, JLayeredPane.PALETTE_LAYER);
        
        setVisible(true);
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
        levelEditor.setupWindow();
        levelEditor.levelEditorManager.start();
    }
    
}
