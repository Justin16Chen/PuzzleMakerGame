package gameplay.mapLoading;

import java.awt.Color;

import gameplay.GameBoard;
import gameplay.GameManager;
import utils.Print;
import utils.drawing.*;
import utils.tween.*;

public class LevelManager {
    
    private GameManager gameManager;
    private GameBoard gameBoard;

    private int currentLevel;
    public int startLevel;
    public int lastLevel;
    private boolean transitioning;
    private double totalTransitionTime;
    private double transitionTime;

    private SimpleSprite transitionSprite;


    public LevelManager(GameManager gameManager, GameBoard gameBoard) {
        this.gameManager = gameManager;
        this.gameBoard = gameBoard;
        this.transitionSprite = new SimpleSprite("transitionSprite", 0, 0, gameManager.getWidth(), gameManager.getHeight(), "transitions").setColor(Color.BLACK);
        this.currentLevel = 1;
        updateGeneralLevelInfo();
    }

    public int getCurrentLevel() { return currentLevel; }
    public boolean transitioningBetweenLevels() { return transitioning; }
    public boolean hasLevel(int level) { return level <= lastLevel && level >= startLevel; }

    // sets the general level info
    public void updateGeneralLevelInfo() {
        // System.out.println("UPDATING GENERAL LEVEL INFO");
        GeneralLevelInfo generalLevelInfo = LevelLoader.getGeneralLevelInfo("levelInfo.json");
        startLevel = generalLevelInfo.getStartLevel();
        lastLevel = generalLevelInfo.getLastLevel();
        totalTransitionTime = generalLevelInfo.getTransitionTime();
        transitionTime = totalTransitionTime * 0.5;
    }

    // transition to a specific level with optional intro/outro tweens
    public void transitionToLevel(Integer level, boolean intro, boolean outro) {
        updateGeneralLevelInfo();
        if (!hasLevel(level)) {
            Print.println(level + " does not exist", Print.RED);
            return;
        }

        // simple transition
        if (!intro && !outro) {
            setLevelInfo(level);
            return;
        }

        transitioning = true;
        Updatables.deleteUpdatables(new String[]{"moveTransitionSpriteTween", "moveTransitionSpriteDownTween", "moveTransitionSpriteUpTween", "finishTransition", "updateGameToNewLevel"}); // clear any updatables from previous transitions

        // sprite transition animation
        transitionSprite.setVisible(true);
        if (intro && outro)
            Tween.createTween("moveTransitionSpriteTween", transitionSprite, "height", 1, gameManager.getHeight(), transitionTime).pingPong();
        else if (intro)
            Tween.createTween("moveTransitionSpriteDownTween", transitionSprite, "height", 1, gameManager.getHeight(), transitionTime);
        else if (outro)
            Tween.createTween("moveTransitionSpriteUpTween", transitionSprite, "height", gameManager.getHeight(), 1, transitionTime);
        
        // load level
        if (intro) 
            Timer.createCallTimer("updateGameToNewLevel", this, transitionTime, "setLevelInfo", level).setPrint(Updatable.PrintType.ON_COMPLETE); // this causes the bug
        else
            setLevelInfo(level);

        // update transition variable
        Timer.createCallTimer("finishTransition", this, intro && outro ? totalTransitionTime : transitionTime, "finishTransition");
    }

    @SuppressWarnings("unused")
    private void finishTransition() {
        transitioning = false;
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
    // returns if it is successful or not
    private boolean setLevelInfo(int level) {
        // load in the level
        LevelInfo levelInfo = LevelLoader.getLevelInfo(level + ".json", gameBoard);
        if (levelInfo != null) {
            currentLevel = level;

            // clear sprites
            gameBoard.clearGameObjects();

            // clear updatables
            Updatables.deleteAllUpdatablesExcept(new String[]{"finishTransition", "updateGameToNewLevel", "moveTransitionSpriteTween", "moveTransitionSpriteDownTween", "moveTransitionSpriteUpTween"});
            gameBoard.setCurrentBoard(levelInfo);
            return true;
        }
        return false;
    }

    // updates the map data to any changes in the json files
    public void updateLevelInfo() {
        setLevelInfo(currentLevel);
    }
}
