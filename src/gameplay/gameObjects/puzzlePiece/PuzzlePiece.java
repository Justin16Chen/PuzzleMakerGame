package gameplay.gameObjects.puzzlePiece;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import gameplay.gameObjects.*;
import utils.direction.Direction;
import utils.direction.Directions;
import utils.drawing.sprites.Sprite;
import utils.drawing.tilemap.Tilemap;

public class PuzzlePiece extends GameObject {

    public static GameObject loadPuzzlePiece(JSONObject jsonObject) {
        return new PuzzlePiece(jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getString("sideData")); 
    }

    private static Tilemap baseTilemap;
    public static void loadTilemaps() {
        baseTilemap = new Tilemap("puzzlePieceTilemap", "res/tilemaps/puzzlePieceBorderTilemap.png", "res/tilemaps/puzzlePieceBorderTilemapData.json");
    }

    private static final double SIDE_DRAW_PERCENT = 0.13;

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

    // all puzzle pieces that need to be fully connected for this one to display its highlight color
    private ArrayList<PuzzlePiece> requiredPieces;
    private Side[] sides;

    public PuzzlePiece(int boardX, int boardY, String sideString) {
        super(GameObject.ObjectType.PUZZLE_PIECE, boardX, boardY, 1, 1);
        this.sides = Side.getSideData(this, sideString);
        requiredPieces = new ArrayList<>();
    }
    public PuzzlePiece(GameObject.ObjectType objectType, int boardX, int boardY, String sideString) {
        super(objectType, boardX, boardY, 1, 1);
        this.sides = Side.getSideData(this, sideString);
        requiredPieces = new ArrayList<>();
    }

    @Override
    public void setup(int x, int y, int width, int height) {
        sprite = new Sprite("puzzlePieceSprite", x, y, width, height, "gameObjects1") {
            @Override
            public void draw(Graphics2D g) {
                baseTilemap.drawTile(g, getX(), getY(), getWidth(), getHeight(), filledTilemapCells);

                for (Direction dir : Directions.getAllDirections())
                    getSide(dir).draw(g, getCenterX(), getCenterY(), getWidth() + 1, (int) Math.ceil((getWidth() * SIDE_DRAW_PERCENT)));
            }
        };

    }

