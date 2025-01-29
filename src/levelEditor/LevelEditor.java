package levelEditor;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import utils.input.KeyInput;
import utils.input.MouseInput;

public class LevelEditor extends JFrame {

    LevelEditorManager levelEditorManager;

    public void setupWindow() {
        
        setTitle("Level Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        // setup input
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput(getInsets());
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
