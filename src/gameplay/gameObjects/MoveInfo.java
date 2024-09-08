package gameplay.gameObjects;

// class to store information about movement
public class MoveInfo {

    public static MoveInfo makeValidMove(int hdir, int vdir) {
        return new MoveInfo(hdir, vdir, true);
    }
    public static MoveInfo makeInvalidMove() {
        return new MoveInfo();
    }

    private int hdir, vdir;
    private boolean canMove;

    private MoveInfo(int hdir, int vdir, boolean canMove) {
        this.hdir = hdir;
        this.vdir = vdir;
        this.canMove = canMove;
    }
    private MoveInfo() {
        this.canMove = false;
    }

    @Override
    public String toString() {
        //if (gameObject != null) {
        //    return "MoveInfo(hdir: " + hdir + " | vdir: " + vdir + ", " + gameObject.toString() + ")";
        //}
        //return "MoveInfo(hdir: " + hdir + " | vdir: " + vdir + ", GameObject: null)";
        return "MoveInfo(hdir: " + hdir + " | vdir: " + vdir + ")";
    }

    public int getHdir() { return hdir; }
    public int getVdir() { return vdir; }
    public boolean canMove() { return canMove; }


}
