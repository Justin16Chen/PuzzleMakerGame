package gameplay;

import gameplay.mapLoading.GameLoader;
import gameplay.mapLoading.MapInfo;
import utils.drawing.SimpleSprite;
import utils.tween.Timer;
import utils.tween.Tween;

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

    public void playIntro() {
        transitionSprite.setVisible(true);
        Tween.createTween("intro", transitionSprite, "height", 0, (int) gameManager.getHeight(), 0.3);
    }
    public void playOutro() {
        transitionSprite.setVisible(true);
        Tween.createTween("outro", transitionSprite, "height", (int) gameManager.getHeight(), 0, 0.3);
    }
    public void transitionToLevel(Integer level, boolean intro, boolean outro) {
        currentLevel = level;
        transitioning = true;
            currentLevel = level;
            if (intro) {
                playIntro();
                Timer.createCallTimer("call setLevel in transitionToLevel", this, 0.35, "setLevel", currentLevel);
                if (outro) {
                    Timer.createCallTimer("play outro", this, 0.4, "playOutro");
                }
            } else {
                setLevel(currentLevel);
                if (outro) {
                    playOutro();
                }
            }
    }
    public void transitionToNextLevel(boolean intro, boolean outro) {
        transitioning = true;
        currentLevel++;
        transitionToLevel(currentLevel, true, true);
    }
    private void setLevel(int level) {
        Timer.createSetTimer("set transition to false", this, 0.35, "transitioning", false);

        // load in the level
        try {
            MapInfo mapInfo = GameLoader.getMapInfo(level + ".json", gameBoard);
            gameBoard.setCurrentBoard(mapInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
