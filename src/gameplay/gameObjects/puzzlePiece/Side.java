package gameplay.gameObjects.puzzlePiece;

import java.awt.Color;
import java.awt.Graphics2D;

import utils.Direction;
import utils.Print;

public class Side {
    public enum Hierarchy {
        PARENT,
        CHILD,
        EMPTY
    }
    public enum Type {
        STRONG,
        WEAK,
        NOTHING
    }
    final public static Color STRONG_COLOR = new Color(200, 180, 20);
    final public static Color WEAK_COLOR = new Color(30, 75, 220);
    final public static Color COLLECTED_WEAK_COLOR = new Color(90, 185, 255);
    final public static double DRAW_WIDTH_PERCENT = 0.15;
    // draw offsets for x and y are the same b/c cos(45) = sin(45)
    final public static double DRAW_OFF = Math.cos(Math.toRadians(45));

    public static Side[] getSideData(PuzzlePiece parent, String typeString, String baseStrengthString) {
        Side[] sideData = new Side[4];
        for (int i=0; i<4; i++) {
            Type type;
            switch (typeString.charAt(i)) {
                case 's': type = Type.STRONG; break;
                case 'w': type = Type.WEAK; break;
                case 'n': type = Type.NOTHING; break;
                default: type = Type.NOTHING; break;
            }
            sideData[i] = new Side(parent, Direction.getDirection(i), type);
        }
        return sideData;
    }

    // whether the side types can connect to each other
    public static boolean isCompatible(Side side1, Side side2) {
        return side1.getType() != Side.Type.NOTHING && side2.getType() != Side.Type.NOTHING  // neither can be a nothing type
            && !side1.isConnected() && !side2.isConnected() // both cannot already be connected
            && side1.getType() == side2.getType(); // both have to be of same type
    }

    // if two puzzle pieces are connected
    public static boolean isConnected(PuzzlePiece p1, PuzzlePiece p2) {
        int xOffset = p2.getBoardX() - p1.getBoardX(), yOffset = p2.getBoardY() - p1.getBoardY();
        if (xOffset * xOffset + yOffset * yOffset != 1) return false;
        Direction.Type oneToTwo = Direction.getDirection(xOffset, yOffset);
        Direction.Type twoToOne = Direction.getOppositeDirection(oneToTwo);
        return p1.getSide(oneToTwo).isConnected() && p2.getSide(twoToOne).isConnected();
    }
    // get the connection baseStrength given the baseStrength of two puzzle pieces
    // connection is strong if both sides are strong
    // otherwise considered weak
    public static Type getConnectionType(Type type1, Type type2) {
        if (type1 != type2 || type1 == Type.NOTHING || type2 == Type.NOTHING)
            return Type.NOTHING;
        return type1;
    }
    public static Type getConnectionType (PuzzlePiece p1, PuzzlePiece p2) {
        int xOffset = p2.getBoardX() - p1.getBoardX(), yOffset = p2.getBoardY() - p1.getBoardY();
        Direction.Type oneToTwo = Direction.getDirection(xOffset, yOffset);
        Direction.Type twoToOne = Direction.getOppositeDirection(oneToTwo);

        return getConnectionType(p1.getSide(oneToTwo).getType(), p2.getSide(twoToOne).getType());
    }

    private Direction.Type direction;
    private Hierarchy hierarchy;
    private Type type;
    private boolean connected;
    private ConnectInfo connectInfo;
    private PuzzlePiece parent;
    private PuzzlePiece piece2;
    private Side piece2Side;

    public Side(PuzzlePiece parent, Direction.Type direction, Type type) {
        this.parent = parent;
        this.direction = direction;
        this.hierarchy = Hierarchy.EMPTY;
        this.type = type;
    }

    public boolean equals(Side side) {
        return getDirection() == side.getDirection()
            && getHierarchy() == side.getHierarchy()
            && getType() == side.getType();
    }

