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
    private Sprite transitionTextBgSprite;

    public LevelManager(GameManager gameManager, GameBoard gameBoard) {
        this.gameManager = gameManager;
        this.gameBoard = gameBoard;
        updateGeneralLevelInfo();
        GameObjectData.loadObjectData();
    }
    public void setup() {
        transitionSprite = new Sprite("transition", 0, 0, gameManager.getWidth(), 0, "transitions");
        transitionSprite.setColor(Color.BLACK);
        transitionSprite.addTag("necessary");
        transitionTextBgSprite = new Sprite("transitionTextBg", 0, 0, gameManager.getWidth(), 0, "transitions");
        transitionTextBgSprite.setColor(new Color(30, 30, 30));
        transitionTextBgSprite.addTag("necessary");
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

        //Updatables.deleteUpdatables(new String[]{"moveTransitionSpriteTween", "moveTransitionSpriteDownTween", "moveTransitionSpriteUpTween", "finishTransition", "updateGameToNewLevel"}); // clear any updatables from previous transitions

        // sprite transition animation
        transitionSprite.setVisible(true);
        if (intro && outro) {
            transitionDown("");
            Timer.createCallTimer("transitionUpTimer", this, generalLevelInfo.getTransitionTime() + generalLevelInfo.getWaitTime(""), "transitionUp");
        }
        else if (intro)
            transitionDown("");
        else if (outro)
            transitionUp(); 
                   
        // load level
        if (intro) 
            Timer.createCallTimer("updateGameToNewLevel", this, generalLevelInfo.getTransitionTime(), "setLevelInfo", level);
        else 
            setLevelInfo(level);
    }

    @SuppressWarnings("unused")
    private void transitionText(String transitionText) {
        double waitTime = generalLevelInfo.getWaitTime(transitionText);
        transitionTextBgSprite.setY((int) (gameManager.getHeight() * 0.5));
        Tween.createTween("transitionTextHeight", transitionTextBgSprite, "height", 0, gameManager.getHeight() * 0.16, waitTime * 0.3).setEaseType(new EaseType(Ease.EASE_OUT));
        Tween.createTween("transitionTextY", transitionTextBgSprite, "y", gameManager.getHeight() * 0.5, gameManager.getHeight() * 0.42, waitTime * 0.3).setEaseType(new EaseType(Ease.EASE_OUT));
        Timer.createCallTimer("hideTransitionText", this, waitTime * 0.7, "hideTransitionText", transitionText);
    }
    @SuppressWarnings("unused")
    private void hideTransitionText(String transitionText) {
        double waitTime = generalLevelInfo.getWaitTime(transitionText);
        Tween.createTween("transitionTextHeight", transitionTextBgSprite, "height", transitionTextBgSprite.getHeight(), 0, waitTime * 0.3).setEaseType(new EaseType(Ease.EASE_IN));
        Tween.createTween("transitionTextY", transitionTextBgSprite, "y", transitionTextBgSprite.getY(), gameManager.getHeight() * 0.5, waitTime * 0.3).setEaseType(new EaseType(Ease.EASE_IN));
    }
    public void transitionDown(String transitionText) {
        transitionSprite.setWidth(gameManager.getWidth());
        transitionSprite.setHeight(gameManager.getHeight());
        transitioning = true;
        Tween.createTween("moveTransitionSpriteTween", transitionSprite, "height", 1, gameManager.getHeight(), generalLevelInfo.getTransitionTime()).setEaseType(new EaseType(Ease.EASE_OUT, 2));
        if (transitionText.length() > 0)
            Timer.createCallTimer("showTransitionText", this, generalLevelInfo.getTransitionTime() * 0.9, "transitionText", transitionText);
    }
    public void transitionUp() {
        transitionSprite.setWidth(gameManager.getWidth());
        transitionSprite.setHeight(gameManager.getHeight());
        transitioning = true;
        Tween.createTween("moveTransitionSpriteUpTween", transitionSprite, "height", gameManager.getHeight(), 1, generalLevelInfo.getTransitionTime()).setEaseType(new EaseType(Ease.EASE_OUT, 2));
        Timer.createSetTimer("finish transition", this, generalLevelInfo.getTotalTransitionTime(), "transitioning", false);
    }
    public void transitionUpSlow() {
        transitionSprite.setWidth(gameManager.getWidth());
        transitionSprite.setHeight(gameManager.getHeight());
        transitioning = true;
        Tween.createTween("moveTransitionSpriteUpTween", transitionSprite, "height", gameManager.getHeight(), 1, generalLevelInfo.getTransitionTime() + 0.4).setEaseType(new EaseType(Ease.EASE_OUT, 2));
        Timer.createSetTimer("finish transition", this, generalLevelInfo.getTotalTransitionTime(), "transitioning", false);
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
    public boolean setLevelInfo(int level) {
        // load in the level
        LevelInfo levelInfo = LevelLoader.getLevelInfo("res/levels/" + level + ".json");
        if (levelInfo != null) {
            currentLevel = level;

            // clear sprites
            gameBoard.clearGameObjects();
            
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
