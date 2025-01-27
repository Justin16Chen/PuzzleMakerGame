package levelEditor;

import utils.ParentFrame;
import utils.input.KeyInput;
import utils.input.MouseInput;

public class LevelEditor extends ParentFrame {

    LevelEditorManager levelEditorManager;

    public LevelEditor() {
        super("Level Editor", 600, 600);
    }

    @Override
    public void setupWindow() {
        
        // setup input
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput(getInsets());
        addKeyListener(keyInput);
        addMouseListener(mouseInput);
        addMouseMotionListener(mouseInput);

        levelEditorManager = new LevelEditorManager(keyInput, mouseInput);
        contentPane.add(levelEditorManager);
        
        pack();
    }

    public static void main(String[] args) {
        LevelEditor levelEditor = new LevelEditor();
        levelEditor.setupWindow();
    }
    
}
