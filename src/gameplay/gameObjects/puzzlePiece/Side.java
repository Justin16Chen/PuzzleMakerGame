package gameplay.gameObjects.puzzlePiece;

import java.awt.Color;
import java.awt.Graphics2D;

import utils.JMath;
import utils.Print;
import utils.direction.Direction;
import utils.direction.Directions;
import utils.tween.EaseType;
import utils.tween.Tween;
import utils.tween.Updatable;

public class Side {
    public enum Type {
        STRONG,
        WEAK,
        NOTHING
    }
    final public static Color STRONG_COLOR = new Color(200, 180, 20);
    final public static Color WEAK_COLOR = new Color(30, 75, 220);
    final public static Color CONNECTED_STRONG_COLOR = new Color(250, 245, 150);
    final public static Color CONNECTED_WEAK_COLOR = new Color(90, 185, 255);
    final public static double DRAW_WIDTH_PERCENT = 0.15;
    // draw offsets for x and y are the same b/c cos(45) = sin(45)
    final public static double DRAW_OFF = Math.cos(Math.toRadians(45));

    // returns a list of sides based on string (from json file)
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
            sideData[i] = new Side(parent, Directions.getDirection(i), type);
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
        Direction oneToTwo = Directions.getDirection(xOffset, yOffset);
        Direction twoToOne = Directions.getOppositeDirection(oneToTwo);
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
        Direction oneToTwo = Directions.getDirection(xOffset, yOffset);
        Direction twoToOne = Directions.getOppositeDirection(oneToTwo);

        return getConnectionType(p1.getSide(oneToTwo).getType(), p2.getSide(twoToOne).getType());
    }

    private Direction direction;
    private Type type;
    private boolean connected;
    private ConnectInfo connectInfo;
    private PuzzlePiece parent;
    private PuzzlePiece piece2;
    private Side piece2Side;
    private double tweenPercent; // plays animation based on this when connecting w another side

    public Side(PuzzlePiece parent, Direction direction, Type type) {
        this.parent = parent;
        this.direction = direction;
        this.type = type;
    }

    public boolean equals(Side side) {
        return getDirection() == side.getDirection()
            && getType() == side.getType();
    }

    @Override
    public String toString() {
        return getString(0);
    }
    public String getString(int number) {
        if (connected && number == 0) return "Side(" + direction + "|" + type + "|\n  p2 pos:(" + piece2.getBoardX() + "," + piece2.getBoardY() + ")\n  p2 side:" + piece2Side.getString(1) + ")";
        return "Side(" + direction + "|" + type + "tweenAmount: " + tweenPercent + ")";
    }

    public Direction getDirection() { return direction; }
    public Type getType() { return type; }
    public boolean isConnected() { return connected; }
    public boolean canConnect() { return type != Type.NOTHING; }
    public ConnectInfo getConnectInfo() { return connectInfo; }
    public PuzzlePiece getParent() { return parent; }
    public PuzzlePiece getPiece2() { return piece2; }
    public Side getPiece2Side() { return piece2Side; }

    public void disconnect() { 
        if (!connected)
            throw new IllegalCallerException("cannot disconnect a side that is not connected");
        connected = false;
        connectInfo = null;
        piece2 = null;
        piece2Side = null;
            
    }
    public void connect(ConnectInfo connectInfo) {
        if (connected)
            throw new IllegalCallerException("cannot connect a side that is already connected");
        connected = true;
        this.connectInfo = connectInfo;
        
        if (parent.equals(connectInfo.getPiece1())) {
            this.piece2 = connectInfo.getPiece2();
            this.piece2Side = connectInfo.getPiece2Side();
        } else if (parent.equals(connectInfo.getPiece2())) {
            this.piece2 = connectInfo.getPiece1();
            this.piece2Side = connectInfo.getPiece1Side();
        } else
            Print.println("ERROR, PARENT OF " + this + " DOES NOT MATCH WITH ANY IN " + connectInfo);

        if (getType() == Type.STRONG)
            Tween.createTween("connectSide", this, "tweenPercent", 0, 1, 0.6).setEaseType(EaseType.EASE_IN);
        
    }

    public void draw(Graphics2D g, int parentDrawx, int parentDrawy, int tileSize) {
        if (getType() == Type.NOTHING || (getType() == Type.STRONG && isConnected() && tweenPercent == 1))
            return;
        int offset = (int) Math.ceil((DRAW_OFF * DRAW_WIDTH_PERCENT * tileSize));
        int[] xList = new int[4], yList = new int[4];
        if (!isConnected())
            g.setColor(getType() == Type.STRONG ? STRONG_COLOR : WEAK_COLOR);
        else
            g.setColor(getType() == Type.STRONG ? CONNECTED_STRONG_COLOR : CONNECTED_WEAK_COLOR);
        
        fillInDrawLists(xList, yList, tileSize, offset);

        for (int i=0; i<xList.length; i++) {
            xList[i] += parentDrawx;
            yList[i] += parentDrawy;
        }
        g.fillPolygon(xList, yList, xList.length);
    }

    // gets the draw information for sides (relative)
    // mutates xList and yList
    private void fillInDrawLists(int[] xList, int[] yList, int tileSize, int offset) {
        int halfTile = tileSize / 2;
        switch (getDirection()) {
            case UP: 
                xList[0] = (int) JMath.lerp(0, halfTile, tweenPercent);               yList[0] = 0;
                xList[1] = (int) JMath.lerp(tileSize, halfTile, tweenPercent);          yList[1] = 0;
                xList[2] = (int) JMath.lerp(tileSize - offset, halfTile, tweenPercent); yList[2] = offset;
                xList[3] = (int) JMath.lerp(offset, halfTile, tweenPercent);            yList[3] = offset;
                break;
            case LEFT: 
                xList[0] = 0;      yList[0] = (int) JMath.lerp(0, halfTile, tweenPercent);
                xList[1] = 0;      yList[1] = (int) JMath.lerp(tileSize, halfTile, tweenPercent);
                xList[2] = offset; yList[2] = (int) JMath.lerp(tileSize - offset, halfTile, tweenPercent);
                xList[3] = offset; yList[3] = (int) JMath.lerp(offset, halfTile, tweenPercent);
                break;
            case DOWN: 
                xList[0] = (int) JMath.lerp(0, halfTile, tweenPercent);                 yList[0] = tileSize;
                xList[1] = (int) JMath.lerp(tileSize, halfTile, tweenPercent);          yList[1] = tileSize;
                xList[2] = (int) JMath.lerp(tileSize - offset, halfTile, tweenPercent); yList[2] = tileSize - offset;
                xList[3] = (int) JMath.lerp(offset, halfTile, tweenPercent);            yList[3] = tileSize - offset;
                break;
            case RIGHT:
                xList[0] = tileSize;          yList[0] = (int) JMath.lerp(0, halfTile, tweenPercent);
                xList[1] = tileSize;          yList[1] = (int) JMath.lerp(tileSize, halfTile, tweenPercent);
                xList[2] = tileSize - offset; yList[2] = (int) JMath.lerp(tileSize - offset, halfTile, tweenPercent);
                xList[3] = tileSize - offset; yList[3] = (int) JMath.lerp(offset, halfTile, tweenPercent);
                break;
        }
    }
}
