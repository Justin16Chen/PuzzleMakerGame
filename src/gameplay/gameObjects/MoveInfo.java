package gameplay.gameObjects;

// class to store information about movement
public class MoveInfo {

    public static enum Type {
        VALID,
        INVALID,
        DISCONNECT
    }

    public static MoveInfo makeValidMove(int hdir, int vdir) {
        return new MoveInfo(Type.VALID, hdir, vdir);
    }
    public static MoveInfo makeDisconnectMove() {
        return new MoveInfo(Type.DISCONNECT);
    }
    public static MoveInfo makeInvalidMove() {
        return new MoveInfo(Type.INVALID);
    }

    private Type type;
    private int hdir, vdir;
    private boolean canMove;

    private MoveInfo(Type type, int hdir, int vdir) {
        this.type = type;
        this.hdir = hdir;
        this.vdir = vdir;
        this.canMove = true;
    }
    private MoveInfo(Type type) {
        this.type = type;
        this.canMove = false;
    }

    @Override
    public String toString() {
        //if (gameObject != null) {
        //    return "MoveInfo(hdir: " + hdir + " | vdir: " + vdir + ", " + gameObject.toString() + ")";
        //}
        //return "MoveInfo(hdir: " + hdir + " | vdir: " + vdir + ", GameObject: null)";
        if (type == Type.VALID) return "MoveInfo(VALID|dir:" + hdir + "|" + vdir + ")";
        return "MoveInfo(" + type + ")";
    }

    public int getHdir() { return hdir; }
    public int getVdir() { return vdir; }
    public boolean canMove() { return canMove; }
    public Type getType() { return type; }


}