    @Override
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
    public boolean shouldConsider(MoveInfo moveInfo, GameObject gameObject) {
        Direction dir = Directions.getDirectionBetweenGameObjects(this, gameObject);
        if (Directions.getDirectionX(dir) == moveInfo.getHdir() && Directions.getDirectionY(dir) == moveInfo.getVdir())
            return true;
        if (!isPuzzlePiece(gameObject) || !Directions.areGameObjectsAdjacent(this, gameObject))
            return false;
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
    public boolean allSidesConnected() {
        for (Direction direction : Directions.getAllDirections()) {
            if (!getSide(direction).isConnected() && getSide(direction).getType() != Side.Type.NOTHING)
                return false;
        }
        return true;
    }
    // pieces that must be all connected for this puzzle piece to light up
    public void updateRequired(GameBoard board) {
        requiredPieces.clear();
        for (Direction dir : Directions.getAllDirections())
            if (getSide(dir).isConnected()) {
                int x = getBoardX() + Directions.getDirectionX(dir);
                int y = getBoardY() + Directions.getDirectionY(dir);
                requiredPieces.add((PuzzlePiece) board.getGameObject(x, y));
            }
    }
    
    @Override
    public void updateTilemapToBoard(GameBoard board) {
        ArrayList<PuzzlePiece> connectedPieces = getAllConnectedPuzzlePieces(board, new ArrayList<PuzzlePiece>());
        filledTilemapCells = new boolean[3][3];
        for (PuzzlePiece piece : connectedPieces) {
            int dx = piece.getBoardX() - getBoardX(), dy = piece.getBoardY() - getBoardY();
            if (dx * dx + dy * dy <= 2) {
                filledTilemapCells[1 + dy][1 + dx] = true;
            }
        }
        // ignore cells that also aren't connected to adjacent sides
        for (Direction dir : Directions.getAllDirections())
            if (!getSide(dir).isConnected())
                filledTilemapCells[1 + Directions.getDirectionY(dir)][1 + Directions.getDirectionX(dir)] = false;
    }
    // gets list of all puzzle pieces that are connected w this
    private ArrayList<PuzzlePiece> getAllConnectedPuzzlePieces(GameBoard board, ArrayList<PuzzlePiece> ignoreList) {
        ArrayList<PuzzlePiece> connectedPieces = new ArrayList<>();
        connectedPieces.add(this);
        ignoreList.add(this);

        for (Direction dir : Directions.getAllDirections()) {
            Side side = getSide(dir);
            if (!side.isConnected())
                continue;
            
            PuzzlePiece piece = (PuzzlePiece) board.getGameObject(getBoardX() + Directions.getDirectionX(dir), getBoardY() + Directions.getDirectionY(dir));
            if (!ignoreList.contains(piece))
                connectedPieces.addAll(piece.getAllConnectedPuzzlePieces(board, ignoreList));
        }
        return connectedPieces;
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
        //Print.println("CUSTOM MOVE FOR " + this, Print.BLUE);
        //System.out.println("direction: (" + moveInfo.getHdir() + ", " + moveInfo.getVdir() + ")");
        
        // move connected puzzle pieces
        for (Direction direction : Directions.getAllDirections()) {
            Side side = getSide(direction);
            if (side.isConnected())
                gameBoard.getGameObject(getBoardX() + Directions.getDirectionX(direction), getBoardY() + Directions.getDirectionY(direction)).move(gameBoard, moveInfo, false);
        }
        // move self
        moveSelf(gameBoard, moveInfo);
    }

    // make sure all connections are still valid - if they aren't, disconnect them
    @Override
    protected void performBeforeMovement(GameBoard gameBoard, MoveInfo moveInfo) {
        checkForDisconnections(gameBoard);
    }
    // check for connections when everything has finished moving
    @Override
    protected void performAfterMovement(GameBoard gameBoard, MoveInfo moveInfo) {
        checkForDisconnections(gameBoard);
        checkForConnections(gameBoard, moveInfo, Side.ConnectType.DECIDE);
    }

    // checks for and disconnects any wrongly connected pieces (only for self, meant to be called for all puzzle pieces)
    public void checkForDisconnections(GameBoard gameBoard) {
        //System.out.println("check disconnections for " + this);
        for (Direction dir : Directions.getAllDirections()) {
            if (!getSide(dir).canConnect()) 
                continue;
            
                int connectDirx = Directions.getDirectionX(dir);
                int connectDiry = Directions.getDirectionY(dir);
                int x = getBoardX() + connectDirx;
                int y = getBoardY() + connectDiry;

                if (!gameBoard.inBounds(x, y)) {
                    if (getSide(dir).isConnected()) 
                        getSide(dir).disconnect();
                    continue;
                } 
                
                GameObject gameObject = gameBoard.getGameObject(x, y);
                // System.out.println(gameObject + " at " + x + " " + y);
                if (!PuzzlePiece.isPuzzlePiece(gameObject)) {
                    if (getSide(dir).isConnected())
                        //System.out.println("not a puzzle piece");
                        getSide(dir).disconnect();
                }
                else {
                    Side otherSide = ((PuzzlePiece) gameObject).getSide(Directions.getOppositeDirection(dir));
                    if (!Side.isCompatible(getSide(dir), otherSide)) {
                        if (getSide(dir).isConnected()) {
                            //System.out.println("not compatible with " + gameObject + " with side " + otherSide);
                            getSide(dir).disconnect();
                            System.out.println(getSide(dir) + " is disconnected");
                        }
                        
                    }
                }

        }
    }
    // update connected state for all sides
    public void checkForConnections(GameBoard gameBoard, MoveInfo moveInfo, Side.ConnectType connectAnimationType) {
        //System.out.println("check connections for " + this);
        for (Direction dir : Directions.getAllDirections()) {
            if (!getSide(dir).canConnect()) 
                continue;
            int connectDirx = Directions.getDirectionX(dir);
            int connectDiry = Directions.getDirectionY(dir);
            
            int x = getBoardX() + connectDirx;
            int y = getBoardY() + connectDiry;
            Side ownSide = getSide(dir);
            if (!gameBoard.inBounds(x, y)) 
                continue;

            GameObject gameObject = gameBoard.getGameObject(x, y);
            if (PuzzlePiece.isPuzzlePiece(gameObject)) {
                Side otherSide = ((PuzzlePiece) gameObject).getSide(Directions.getOppositeDirection(dir));
                if (Side.isCompatible(ownSide, otherSide)) {
                    ownSide.connect(connectAnimationType);
                    otherSide.connect(connectAnimationType);
                }
            }
        }
    }
    
    @Override
    public void updateDrawList() {
        ArrayList<String> drawList = new ArrayList<String>();

        drawList.add("pos: (" + getBoardX() + ", " + getBoardY() + ")");
        drawList.add("right and bottom: " + getBoardRight() + ", " + getBoardBottom());
        drawList.add("sides: ");
        for (int i = 0; i < 4; i++) {
            Direction direction = Directions.getDirection(i);
            drawList.add(direction + ": " + getSide(direction));
        }
        drawList.add("filled adjacent cells");
        for (int i = 0; i < filledTilemapCells.length; i++) {
            String str = "";
            for (int j = 0; j < filledTilemapCells.length; j++)
                str += filledTilemapCells[i][j];
            drawList.add(str);
        }

        infoBox.setDrawList(drawList);
    }
    
    public String toString() {
        return super.toString() + " " + Arrays.toString(sides);
    }
}
