package gameplay.gameObjects.puzzlePiece;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.json.JSONObject;

import gameplay.GameBoard;
import gameplay.gameObjects.*;
import utils.direction.Direction;
import utils.direction.Directions;
import utils.drawing.sprites.Sprite;

public class PuzzlePiece extends GameObject {

    public static GameObject loadPuzzlePiece(JSONObject jsonObject) {
        return new PuzzlePiece(jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData")); 
    }

    public static Color COLOR = new Color(72, 72, 72);
    public static Color HIGHLIGHT_COLOR = new Color(132, 132, 132);
    public static Color OUTLINE_COLOR = COLOR;

    public static boolean isPuzzlePiece(GameObject gameObject) {
        if (gameObject == null) 
            return false;
        return gameObject.getObjectType() == GameObject.ObjectType.PLAYER_PIECE || gameObject.getObjectType() == GameObject.ObjectType.PUZZLE_PIECE;
    }

    public static void disconnect(PuzzlePiece p1, PuzzlePiece p2) {
        Direction p1Dir = Directions.getDirectionBetweenGameObjects(p1, p2);
        p1.getSide(p1Dir).disconnect();
        p2.getSide(Directions.getOppositeDirection(p1Dir)).disconnect();
    }

    private Side[] sides;

    public PuzzlePiece(int boardX, int boardY, String sideString) {
        super(GameObject.ObjectType.PUZZLE_PIECE, boardX, boardY, 1, 1);
        this.sides = Side.getSideData(this, sideString);
    }
    public PuzzlePiece(GameObject.ObjectType objectType, int boardX, int boardY, String sideString) {
        super(objectType, boardX, boardY, 1, 1);
        this.sides = Side.getSideData(this, sideString);
    }

    @Override
    public void setup(int x, int y, int width, int height) {
        sprite = new Sprite("puzzlePieceSprite", x, y, width, height, "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(getDrawColor());
                g.fillRect(getX(), getY(), getWidth(), getHeight());

                for (int i=0; i<4; i++)
                    getSide(Directions.getDirection(i)).draw(g, getX(), getY(), getWidth());
            }
        };
    }

    public boolean equals(GameObject gameObject) {
        if (gameObject == null) return false;
        if (gameObject.getObjectType() != getObjectType()) return false;
        PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;

        boolean hasSameSides = true;
        for (Direction direction : Directions.getAllDirections()) {
            if (!getSide(direction).equals(puzzlePiece.getSide(direction))) {
                hasSameSides = false;
                break;
            }
        }
        return super.equals(puzzlePiece) && hasSameSides;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonGameObject = super.toJSONObject();
        String sideString = "";
        for (Side side : sides)
            switch (side.getType()) {
                case NOTHING: sideString += "n"; break;
                case STRONG: sideString += "s"; break;
                case WEAK: sideString += "w"; break;
            }
        jsonGameObject.put("sideData", sideString);
        return jsonGameObject;
    }

    // should consider any connected puzzle pieces when checking for breakpoints
    @Override
    public boolean shouldConsider(GameObject gameObject) {
        if (!isPuzzlePiece(gameObject) || !Directions.areGameObjectsAdjacent(this, gameObject))
            return false;
        Direction dir = Directions.getDirectionBetweenGameObjects(this, gameObject);
        return getSide(dir).isConnected();
    }

    @Override
    public boolean mustMoveWith(GameObject gameObject) {
        if (!isPuzzlePiece(gameObject) || !Directions.areGameObjectsAdjacent(this, gameObject))
            return false;
        Direction dir = Directions.getDirectionBetweenGameObjects(this, gameObject);
        return getSide(dir).getType() == Side.Type.STRONG
            && getSide(dir).isConnected();
    }

    // gets the type of side based on a direction
    public Side getSide(Direction direction) {
        return sides[Directions.getMoveIndex(direction)];
    }
    // one of the sides is connected
    public boolean areAllSidesConnected() {
        for (Direction direction : Directions.getAllDirections()) {
            if (!getSide(direction).isConnected() && getSide(direction).getType() != Side.Type.NOTHING)
                return false;
        }
        return true;
    }

