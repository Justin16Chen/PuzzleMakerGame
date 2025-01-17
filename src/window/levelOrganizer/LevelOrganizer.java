package window.levelOrganizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import utils.input.KeyInput;
import utils.input.MouseInput;
import window.ParentFrame;

public class LevelOrganizer extends ParentFrame {

    public LevelOrganizer() {
        super("Level Organizer", 400, 600);
    }

    // setup the window
    public void setupWindow() {
        contentPane.setLayout(new BorderLayout());

        // setup input
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput(getInsets());
        this.addKeyListener(keyInput);
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);

        JPanel northContainer = new JPanel(new FlowLayout());
        northContainer.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        JLabel title = new JLabel("Level Organizer");
        title.setFont(new Font("Arial", Font.PLAIN, 24));
        northContainer.setPreferredSize(new Dimension(100, 80));
        northContainer.add(title);
        contentPane.add(northContainer, BorderLayout.NORTH);

        LevelOrganizerManager levelOrganizerManager = new LevelOrganizerManager(keyInput, mouseInput);
        levelOrganizerManager.setPreferredSize(new Dimension(200, 480));
        
        JPanel centerContainer = new JPanel(new FlowLayout());
        centerContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        centerContainer.setPreferredSize(new Dimension(200, 510));
        centerContainer.add(levelOrganizerManager);

        contentPane.add(centerContainer, BorderLayout.CENTER);

        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, 1);
        scrollBar.setPreferredSize(new Dimension(15, 200));
        contentPane.add(scrollBar, BorderLayout.EAST);

        levelOrganizerManager.setScrollBar(scrollBar);

        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(80, 35));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                levelOrganizerManager.save();
            }
        });

        JPanel southContainer = new JPanel(new FlowLayout());
        southContainer.setPreferredSize(new Dimension(200, 70));
        southContainer.setBorder(BorderFactory.createEmptyBorder(13, 0, 0, 0));
        southContainer.add(saveButton);
        contentPane.add(southContainer, BorderLayout.SOUTH);

        pack();
    }
    
    public static void main(String[] args) {
        LevelOrganizer levelOrganizer = new LevelOrganizer();
        levelOrganizer.setupWindow();
    }
}
