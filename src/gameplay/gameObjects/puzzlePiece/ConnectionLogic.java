package gameplay.gameObjects.puzzlePiece;

import java.util.ArrayList;

import gameplay.GameBoard;
import gameplay.gameObjects.GameObject;
import utils.Print;
import utils.direction.Direction;
import utils.direction.Directions;

public class ConnectionLogic {
    /*  pseudo code for finding breakpoints given breakpoint boundaries
        for each breakpoint boundary (bb)
          skip if bb.objectType != puzzle piece
          skip if bb already in breakpoint list
          p (puzzlePiece) = bb
          while p.index > 0
            for each adjacent puzzle piece (adj)
              if adj.moveIndex < p.index
                if adj.canMove(directionOfMovement) && hasWeakConnection(p, adj)
                  addBreakpoint(p)
                  addBreakpointSide(p.side(direction from p to adj))
                  stop searching for breakpoint for bb
            if p != breakpoint
                list = adjacentPuzzlePieces 
                  WHERE adj has not been traversed through when finding previous breakpoints
                        AND adj.index < p.index
                if list.length > 0
                  p = ANY(list)
                else
                  this means the multiple breakpoint boundaries share the same breakpoint
                  stop searching for breakpoint boundary for bb
           
          if p.index = 0
            movement = invalid, throw error/print something out
    */
    public static ArrayList<Side> findBreakpoints(GameBoard gameBoard, ArrayList<GameObject> breakpointBoundaries, int hdir, int vdir) {
        // Print.println("FINDING BREAKPOINTS", Print.YELLOW);
        ArrayList<PuzzlePiece> breakpoints = new ArrayList<>();
        ArrayList<Side> sidesToDisconnect = new ArrayList<>();
        ArrayList<int[]> checkedPositions = new ArrayList<>();

        // do not need to find a breakpoint for each breakpoint boundary
        for (GameObject breakpointBoundary : breakpointBoundaries) {

            // skip if boundary is not a puzzle piece
            if (!PuzzlePiece.isPuzzlePiece(breakpointBoundary))
                continue;
            // skip if already found breakpoint
            boolean alreadyFoundBreakpoint = false;
            for (PuzzlePiece breakpoint : breakpoints)
                if (breakpointBoundary.equals(breakpoint)) {
                    alreadyFoundBreakpoint = true;
                    break;
                }
            if (alreadyFoundBreakpoint)
                continue;
            
            PuzzlePiece puzzlePiece = (PuzzlePiece) breakpointBoundary;
            puzzlePiece.clearParents();
            // System.out.println("BOUNDARY: " + breakpointBoundary);
            do {
                int[] pos = { puzzlePiece.getBoardX(), puzzlePiece.getBoardY() };
                checkedPositions.add(pos);
                // System.out.println("puzzle piece to find adjacent cells for: " + puzzlePiece);
                boolean foundBreakpoint = false;
                ArrayList<PuzzlePiece> lowerIndexPuzzlePieces = new ArrayList<>();

                // look at adjacent puzzle pieces to see if this is a breakpoint
                for (Direction direction : Directions.getAllDirections()) {
                    // find adjacent cell position
                    int dirx = Directions.getDirectionX(direction);
                    int diry = Directions.getDirectionY(direction);
                    int x = puzzlePiece.getBoardX() + dirx;
                    int y = puzzlePiece.getBoardY() + diry;

                    // skip invalid/empty cells
                    if (!gameBoard.inBounds(x, y) || gameBoard.getGameObject(x, y) == null) continue;
                    
                    GameObject adjacentGameObject = gameBoard.getGameObject(x, y);
                    // System.out.println("adjacent game object: " + adjacentGameObject);

                    // only puzzle pieces have connections
                    if (!PuzzlePiece.isPuzzlePiece(adjacentGameObject))
                        continue;

                    PuzzlePiece adjacentPuzzlePiece = (PuzzlePiece) adjacentGameObject;

                    if (adjacentPuzzlePiece.getMoveIndex() < puzzlePiece.getMoveIndex()) {
                        if (adjacentPuzzlePiece.getMoveInfo(hdir, vdir).canMove() && Side.getConnectionType(puzzlePiece, adjacentPuzzlePiece) == Side.Type.WEAK) {
                            breakpoints.add(puzzlePiece);
                            Direction pToAdjDirection = Directions.getDirection(adjacentGameObject.getBoardX() - puzzlePiece.getBoardX(), adjacentGameObject.getBoardY() - puzzlePiece.getBoardY());
                            sidesToDisconnect.add(puzzlePiece.getSide(pToAdjDirection));
                            foundBreakpoint = true;
                            break;
                        }
                        lowerIndexPuzzlePieces.add(adjacentPuzzlePiece);
                    }
                }

                // already found a breakpoint for breakpoint boundary, can stop searching and move on to next breakpoint boundary
                // breakpoint boundary cannot have more than one breakpoint
                if (foundBreakpoint)
                    break;

                // only look at puzzle pieces that have not been checked yet
                // checking puzzle pieces that have already been checked will lead down an already known breakpoint
                // and might ignore other possible breakpoints
                boolean hasNewOption = false;
                for (int i=0; i<lowerIndexPuzzlePieces.size(); i++) {
                    PuzzlePiece adjacentPuzzlePiece = lowerIndexPuzzlePieces.get(i);

                    boolean alreadyChecked = false;
                    for (int[] checkedPos : checkedPositions)
                        if (checkedPos[0] == adjacentPuzzlePiece.getBoardX() && checkedPos[1] == adjacentPuzzlePiece.getBoardY()) {
                            alreadyChecked = true;
                            break;
                        }
                    if (alreadyChecked)
                        continue;

                    hasNewOption = true;

                    // look to adjacent puzzle piece for possibility of breakpoint
                    puzzlePiece = adjacentPuzzlePiece;
                }

                // if there are no more unchecked puzzle pieces with a lower index, there is no breakpoint for the boundary and we can move on
                if (!hasNewOption) 
                    break;
                

            } while (puzzlePiece.getMoveIndex() > 0);

            if (puzzlePiece.getMoveIndex() == 0) {
                Print.println("ERROR, BREAKPOINT NOT FOUND FOR BREAKPOINT  " + breakpointBoundary);
                return null;
            }
        }
        return sidesToDisconnect;
    }


    // disconnect all breakpoints
    public static void disconnectBreakpoints(ArrayList<Side> breakpoints) {
        //Print.println("DISCONNECTING BREAKPOINT SIDES", Print.YELLOW);
        for (Side side : breakpoints)
            PuzzlePiece.disconnect(new DisconnectInfo(side.getParent(), side.getPiece2(), side.getDirection()));
    }
}
