package gameplay;

import gameplay.mapLoading.*;
import utils.Print;
import utils.drawing.*;
import utils.tween.*;

public class LevelManager {
    
    private GameManager gameManager;
    private GameBoard gameBoard;

    private int currentLevel;
    public int numLevels;
    private boolean transitioning;
    private double totalTransitionTime;
    private double transitionTime;
    private double waitTime;

    private SimpleSprite transitionSprite;


    public LevelManager(GameManager gameManager, GameBoard gameBoard) {
        this.gameManager = gameManager;
        this.gameBoard = gameBoard;
        this.transitionSprite = SimpleSprite.createSimpleSprite(0, 0, gameManager.getWidth(), gameManager.getHeight());
        this.currentLevel = 1;
        updateGeneralLevelInfo();
    }

    public int getCurrentLevel() { return currentLevel; }
    public boolean transitioningBetweenLevels() { return transitioning; }
    public boolean hasLevel(int level) { return level <= numLevels && level > 0; }

    // sets the general level info
    public void updateGeneralLevelInfo() {
        System.out.println("UPDATING GENERAL LEVEL INFO");
        GeneralLevelInfo generalLevelInfo = LevelLoader.getGeneralLevelInfo("levelInfo.json");
        System.out.println(generalLevelInfo);
        numLevels = generalLevelInfo.getNumLevels();
        totalTransitionTime = generalLevelInfo.getTransitionTime();
        transitionTime = totalTransitionTime * 0.4;
        waitTime = totalTransitionTime * 0.2;
    }
    // play the intro/outro tweens for level transition
    private void playIntro() {
        transitioning = true;
        transitionSprite.setVisible(true);
        Tween.createTween("intro", transitionSprite, "height", 0, (int) gameManager.getHeight(), transitionTime, 0);
    }
    private void playOutro() {
        System.out.println("playOutro function called");
        transitionSprite.setVisible(true);
        Tween.createTween("outro", transitionSprite, "height", (int) gameManager.getHeight(), 0, transitionTime, 0);
        Timer.createSetTimer("stop transition", this, transitionTime, "transitioning", false, 0);
    }

    // transition to a specific level with optional intro/outro tweens
    public void transitionToLevel(Integer level, boolean intro, boolean outro) {
        updateGeneralLevelInfo();
        if (!hasLevel(level)) {
            Print.println(level + " does not exist", Print.RED);
            return;
        }
        currentLevel = level;
        if (intro) {
            playIntro();
            Timer.createCallTimer("call setLevelInfo in transitionToLevel", this, transitionTime + waitTime * 0.5, "setLevelInfo", 0, currentLevel);
            if (outro) {
                Timer.createCallTimer("play outro", this, transitionTime, "playOutro", 0).setPrint(Updatable.PrintType.ON_COMPLETE);
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
        if (!hasLevel(currentLevel + 1)) {
            Print.println(currentLevel + 1 + " does not exist", Print.RED);
            return;
        }
        currentLevel++;
        transitionToLevel(currentLevel, true, true);
    }

    // sets the level info of the gameboard to a level
    private void setLevelInfo(int level) {

        // load in the level
        LevelInfo levelInfo = LevelLoader.getLevelInfo(level + ".json", gameBoard);
        if (levelInfo != null) {
            gameBoard.setCurrentBoard(levelInfo);
        }
    }

    // updates the map data to any changes in the json files
    public void updateLevelInfo() {
        // load in the level
        LevelInfo levelInfo = LevelLoader.getLevelInfo(currentLevel + ".json", gameBoard);
        if (levelInfo != null) {
            gameBoard.setCurrentBoard(levelInfo);
        }
    }
}