    // gets the moveinfo based on all of the attached puzzle pieces
    // that are in the direction of movement
    // or only checks puzzle pieces that have strong connections and that have the correct hierarchy structure
    @Override
    public MoveInfo getMoveInfo(GameBoard gameBoard, ArrayList<GameObject> callerList, int hdir, int vdir) {
        callerList.add(this);
        //Print.println("GET ALL MOVE INFO for " + this + ", dir: " + hdir + ", " + vdir, Print.PURPLE);
        // make sure movement is in bounds and is valid
        // currently using base GameObject.getMoveInfo
        // need to create PuzzlePiece.getMoveInfo to account for any connected PuzzlePieces

        for (Direction selfToPiece : Directions.getAllDirections()) {
            int offsetx = Directions.getDirectionX(selfToPiece);
            int offsety = Directions.getDirectionY(selfToPiece);

            if (!gameBoard.inBounds(getBoardX() + offsetx, getBoardY() + offsety))
                continue;

            GameObject gameObject = gameBoard.getGameObject(getBoardX() + offsetx, getBoardY() + offsety);

            //System.out.println(selfToPiece + ": " + gameObject);

            // empty cell
            if (gameObject == null)
                continue;

            // already found moveInfo, ignore
            if (callerList.contains(gameObject))
                continue;

            // only get the move info if it is in the direction of movement
            // or if the object is a strong connected puzzle piece
            MoveInfo moveInfo = null;
            if (offsetx == hdir && offsety == vdir) {
                // call the proper function
                if (isPuzzlePiece(gameObject)) 
                    moveInfo = ((PuzzlePiece) gameObject).getMoveInfo(gameBoard, callerList, hdir, vdir);
                else 
                    moveInfo = gameObject.getMoveInfo(gameBoard, callerList, hdir, vdir);
            }
            else if (getSide(selfToPiece).isConnected() && getSide(selfToPiece).getType() == Side.Type.STRONG) {
                PuzzlePiece puzzlePiece = (PuzzlePiece) gameObject;
                moveInfo = puzzlePiece.getMoveInfo(gameBoard, callerList, hdir, vdir);
            }
            if (moveInfo != null && !moveInfo.canMove())
                return MoveInfo.makeInvalidMove();
        }

        // also always get move info for base piece with no side offsets
        return super.getMoveInfo(gameBoard, callerList, hdir, vdir);
    }
    // moves itself and any attached puzzle pieces
    // also look for any new connections to attach puzzle pieces
    @Override
    public void customMove(GameBoard gameBoard, MoveInfo moveInfo) {
        // Print.println("MOVE ALL ATTACHED FOR " + this, Print.BLUE);
        // System.out.println("direction: (" + moveInfo.getHdir() + ", " + moveInfo.getVdir() + ")");
        
        // move connected puzzle pieces
        for (Direction direction : Directions.getAllDirections()) {
            Side side = getSide(direction);
            if (side.isConnected())
                gameBoard.getGameObject(getBoardX() + Directions.getDirectionX(direction), getBoardY() + Directions.getDirectionY(direction)).move(gameBoard, moveInfo, false);
        }
        // move self
        moveSelf(gameBoard, moveInfo);
    }

    // check for connections when everything has finished moving
    @Override
    protected void performAfterMovement(GameBoard gameBoard, MoveInfo moveInfo) {
        checkForConnections(gameBoard, moveInfo, true);
    }

    // update connected state for all sides
    public void checkForConnections(GameBoard gameBoard, MoveInfo moveInfo, boolean playAnimation) {
        for (Direction dir : Directions.getAllDirections()) {
            if (getSide(dir).isConnected() || !getSide(dir).canConnect()) 
                continue;
            int connectDirx = Directions.getDirectionX(dir);
            int connectDiry = Directions.getDirectionY(dir);
            if (connectDirx == -moveInfo.getHdir() && connectDiry == -moveInfo.getVdir()) 
                continue;
            
            int x = getBoardX() + connectDirx;
            int y = getBoardY() + connectDiry;
            if (!gameBoard.inBounds(x, y))
                continue;
            GameObject gameObject = gameBoard.getGameObject(x, y);
            if (PuzzlePiece.isPuzzlePiece(gameObject)) {
                Side ownSide = getSide(dir);
                Side otherSide = ((PuzzlePiece) gameObject).getSide(Directions.getOppositeDirection(dir));
                if (Side.isCompatible(ownSide, otherSide)) {
                    ownSide.connect(playAnimation);
                    otherSide.connect(playAnimation);
                }
            }

        }
    }

    // get the color that the puzzle piece should be
    private Color getDrawColor() {
        if (areAllSidesConnected())
            return HIGHLIGHT_COLOR;
        
        return COLOR;
    }
    
    @Override
    public void updateDrawList() {
        ArrayList<String> drawList = new ArrayList<String>();

        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() + ")");
        drawList.add("sides: ");
        for (int i=0; i<4; i++) {
            Direction direction = Directions.getDirection(i);
            drawList.add(direction + ": " + getSide(direction));
        }

        infoBox.setDrawList(drawList);
    }
    
}
