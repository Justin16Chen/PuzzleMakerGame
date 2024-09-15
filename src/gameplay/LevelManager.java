package gameplay;

import gameplay.mapLoading.*;
import utils.drawing.*;
import utils.tween.*;

public class LevelManager {
    
    private GameManager gameManager;
    private GameBoard gameBoard;

    private int currentLevel = 1;
    private boolean transitioning;

    private SimpleSprite transitionSprite;


    public LevelManager(GameManager gameManager, GameBoard gameBoard) {
        this.gameManager = gameManager;
        this.gameBoard = gameBoard;
        this.transitionSprite = SimpleSprite.createSimpleSprite(0, 0, gameManager.getWidth(), gameManager.getHeight());
    }

    public int getCurrentLevel() { return currentLevel; }
    public boolean transitioningBetweenLevels() { return transitioning; }

    // play the intro/outro tweens for level transition
    private void playIntro() {
        transitioning = true;
        transitionSprite.setVisible(true);
        Tween.createTween("intro", transitionSprite, "height", 0, (int) gameManager.getHeight(), 0.3, 0);
    }
    private void playOutro() {
        System.out.println("playOutro function called");
        transitionSprite.setVisible(true);
        Tween.createTween("outro", transitionSprite, "height", (int) gameManager.getHeight(), 0, 0.3, 0);
        Timer.createSetTimer("stop transition", this, 0.3, "transitioning", false, 0);
    }

    // transition to a specific level with optional intro/outro tweens
    public void transitionToLevel(Integer level, boolean intro, boolean outro) {
        currentLevel = level;
        if (intro) {
            playIntro();
            Timer.createCallTimer("call setLevelInfo in transitionToLevel", this, 0.35, "setLevelInfo", 0, currentLevel);
            if (outro) {
                Timer.createCallTimer("play outro", this, 0.4, "playOutro", 0).setPrint(Updatable.PrintType.ON_COMPLETE);
            }
        } else {
            setLevelInfo(currentLevel);
            if (outro) {
                playOutro();
            }
            else {
                transitioning = false;
            }
        }
    }
    
    // transition to the next level with optional intro/outro tweens
    public void transitionToNextLevel(boolean intro, boolean outro) {
        currentLevel++;
        transitionToLevel(currentLevel, true, true);
    }

    // sets the level info of the gameboard to a level
    private void setLevelInfo(int level) {

        // load in the level
        MapInfo mapInfo = LevelLoader.getMapInfo(currentLevel + ".json", gameBoard);
        if (mapInfo != null) {
            gameBoard.setCurrentBoard(mapInfo);
        }
    }

    // updates the map data to any changes in the json files
    private void updateLevelInfo() {
        // load in the level
        MapInfo mapInfo = LevelLoader.getMapInfo(currentLevel + ".json", gameBoard);
        if (mapInfo != null) {
            gameBoard.setCurrentBoard(mapInfo);
        }
    }
}
