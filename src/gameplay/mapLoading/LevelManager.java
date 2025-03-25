package gameplay.mapLoading;

import java.awt.Color;
import java.awt.Graphics2D;

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
    private Sprite transitionTextBgSprite;

    public LevelManager(GameManager gameManager, GameBoard gameBoard) {
        this.gameManager = gameManager;
        this.gameBoard = gameBoard;
        transitionSprite = new Sprite("transition", 0, 0, gameManager.getWidth(), gameManager.getHeight(), "transitions");
        transitionSprite.setColor(Color.BLACK);
        transitionTextBgSprite = new Sprite("transitionTextBg", 0, 0, gameManager.getWidth(), 0, "transitions");
        transitionTextBgSprite.setColor(new Color(30, 30, 30));
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
        if (intro && outro) {
            transitionDown();
            Timer.createCallTimer("showTransitionText", this, generalLevelInfo.getTransitionTime() * 0.9, "transitionText");
            Timer.createCallTimer("transitionUpTimer", this, generalLevelInfo.getTransitionTime() + 1.2, "transitionUp");
        }
        else if (intro)
            transitionDown();
        else if (outro)
            transitionUp();        
        // load level
        if (intro) {
            transitionSprite.setWidth(gameManager.getWidth());
            transitionSprite.setHeight(gameManager.getHeight());
            Timer.createCallTimer("updateGameToNewLevel", this, generalLevelInfo.getTransitionTime(), "setLevelInfo", level);
        } else 
            setLevelInfo(level);

        // update transition variable
        Timer.createCallTimer("finishTransition", this, intro && outro ? generalLevelInfo.getTotalTransitionTime() : generalLevelInfo.getTransitionTime(), "finishTransition");
    }

    private void transitionText() {
        transitionTextBgSprite.setY((int) (gameManager.getHeight() * 0.5));
        Tween.createTween("transitionTextHeight", transitionTextBgSprite, "height", 0, gameManager.getHeight() * 0.15, 0.3).setEaseType(new EaseType(Ease.EASE_OUT));
        Tween.createTween("transitionTextY", transitionTextBgSprite, "y", gameManager.getHeight() * 0.5, gameManager.getHeight() * 0.35, 0.3).setEaseType(new EaseType(Ease.EASE_OUT));
        Timer.createCallTimer("hideTransitionText", this, 0.9, "hideTransitionText");
    }
    private void hideTransitionText() {
        Tween.createTween("transitionTextHeight", transitionTextBgSprite, "height", transitionTextBgSprite.getHeight(), 0, 0.3).setEaseType(new EaseType(Ease.EASE_IN));
        Tween.createTween("transitionTextY", transitionTextBgSprite, "y", transitionTextBgSprite.getY(), gameManager.getHeight() * 0.5, 0.3).setEaseType(new EaseType(Ease.EASE_IN));
    }
    private void transitionDown() {
        Tween.createTween("moveTransitionSpriteTween", transitionSprite, "height", 1, gameManager.getHeight(), generalLevelInfo.getTransitionTime()).setEaseType(new EaseType(Ease.EASE_OUT, 2));
    }
    private void transitionUp() {
        System.out.println("TRANSITION UP");
        Tween.createTween("moveTransitionSpriteUpTween", transitionSprite, "height", gameManager.getHeight(), 1, generalLevelInfo.getTransitionTime()).setEaseType(new EaseType(Ease.EASE_OUT, 2));
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
                "moveTransitionSpriteDownTween", "moveTransitionSpriteUpTween", "transitionUpTimer", 
                "showTransitionText", "transitionTextHeight", "transitionTextY", "hideTransitionText" });
            
            // create the new game board
            gameBoard.setCurrentBoard(levelInfo);
            
            // set any instructions for the level (not all levels have instructions)
            gameManager.setInstructions(levelInfo.getInstructions());

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
