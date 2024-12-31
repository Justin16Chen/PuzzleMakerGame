package gameplay.gameObjects;

import java.util.ArrayList;

import gameplay.GameBoard;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import gameplay.gameObjects.puzzlePiece.Side;
import utils.Direction;
import utils.Print;

public class MoveLogic {
    public static boolean canObjectsMove(GameBoard gameBoard, ArrayList<GameObject> gameObjects, int hdir, int vdir) {
        Print.println("CAN OBJECTS MOVE: " + gameObjects.size() + " OBJECTS IN DIRECTION (" + hdir + ", " + vdir + ")", Print.YELLOW);

        // no movement
        if (hdir == 0 && vdir == 0) return true;

        // valid game objects that can be skipped b/c we already checked that they can move
        ArrayList<GameObject> checkedGameObjects = new ArrayList<>();

        // tx, ty: targetx, targety
        // go: gameObject
        // tgo: targetGameObject
        for (GameObject go : gameObjects) {

            // skip game object if already checked
            // cgo: checked game object
            boolean checkedgo = false;
            for (GameObject cgo : checkedGameObjects) {
                if (go.equals(cgo)) {
                    checkedgo = true;
                    break;
                }
            }
            if (checkedgo) continue;

            // target position (where to look next)
            int tx = go.getBoardX(), ty = go.getBoardY();
            boolean canMove = true;
            do {

                // target game object at target position
                GameObject tgo = gameBoard.getGameObject(tx, ty);

                // empty cell
                if (tgo == null) break;

                // immovable object
                if (!tgo.isMovable()) {
                    canMove = false;
                    break;
                }

                // add valid game object to checkedGameObjectList
                checkedGameObjects.add(tgo);

                // increment target position to look to next square
                tx += hdir;
                ty += vdir;

                // object cannot move out of bounds
                if (!gameBoard.inBounds(tx, ty)) {
                    canMove = false;
                    break;
                }
            } while (true);

            // even if one of the gameObjects cannot move, this function will return false
            if (!canMove) return false;
        }
        return true;
    }