    @Override
    public String toString() {
        return getString(0);
    }
    public String getString(int number) {
        if (connected && number == 0) return "Side(" + direction + "|" + type + "|\n  p2 pos:(" + piece2.getBoardX() + "," + piece2.getBoardY() + ")\n  p2 side:" + piece2Side.getString(1) + ")";
        return "Side(" + direction + "|" + type + "|" + hierarchy + ")";
    }

    public Direction.Type getDirection() { return direction; }
    public Hierarchy getHierarchy() { return hierarchy; }
    public Type getType() { return type; }
    public boolean isConnected() { return connected; }
    public boolean canConnect() { return type != Type.NOTHING; }
    public ConnectInfo getConnectInfo() { return connectInfo; }
    public PuzzlePiece getParent() { return parent; }
    public PuzzlePiece getPiece2() { return piece2; }
    public Side getPiece2Side() { return piece2Side; }

    public void setConnected(boolean connected) { 
        if (type != Side.Type.NOTHING) this.connected = connected; 
        else this.connected = false;

        if (!connected) {
            connectInfo = null;
            piece2 = null;
            piece2Side = null;
        }
    }
    public void setConnectInfo(ConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
        // System.out.println("SETTING CONNECT INFO");
        if (parent.equals(connectInfo.getPiece1())) {
            this.piece2 = connectInfo.getPiece2();
            this.piece2Side = connectInfo.getPiece2Side();
        } else if (parent.equals(connectInfo.getPiece2())) {
            this.piece2 = connectInfo.getPiece1();
            this.piece2Side = connectInfo.getPiece1Side();
        } else {
            Print.println("ERROR, PARENT OF " + this + " DOES NOT MATCH WITH ANY IN " + connectInfo);
        }
    }
    public void setHierarchy(Hierarchy hierarchy ) { this.hierarchy = hierarchy; }

    public void draw(Graphics2D g, int parentDrawx, int parentDrawy, int tileSize) {
        if (getType() == Type.NOTHING || (getType() == Type.STRONG && isConnected()))
            return;
        int offset = (int) Math.ceil((DRAW_OFF * DRAW_WIDTH_PERCENT * tileSize));
        int[] xList = new int[4], yList = new int[4];
        if (!isConnected())
            g.setColor(getType() == Type.STRONG ? STRONG_COLOR : WEAK_COLOR);
        else
            g.setColor(COLLECTED_WEAK_COLOR);
        switch (getDirection()) {
            case UP: 
                xList[0] = 0;                 yList[0] = 0;
                xList[1] = tileSize;          yList[1] = 0;
                xList[2] = tileSize - offset; yList[2] = offset;
                xList[3] = offset;            yList[3] = offset;
                break;
            case LEFT: 
                xList[0] = 0;      yList[0] = 0;
                xList[1] = 0;      yList[1] = tileSize;
                xList[2] = offset; yList[2] = tileSize - offset;
                xList[3] = offset; yList[3] = offset;
                break;
            case DOWN: 
                xList[0] = 0;                 yList[0] = 0;
                xList[1] = tileSize;          yList[1] = 0;
                xList[2] = tileSize - offset; yList[2] = offset;
                xList[3] = offset;            yList[3] = offset;
                for (int i=0; i<xList.length; i++) 
                    yList[i] = tileSize - yList[i];

                break;
            case RIGHT:
                xList[0] = 0;      yList[0] = 0;
                xList[1] = 0;      yList[1] = tileSize;
                xList[2] = offset; yList[2] = tileSize - offset;
                xList[3] = offset; yList[3] = offset;
                for (int i=0; i<xList.length; i++)
                    xList[i] = tileSize - xList[i];
                break;
        }
        for (int i=0; i<xList.length; i++) {
            xList[i] += parentDrawx;
            yList[i] += parentDrawy;
        }
        g.fillPolygon(xList, yList, xList.length);
    }
}
