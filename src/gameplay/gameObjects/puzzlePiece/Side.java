package gameplay.gameObjects.puzzlePiece;

import java.awt.Color;
import java.awt.Graphics2D;

import utils.direction.Direction;
import utils.direction.Directions;
import utils.math.JMath;
import utils.tween.Ease;
import utils.tween.EaseType;
import utils.tween.Tween;

public class Side {
    public enum Type {
        STRONG,
        WEAK,
        NOTHING
    }
    final private static Color STRONG_COLOR = new Color(200, 180, 20);
    final private static Color WEAK_COLOR = new Color(70, 120, 230);
    final private static Color CONNECTED_STRONG_COLOR = new Color(250, 245, 150);
    final private static Color CONNECTED_WEAK_COLOR = new Color(90, 185, 255);
    final private static double CONNECT_TWEEN_TIME = 0.6;

    // returns a list of sides based on string (from json file)
    public static Side[] getSideData(PuzzlePiece parent, String typeString) {
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
    private PuzzlePiece parent;
    private double tweenPercent; // plays animation based on this when connecting w another side
    private Tween connectTween;

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
        if (type == Type.NOTHING)
            return direction + "|NONE";
        return direction + "|" + connected;
    }

    public Direction getDirection() { return direction; }
    public Type getType() { return type; }
    public boolean isConnected() { return connected; }
    public boolean canConnect() { return type != Type.NOTHING; }
    public PuzzlePiece getParent() { return parent; }

    public void disconnect() { 
        connected = false;
        tweenPercent = 0;
        if (connectTween != null)
            connectTween.delete();

    }
    public void connect(boolean playAnimation) {
        // ignore if already connected
        if (connected)
            return;
        connected = true;

        if (getType() == Type.STRONG) 
            if (playAnimation)
                connectTween = Tween.createTween("connectSide", this, "tweenPercent", 0, 1, CONNECT_TWEEN_TIME).setEaseType(new EaseType(Ease.EASE_IN_BACK));
            else 
                tweenPercent = 1;
    }

    private int getTweenedDrawSize(int size, double tweenPercent) {
        return (int) Math.round(size * (1 - tweenPercent));
    }
    public void draw(Graphics2D g, int drawCenterX, int drawCenterY, int width, int height) {

        if (getType() == Side.Type.NOTHING)
            return;
        // top side
        int halfWidth = width / 2;
        int inset = (int) (height / Math.sqrt(2));

        int tweenedHalfWidth = getTweenedDrawSize(halfWidth, tweenPercent);
        int leftTweenedHalfInset = getTweenedDrawSize(-halfWidth + inset, tweenPercent);
        int rightTweenedHalfInset = getTweenedDrawSize(halfWidth - inset, tweenPercent);
        int[] xList = { -tweenedHalfWidth,  tweenedHalfWidth,  rightTweenedHalfInset, leftTweenedHalfInset };
        int[] yList = { -halfWidth, -halfWidth, -halfWidth + height, -halfWidth + height };

        // rotate 
        int dirIndex = Directions.getDirectionIndex(direction);
        for (int i=0; i<4; i++) {
            double[] rotatedPoint = JMath.rotateOrthogonalAroundPoint(xList[i], yList[i], 0, 0, dirIndex);
            xList[i] = drawCenterX + (int) rotatedPoint[0];
            yList[i] = drawCenterY + (int) rotatedPoint[1];
        }

        switch (type) {
            case WEAK: g.setColor(connected ? CONNECTED_WEAK_COLOR : WEAK_COLOR); break;
            case STRONG: g.setColor(connected ? CONNECTED_STRONG_COLOR : STRONG_COLOR); break;
        }
        g.fillPolygon(xList, yList, xList.length); 
    }
    
    
}