    // index represents how many movable gameObjects the gameObject is away from the mover (usually the player)
    public static void updateMoveIndecies(GameBoard gameBoard, int moverx, int movery) {

        // check preconditions
        if (!gameBoard.inBounds(moverx, movery)) throw new RuntimeException("start pos of (" + moverx + ", " + movery + ") is not in bounds of gameboard");
        GameObject mover = gameBoard.getGameObject(moverx, movery);
        if (mover == null) throw new RuntimeException("starting object cannot be null at (" + moverx + ", " + movery + ")");

        // preset all game object indeces to -1 - if they are not connected in any way to the mover, their index will remain -1
        for (GameObject gameObject : gameBoard.getGameObjects()) 
            gameObject.setMoveIndex(-1);

        // index of game objects
        int index = 0;

        // cells to set index of (at first, only set index of starting cell, then expand outwards)
        ArrayList<GameObject> goList = new ArrayList<>();
        goList.add(mover);

        do {

            // set the index
            for (GameObject gameObject : goList) {
                gameObject.setMoveIndex(index);
            }

            // update the index
            index++;

            // update the game object list
            ArrayList<GameObject> newGoList = new ArrayList<>();
            for (GameObject gameObject : goList) {

                for (int i=0; i<4; i++) {
                    // find adjacent cell position
                    Direction.Type direction = Direction.getDirection(i);
                    int dirx = Direction.getDirectionX(direction);
                    int diry = Direction.getDirectionY(direction);
                    int x = gameObject.getBoardX() + dirx;
                    int y = gameObject.getBoardY() + diry;

                    // only add to list if it is a valid non-empty game object
                    if (!gameBoard.inBounds(x, y) || gameBoard.getGameObject(x, y) == null) continue;

                    // and is movable
                    // and if the game object does not have to check or has not been indexed yet
                    GameObject adjacentGameObject = gameBoard.getGameObject(x, y);
                    if (adjacentGameObject.isMovable() && adjacentGameObject.getMoveIndex() == -1)
                        newGoList.add(adjacentGameObject);
                }
            }
            goList = newGoList;

        } while (goList.size() > 0);

    }
    public static ArrayList<GameObject> findBreakpointBoundaries(GameBoard gameBoard, int moverx, int movery, int hdir, int vdir) {
        // Print.println("FIND BREAKPOINT BOUNDARIES", Print.YELLOW);

        // check preconditions
        if (!gameBoard.inBounds(moverx, movery)) throw new RuntimeException("start pos of (" + moverx + ", " + movery + ") is not in bounds of gameboard");
        GameObject mover = gameBoard.getGameObject(moverx, movery);
        if (mover == null) throw new RuntimeException("starting object cannot be null at (" + moverx + ", " + movery + ")");
        
        if (hdir == 0 && vdir == 0) return new ArrayList<GameObject>();

        // also clear all parents
        // uncheck all sides
        for (GameObject gameObject : gameBoard.getGameObjects()) {
            gameObject.clearParents();
            gameObject.setMustCheck(false);
            gameObject.clearCheckedSides();
        }

        // list of all mustCheck gameObjects that cannot move (the breakpoint must be somewhere between gameObject.index and 0, assuming the attempted movement is valid)
        ArrayList<GameObject> breakpointBoundaries = new ArrayList<>();

        // cells to set index of (at first, only set index of starting cell, then expand outwards)
        ArrayList<GameObject> goList = new ArrayList<>();
        goList.add(mover);

        // the mover must always move
        mover.setMustCheck(true);

        do {
            // System.out.println("NEXT ITERATION");
            for (GameObject gameObject : goList) {

                // set mustCheck
                // can ignore if it is already set to true
                // it either is in direction of motion or is strong connected to parent
                // but the parent must also be strong connected
                if (!gameObject.mustCheck()) {
                    // System.out.println("checking game object: " + gameObject);
                    for (GameObject parent : gameObject.getParents()) {
                        if (parent.mustCheck()) {
                            int dirx = gameObject.getBoardX() - parent.getBoardX();
                            int diry = gameObject.getBoardY() - parent.getBoardY();
                            if (dirx == hdir && diry == vdir) {
                                gameObject.setMustCheck(true);
                                break;
                            }
                            else if (PuzzlePiece.isPuzzlePiece(gameObject) && PuzzlePiece.isPuzzlePiece(parent)
                                    && Side.getConnectionType((PuzzlePiece) gameObject, (PuzzlePiece) parent) == Side.Type.STRONG) {
                                gameObject.setMustCheck(true);
                                break;
                            }
                        }
                    }
                    // System.out.println("must check: " + gameObject.mustCheck());
                }

                // if gameObject.canMove = false AND gameObject.mustCheck = true
                // there must be a break point between this index and 0
                // only add distinct breakpoints (no repeats)
                if (!gameObject.getMoveInfo(hdir, vdir).canMove()) {
                    boolean hasBreakpoint = false;
                    for (GameObject breakpoint : breakpointBoundaries) {
                        if (breakpoint.equals(gameObject)) {
                            hasBreakpoint = true;
                            break;
                        }
                    }
                    if (!hasBreakpoint) {
                        breakpointBoundaries.add(gameObject);
                        // System.out.println("ADDING BREAKPOINT BOUNDARY: " + gameObject);
                    }
                }
            }

            // System.out.println("ADDING ADJACENT GAME OBJECTS");

            // update the game object list
            ArrayList<GameObject> newGoList = new ArrayList<>();
            for (GameObject gameObject : goList) {
                // System.out.println("game object to find adjacent cells for: ");
                // System.out.println(gameObject);

                for (int i=0; i<4; i++) {
                    // find adjacent cell position
                    Direction.Type direction = Direction.getDirection(i);
                    Direction.Type oppositeDirection = Direction.getOppositeDirection(direction);
                    int dirx = Direction.getDirectionX(direction);
                    int diry = Direction.getDirectionY(direction);
                    int x = gameObject.getBoardX() + dirx;
                    int y = gameObject.getBoardY() + diry;

                    // only add to list if it is a connected puzzle piece 
                    if (!gameBoard.inBounds(x, y) || gameBoard.getGameObject(x, y) == null) continue;
                    GameObject adjacentGameObject = gameBoard.getGameObject(x, y);
                    if (PuzzlePiece.isPuzzlePiece(adjacentGameObject) && Side.isConnected((PuzzlePiece) adjacentGameObject, (PuzzlePiece) gameObject)) {
                        PuzzlePiece adjacentPuzzlePiece = (PuzzlePiece) adjacentGameObject;

                        // and if the game object does not have to check or has not been indexed yet or has an inaccurate index
                        // System.out.println("adjacent puzzle piece: " + adjacentPuzzlePiece);
                        if (!adjacentPuzzlePiece.mustCheck() && !adjacentPuzzlePiece.getCheckedSide(oppositeDirection)) {

                            // add to next iteration's list
                            adjacentPuzzlePiece.checkSide(oppositeDirection);
                            // System.out.println("adding to list");
                            newGoList.add(adjacentGameObject);

                            // add parent 
                            adjacentPuzzlePiece.addParent(gameObject);
                        }
                    }
                }
            }
            goList = newGoList;
        } while (goList.size() > 0);
        return breakpointBoundaries;
    }
}
