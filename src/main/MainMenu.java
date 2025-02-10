package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.ParentFrame;
import utils.Print;

public class MainMenu extends ParentFrame {

    public final static int FRAMES_PER_SECOND = 60;
    private Game game;
    
    public MainMenu() {
        super("Level Select", 200, 200);
    }

    @Override
    public void setupWindow() {
        contentPane.setLayout(new BorderLayout());
        
        // title
        JPanel northContainer = new JPanel(new FlowLayout());
        northContainer.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        JLabel title = new JLabel("Level Select");
        title.setFont(new Font("Arial", Font.PLAIN, 24));
        northContainer.setPreferredSize(new Dimension(100, 80));
        northContainer.add(title);
        contentPane.add(northContainer, BorderLayout.NORTH);

        // level select text field
        JPanel centerContainer = new JPanel(new FlowLayout());
        centerContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        centerContainer.setPreferredSize(new Dimension(30, 50));
        JTextField textField = new JTextField(5);
        textField.setPreferredSize(new Dimension(30, 40));
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = textField.getText();
                try {
                    int level = Integer.valueOf(input);
                    if (game == null) {
                        game = new Game(FRAMES_PER_SECOND, level);
                        game.startGame();
                    } 
                    else
                        game.getGameManager().getLevelManager().transitionToLevel(level, true, true);
                    game.toFront();
                    game.requestFocus();
                    
                } catch (NumberFormatException error) {
                    Print.println(input + " is not an integer", Print.RED);
                }
                
            }
        });
        centerContainer.add(textField);
        contentPane.add(centerContainer, BorderLayout.CENTER);

        pack();
    }

    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.setupWindow();
    }
}
