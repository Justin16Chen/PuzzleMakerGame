package window.levelOrganizer;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.concurrent.Flow;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import utils.input.KeyInput;
import utils.input.MouseInput;
import window.ParentFrame;

public class LevelOrganizer extends ParentFrame {

    public LevelOrganizer() {
        super("Level Organizer", 600, 600);
    }

    // setup the window
    public void setupWindow() {

        // setup content pane for window
        contentPane = this.getContentPane();
        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.setLayout(new BorderLayout());

        setResizable(false);
        setTitle(title);                                                        // title of window
        setAlwaysOnTop(true);
        

        // setup input
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput(getInsets());
        this.addKeyListener(keyInput);
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);

        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        LevelOrganizerManager levelOrganizerManager = new LevelOrganizerManager(this, keyInput, mouseInput);
        levelOrganizerManager.setPreferredSize(new Dimension(200, 500));
        
        JPanel centerContainer = new JPanel(new FlowLayout());
        centerContainer.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        centerContainer.add(levelOrganizerManager);

        contentPane.add(centerContainer, BorderLayout.CENTER);

        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, 1);
        scrollBar.setPreferredSize(new Dimension(15, 200));
        contentPane.add(scrollBar, BorderLayout.EAST);

        levelOrganizerManager.setScrollBar(scrollBar);


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                    // setup how to close the window
        this.pack();                                                            //  make sure window size is valid
        this.setVisible(true);                                                // show window
    }

    public static void main(String[] args) {
        LevelOrganizer levelOrganizer = new LevelOrganizer();
        levelOrganizer.setupWindow();
    }
}
