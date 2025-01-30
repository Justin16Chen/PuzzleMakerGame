package gameplay.gameObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gameplay.GameBoard;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import utils.Print;
import utils.direction.Direction;
import utils.direction.Directions;

public class MoveLogic {
    /*
    int hdir, vdir = directionOfMovement
    list breakpoints<puzzle, puzzle> = new list<>()
    list gameObjToCheck<gameObj> = new list<>()
    gameObjToCheck.add(mover)
    hashmap checkedPositions<<int, int>, boolean> = new hashmap<>()
    do {
        for current : gameObjToCheck
            for direction : getAllDirections()
                gameObj = getGameObj(current.x + direction.x, current.y + direction.y)

                if gameObj == null or checkedPositions.getValue(gameObj.x, gameObj.y) == true
                    skip
                
                if direction != directionOfMovement
                    // skip unecessary game objects
                    if (!current.shouldConsider(gameObj))
                        skip

                if (!gameObj.canMove(direction))
                    breakpoints.add(current, gameObj)
                else
                    gameObjToCheck.add(gameObj)
            gameObjToCheck.remove(current)
    } while (gameObjToCheck.length > 0)
    */

    public static ArrayList<GameObject[]> findBreakpoints(GameBoard gameBoard, GameObject mover, int hdir, int vdir) { 
        ArrayList<GameObject[]> breakpoints = new ArrayList<>();
        ArrayList<GameObject> gameObjectsToCheck = new ArrayList<>();
        gameObjectsToCheck.add(mover);
        HashMap<List<Integer>, Boolean> checkedPositions = new HashMap<>();

        do {
            // check each object for breakpoints
            for (int i=0; i<gameObjectsToCheck.size(); i++) {
                GameObject current = gameObjectsToCheck.get(i);

                // check for breakpoints on all directions
                for (Direction dir : Directions.getAllDirections()) {

                    // skip if out of bounds
                    int x = current.getBoardX() + Directions.getDirectionX(dir);
                    int y = current.getBoardY() + Directions.getDirectionY(dir);
                    if (!gameBoard.inBounds(x, y))
                        continue;
                    
                    GameObject gameObject = gameBoard.getGameObject(x, y);

                    // skip if cell is empty
                    if (gameObject == null)
                        continue;

                    // skip if cell has already been checked
                    if (checkedPositions.get(List.of(x, y)) != null)
                        continue;
                    checkedPositions.put(List.of(x, y), true); // update hash map

                    // skip if game object should not be considered (isn't affected by movement)
                    // dir != Directions.getDirection(hdir, vdir) && 
                    if (!current.shouldConsider(gameObject))
                        continue;
                    
                    // if current.canMove = true but gameObject.canMove = false there must be a breakpoint between these game objects
                    if (!gameObject.getMoveInfo(gameBoard, new ArrayList<GameObject>(), hdir, vdir).canMove() && !current.mustMoveWith(gameObject))
                        breakpoints.add(new GameObject[]{current, gameObject}); 
                    else
                        gameObjectsToCheck.add(gameObject); // add object to be further analyzed next iteration
                }
                gameObjectsToCheck.remove(i--); // don't check current next iteration
            }
        } while (gameObjectsToCheck.size() > 0);
        return breakpoints;
    }

    public static void disconnectBreakpoints(ArrayList<GameObject[]> breakpoints) {
        for (GameObject[] breakpoint : breakpoints) {
            // for now, skip anything that aren't puzzle pieces
            if (!PuzzlePiece.isPuzzlePiece(breakpoint[0]) || !PuzzlePiece.isPuzzlePiece(breakpoint[1])) {
                Print.println(breakpoint[0] + " and " + breakpoint[1] + " found in breakpoint list when disconnecting", Print.RED);
                Print.println("THIS SHOULD NOT HAPPEN!!!", Print.RED);
                continue;
            }
            
            PuzzlePiece.disconnect((PuzzlePiece) breakpoint[0], (PuzzlePiece) breakpoint[1]);
        }
    }
}
