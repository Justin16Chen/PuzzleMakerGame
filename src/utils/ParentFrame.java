package utils;

import javax.swing.*;
import java.awt.*;

public abstract class ParentFrame extends JFrame {

    // properties of the JFrame
    protected String title;
    protected int width, height;
    protected Container contentPane;
    
    public ParentFrame(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        // setup content pane for window
        contentPane = getContentPane();
        contentPane.setPreferredSize(new Dimension(width, height));

        setTitle(title);
        setAlwaysOnTop(true);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);      
    }
    
    public abstract void setupWindow();
}
