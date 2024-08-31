package input;

public class InputBase {
    private int downTime = 0;
    private int upTime = 0;
    private boolean down = false;
    
    public void update() {
        if (down) {
            downTime++;
        }
        else {
            upTime++;
        }
    }

    public void setDown(boolean down) {
        this.down = down;
        if (down) {
            upTime = 0;
        }
        else {
            downTime = 0;
        }
    }

    public boolean down() {
        return down;
    }
    public boolean clicked() {
        return downTime == 1;
    }
    public boolean released() {
        return upTime == 1;
    }
}
