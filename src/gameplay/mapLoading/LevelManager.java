package gameplay.mapLoading;

import java.awt.Color;

import gameplay.GameManager;
import gameplay.gameObjects.GameBoard;
import gameplay.gameObjects.GameObjectData;
import utils.Print;
import utils.drawing.sprites.Sprite;
import utils.tween.*;

public class LevelManager {
    
    private GameManager gameManager;
    private GameBoard gameBoard;

    private int currentLevel;
    private boolean transitioning;
    private GeneralLevelInfo generalLevelInfo;

    private Sprite transitionSprite;


    public LevelManager(GameManager gameManager, GameBoard gameBoard) {
        this.gameManager = gameManager;
        this.gameBoard = gameBoard;
        transitionSprite = new Sprite("transitionSprite", 0, 0, gameManager.getWidth(), gameManager.getHeight(), "transitions");
        transitionSprite.setColor(Color.BLACK);
        updateGeneralLevelInfo();
        GameObjectData.loadObjectData();
    }

    public int getCurrentLevel() { return currentLevel; }
    public boolean transitioningBetweenLevels() { return transitioning; }
    public boolean hasLevel(int level) { return level <= generalLevelInfo.getEndLevel() && level >= generalLevelInfo.getStartLevel(); }
    public GeneralLevelInfo getGeneralLevelInfo() { return generalLevelInfo; }

    // sets the general level info
    public void updateGeneralLevelInfo() {
        GeneralLevelInfo newInfo = LevelLoader.getGeneralLevelInfo("res/properties/levelInfo.json");
        if (newInfo != null)
            generalLevelInfo = newInfo;
    }

    // transition to a specific level with optional intro/outro tweens
    public void transitionToLevel(Integer level, boolean intro, boolean outro) {
        updateGeneralLevelInfo();
        GameObjectData.loadObjectData();
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
            Tween.createTween("moveTransitionSpriteTween", transitionSprite, "height", 1, gameManager.getHeight(), generalLevelInfo.getTransitionTime()).setEaseType(new EaseType(Ease.EASE_OUT, 2)).pingPong();
        else if (intro)
            Tween.createTween("moveTransitionSpriteDownTween", transitionSprite, "height", 1, gameManager.getHeight(), generalLevelInfo.getTransitionTime()).setEaseType(new EaseType(Ease.EASE_OUT, 2));
        else if (outro)
            Tween.createTween("moveTransitionSpriteUpTween", transitionSprite, "height", gameManager.getHeight(), 1, generalLevelInfo.getTransitionTime()).setEaseType(new EaseType(Ease.EASE_OUT, 2));
        
        // load level
        if (intro) 
            Timer.createCallTimer("updateGameToNewLevel", this, generalLevelInfo.getTransitionTime(), "setLevelInfo", level);
        else
            setLevelInfo(level);

        // update transition variable
        Timer.createCallTimer("finishTransition", this, intro && outro ? generalLevelInfo.getTotalTransitionTime() : generalLevelInfo.getTransitionTime(), "finishTransition");
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
        transitionToLevel(currentLevel, intro, outro);
    }

    // sets the level info of the gameboard to a level
    // returns if it is successful or not
    private boolean setLevelInfo(int level) {
        // load in the level
        LevelInfo levelInfo = LevelLoader.getLevelInfo("res/levels/" + level + ".json");
        if (levelInfo != null) {
            currentLevel = level;

            // clear sprites
            gameBoard.clearGameObjects();

            // clear updatables
            Updatables.deleteAllUpdatablesExcept(new String[]{ "finishTransition", "updateGameToNewLevel", "moveTransitionSpriteTween", 
                "moveTransitionSpriteDownTween", "moveTransitionSpriteUpTween" });
            
            // create the new game board
            gameBoard.setCurrentBoard(levelInfo);
            
            // update the visuals of the new game board
            gameManager.updateGameBoardVisuals();
            return true;
        }
        return false;
    }

    // updates the map data to any changes in the json files
    public void updateLevelInfo() {
        setLevelInfo(currentLevel);
    }
}
