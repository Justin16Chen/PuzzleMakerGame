package window;

import javax.swing.*;
import java.awt.*;

public class ParentFrame extends JFrame {

    // properties of the JFrame
    protected String title;
    protected int width, height;
    protected Container contentPane;
    
    public ParentFrame(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }
    
}
