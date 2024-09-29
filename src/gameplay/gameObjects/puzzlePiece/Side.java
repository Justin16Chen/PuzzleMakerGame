package gameplay.gameObjects.puzzlePiece;

import utils.Direction;

public class Side {
    public enum Hierarchy {
        PARENT,
        CHILD,
        EMPTY
    }
    public enum Type {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }
    public enum Strength {
        STRONG,
        WEAK
    }

    public static Side[] getSideData(String typeString, String baseStrengthString) {
        Side[] sideData = new Side[4];
        for (int i=0; i<4; i++) {
            Type type;
            switch (typeString.charAt(i)) {
                case '+': type = Type.POSITIVE; break;
                case '-': type = Type.NEGATIVE; break;
                case 'n': type = Type.NEUTRAL; break;
                default: type = Type.NEUTRAL; break;
            }
            Strength baseStrength = baseStrengthString.charAt(i) == 's' ? Strength.STRONG : Strength.WEAK;
            sideData[i] = new Side(Direction.getDirection(i), type, baseStrength);
        }
        return sideData;
    }

    // whether the side types can connect to each other
    public static boolean isCompatible(Side side1, Side side2) {
        return side1.getType() != side2.getType() && 
            side1.getType() != Side.Type.NEUTRAL && side2.getType() != Side.Type.NEUTRAL &&
            !side1.isConnected() && !side2.isConnected();
    }

    // get the connection baseStrength given the baseStrength of two puzzle pieces
    // connection is strong if both sides are strong
    // otherwise considered weak
    public static Strength getConnectionStrength(Strength baseStrength1, Strength baseStrength2) {
        return baseStrength1 == Strength.STRONG && baseStrength2 == Strength.STRONG ? Strength.STRONG : Strength.WEAK;
    }

    private Direction.Type direction;
    private Hierarchy hierarchy;
    private Type type;
    private Strength baseStrength;
    private boolean connected;

    public Side(Direction.Type direction, Type type, Strength baseStrength) {
        this.direction = direction;
        this.hierarchy = Hierarchy.EMPTY;
        this.type = type;
        this.baseStrength = baseStrength;
    }

    @Override
    public String toString() {
        return "Side(direction: " + direction + " | type: " + type + " | baseStrength: " + baseStrength + " | hierarchy: " + hierarchy + ")";
    }

    public Direction.Type getDirection() { return direction; }
    public Hierarchy getHierarchy() { return hierarchy; }
    public Type getType() { return type; }
    public Strength getStrength() { return baseStrength; }
    public boolean isConnected() { return connected; }

    public void setConnected(boolean connected) { 
        if (type != Side.Type.NEUTRAL) this.connected = connected; 
        else this.connected = false;
    }
    public void setHierarchy(Hierarchy hierarchy ) { this.hierarchy = hierarchy; }
}
