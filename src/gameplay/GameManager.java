package gameplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.*;

import javax.swing.JPanel;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import gameplay.gameObjects.GameBoard;
import gameplay.gameObjects.GameObject;
import gameplay.gameObjects.GameObjectData;
import gameplay.gameObjects.puzzlePiece.PuzzlePiece;
import gameplay.mapLoading.LevelLoader;
import gameplay.mapLoading.LevelManager;
import utils.ParentFrame;
import utils.drawing.InfoBox;
import utils.drawing.sprites.Sprite;
import utils.drawing.sprites.Sprites;
import utils.input.*;
import utils.tween.*;

public class GameManager extends JPanel {

    public static final Color BG_COLOR = new Color(230, 230, 230);
    public static final double BOARD_SCALE_RATIO = 0.85;
    public static final int INSTRUCTION_LINE_HEIGHT = 30, INSTRUCTION_FONT_SIZE = 25;

    // refresh level from json keybind
    public static final String TOGGLE_DEBUG_KEY = "Q",
            RELOAD_LEVEL_KEY = "R",
            PREV_LEVEL_KEY = "2",
            NEXT_LEVEL_KEY = "3",
            TOGGLE_GRID_LINES_KEY = "G",
            MOVE_TO_LEVEL_KEY = "Shift",
            PRINT_UPDATABLES_KEY = "U",
            INCREMENT_HDIR_KEY = "Minus",
            INCREMENT_VDIR_KEY = "Equals",
            FIND_BREAKPOINT_KEY = "0",
            PRINT_BOARD_KEY = "P";

    public static final double BUFFER_TRANSITION_TIME = 0.6;
    
    // window
    private ParentFrame window;
    private Insets contentPaneInsets;
    
    // game loop
    private int fps;
    private boolean hasGameLoopInterval;
    private long gameLoopInterval;
    protected Thread gameLoopThread;    // controls the game loop
    public boolean createdGameLoop;
    public boolean runningGameLoop;
    public double dt;

    // level management
    private LevelManager levelManager;
    public LevelManager getLevelManager() { return levelManager; }

    // debug
    public boolean showDebug;
    private InfoBox debugInfoBox;

    // input
    private KeyInput keyInput;
    public KeyInput getKeyInput() { return keyInput; }
    private MouseInput mouseInput;
    public MouseInput getMouseInput() { return mouseInput; }

    // debug MoveLogic.java IN PROGRESS
    private int hdir = 0, vdir = 0;
    private ArrayList<GameObject[]> breakpoints = new ArrayList<>();

    // get the FPS and delay based off of FPS
    public int getFPS() { return fps; }
    public long getGameLoopInterval() {
        if (!hasGameLoopInterval) {
            gameLoopInterval = (long) Math.round(1000 / fps); 
            hasGameLoopInterval = true;
        }
        return gameLoopInterval; 
    }
    
    // get window size
    public int getWindowWidth() {
        return window.getWidth();
    }
    public int getWindowHeight() {
        return window.getHeight();
    }
    public Insets getContentPaneInsets() {
        return contentPaneInsets;
    }
    public void setContentPaneInsets(Insets insets) {
        contentPaneInsets = insets;
    }

    // game board
    GameBoard gameBoard;
    String[] instructions;

    public GameManager(ParentFrame window, int framesPerSecond, KeyInput keyInput, MouseInput mouseInput) {
        this.window = window;
        this.fps = framesPerSecond;
        this.keyInput = keyInput;
        this.mouseInput = mouseInput;
    }

    // start the game
    public void startGame(int startLevel) {

        setupLayers();

        // debug info box drawer
        debugInfoBox = new InfoBox("debugInfoBox", 0, 0);
        debugInfoBox.setVisible(false);

        // setup base level properties
        LevelLoader.updateObjectData();

        loadTilemaps();

        // create game board
        gameBoard = new GameBoard(keyInput, mouseInput);
        gameBoard.setup();
        
        // load level
        levelManager = new LevelManager(this, gameBoard);
        levelManager.transitionToLevel(startLevel, false, true);

        // hide info boxes to start
        gameBoard.hideObjInfoBoxes();

        // level text
        new Sprite("uiText", "ui") {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 15));
                g.drawString("Level " + levelManager.getCurrentLevel(), gameBoard.getBoardSprite().getX(), gameBoard.getBoardSprite().getY() - 12);

                if (instructions != null) {
                    int fontSize = INSTRUCTION_FONT_SIZE;
                    g.setFont(new Font("Arial", Font.PLAIN, fontSize));
                    int totalSize = instructions.length * INSTRUCTION_LINE_HEIGHT;
                    int startY = gameBoard.getBoardSprite().getY() / 2 - totalSize / 2 + INSTRUCTION_FONT_SIZE;
                    for (int i=0; i<instructions.length; i++) {
                        int xOffset = g.getFontMetrics().stringWidth(instructions[i]) / 2;
                        g.drawString(instructions[i], gameBoard.getBoardSprite().getCenterX() - xOffset, startY + INSTRUCTION_LINE_HEIGHT * i);
                    }
                }
            }
        };

        // resize window listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateGameBoardVisuals();
            }
        });
        
        // create and start the game loop
        createGameLoop();
        startGameLoop();
    }

    private void setupLayers() {
        Sprites.addLayer("default", 0);
        Sprites.addLayer("gameBoard", 1);
        Sprites.addLayer("gameObjects1", 2);
        Sprites.addLayer("gameObjects2", 3);
        Sprites.addLayer("gameObjects3", 4);
        Sprites.addLayer("effects", 5);
        Sprites.addLayer("ui", 6);
        Sprites.addLayer("transitions", 7);
        Sprites.addLayer("debug", 8);
    }

    private void loadTilemaps() {
        PuzzlePiece.loadTilemaps();
    }

    public void setInstructions(String[] instructions) {
        this.instructions = instructions;
    }

    // create the game loop
    public void createGameLoop() {
        gameLoopThread = new Thread() {
            @Override
            public void run() {

                long prevTime = System.currentTimeMillis();
                long currentTime = System.currentTimeMillis();

                while (runningGameLoop) {
                    prevTime = currentTime;
                    currentTime = System.currentTimeMillis();
                    dt = (currentTime - prevTime) / 1000.;

                    // update input
                    keyInput.update();
                    mouseInput.update();
                    mouseInput.setInsets(window.getInsets());

                    // update background systems
                    Updatables.updateUpdatables(dt);

                    updateGame(dt);                                             // update function
                    repaint();                                                // draw function

                    // delay between current frame and next frame
                    try {
                        Thread.sleep(getGameLoopInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        stopGameLoop();
                    }
                }
            }
        };
        createdGameLoop = true;
    }
    
    // start the game loop
    public void startGameLoop() {
        runningGameLoop = true;
        gameLoopThread.start();
    }

    // stop the game loop
    public void stopGameLoop() {
        runningGameLoop = false;
    }

    public void updateGame(double dt) {

        // update game board and game objects
        gameBoard.update(dt);

        // go to next level
        if (gameBoard.allPuzzlePiecesConnected() 
        && !levelManager.transitioningBetweenLevels() && levelManager.hasLevel(levelManager.getCurrentLevel() + 1)
        && !Updatables.hasUpdatable("call level transition")) 
            Timer.createCallTimer("call level transition", levelManager, BUFFER_TRANSITION_TIME, "transitionToNextLevel", true, true);

        if (keyInput.keyClicked(INCREMENT_HDIR_KEY)) 
            if (++hdir == 2) 
                hdir = -1;
        if (keyInput.keyClicked(INCREMENT_VDIR_KEY)) 
            if (++vdir == 2) 
                vdir = -1;
        if (keyInput.keyClicked(FIND_BREAKPOINT_KEY)) {
            //breakpoints = MoveLogic.findBreakpoints(gameBoard, gameBoard.getPlayerPiece(), hdir, vdir);
        }

        if (keyInput.keyClicked(TOGGLE_GRID_LINES_KEY))
            gameBoard.toggleGridVisible();



        // print the game board
        if (keyInput.keyClicked(PRINT_BOARD_KEY))
            gameBoard.printBoard();

        // refresh map data from json files
        if (keyInput.keyClicked(RELOAD_LEVEL_KEY)) {
            levelManager.updateGeneralLevelInfo();
            GameObjectData.loadObjectData();
            levelManager.updateLevelInfo();
            loadTilemaps();
        }

        // keybinds to move through levels
        if (keyInput.keyClicked(PREV_LEVEL_KEY)) 
            levelManager.transitionToLevel(levelManager.getCurrentLevel() - 1, false, false);
        if (keyInput.keyClicked(NEXT_LEVEL_KEY)) 
            levelManager.transitionToNextLevel(false, false);
        
        // show/hide debug info
        if (keyInput.keyClicked(TOGGLE_DEBUG_KEY)) {
            showDebug = !showDebug;
            debugInfoBox.setVisible(showDebug);
        }
        if (showDebug) updateDebugDrawList();

        if (keyInput.keyClicked(PRINT_UPDATABLES_KEY))
            Updatables.setAllowPrint(!Updatables.getAllowPrint());
    }

    // swing's built in draw function for UI components
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;

        if (createdGameLoop) {
            // clear screen
            g.setColor(BG_COLOR);
            g.fillRect(0, 0, getWindowWidth(), getWindowHeight());

            // draw game sprites
            Sprites.drawSprites(g2);
        }
    }

    // update game board visuals when map reloads
    public void updateGameBoardVisuals() {
        int boardCenterX = getWidth() / 2;
        int boardCenterY = getHeight() / 2;
        int boardWidth = (int) (getWidth() * BOARD_SCALE_RATIO);
        int boardHeight = (int) (getHeight() * BOARD_SCALE_RATIO);

        // making sure there is enough room for any possible instructions
        if (instructions != null) {
            int neededInstructionSpace = INSTRUCTION_LINE_HEIGHT * instructions.length;

            int currentInstructionSpace = (getHeight() - boardHeight) / 2;
            if (currentInstructionSpace < neededInstructionSpace) {
                int yOffset = neededInstructionSpace - currentInstructionSpace;
                boardHeight -= yOffset;
            }
        }
        
        gameBoard.updateBoardVisuals(boardCenterX, boardCenterY, boardWidth, (int) boardHeight);
    }

    private void updateDebugDrawList() {
        ArrayList<String> drawList = new ArrayList<>();
        addDebugGeneral(drawList);
        //addDebugInput(drawList);
        addDebugControls(drawList);
        //addDebugUpdatables(drawList);
        //addDebugSprites(drawList);
        //addDebugLevel(drawList);
        //addDebugGameObjects(drawList);
        //addDebugMovement(drawList);
        
        debugInfoBox.setDrawList(drawList);
    }

    private void addDebugGeneral(ArrayList<String> drawList) {
        drawList.add("===GENERAL===");
        drawList.add("dt: " + dt);
        drawList.add("window size: (" + getWidth() + ", " + getHeight() + ")");
         drawList.add("mouse pos: (" + mouseInput.getX() + ", " + mouseInput.getY() + ")");
        drawList.add("gameManager insets: " + getInsets().left + ", " + getInsets().top);
        drawList.add("contentPane insets: " + getContentPaneInsets().left + ", " + getContentPaneInsets().top);
        drawList.add("main insets: " + window.getInsets().left + ", " + window.getInsets().top);
    }
    @SuppressWarnings("unused")
    private void addDebugInput(ArrayList<String> drawList) {
        drawList.add("===INPUT===");
        InputBase.State[] states = { 
            InputBase.State.DOWN, 
            InputBase.State.CLICKED, 
            InputBase.State.RELEASED 
        };
        for (InputBase.State state : states) {
            ArrayList<String> keyList = keyInput.getAllKeys(state);
            if (keyList.size() > 0) {
                String keyString = "";
                for (String keyName : keyList) keyString += keyName + ", ";
                keyString = keyString.substring(0, keyString.length() - 2);
                drawList.add("keys " + state.toString().toLowerCase() + ": " + keyString);
            } else {
                drawList.add("keys " + state.toString().toLowerCase() + ": none");
            }
        }
    }
    @SuppressWarnings("unused")
    private void addDebugUpdatables(ArrayList<String> drawList) {
        drawList.add("===UPDATABLES===");
        drawList.add("can print updatables: " + Updatables.getAllowPrint());
        drawList.add("amount of updatables: " + Updatables.getUpdatables().size());
        for (Updatable updatable : Updatables.getUpdatables()) 
            drawList.add("" + updatable.getName());
    }
    @SuppressWarnings("unused")
    private void addDebugSprites(ArrayList<String> drawList) {
        drawList.add("===SPRITES===");
        HashMap<String, Integer> spriteCounters = new HashMap<>();
        for (Sprite sprite : Sprites.getSprites()) 
            if (spriteCounters.containsKey(sprite.getName()))
                spriteCounters.put(sprite.getName(), spriteCounters.get(sprite.getName()) + 1);
            else
                spriteCounters.put(sprite.getName(), 1);
        
        for (String spriteName : spriteCounters.keySet()) 
            drawList.add(spriteName + ": " + spriteCounters.get(spriteName));
        
    }
    @SuppressWarnings("unused")
    private void addDebugControls(ArrayList<String> drawList) {
        drawList.add("===CONTROLS===");
        drawList.add(TOGGLE_DEBUG_KEY + ": toggle debug info");
        drawList.add(PRINT_BOARD_KEY + ": print game board (objType idx)");
        drawList.add(PRINT_UPDATABLES_KEY + ": print updatables");
        drawList.add(RELOAD_LEVEL_KEY + ": refresh level json file");
        drawList.add(PREV_LEVEL_KEY + ": go to prev lvl");
        drawList.add(NEXT_LEVEL_KEY + ": go to next lvl");
        drawList.add(TOGGLE_GRID_LINES_KEY + ": toggle grid lines visibility");
        drawList.add(INCREMENT_HDIR_KEY + ": increment breakpoint hdir");
        drawList.add(INCREMENT_VDIR_KEY + ": increment breakpoint vdir");
        drawList.add(FIND_BREAKPOINT_KEY + ": find breakpoints from player pos, hdir, and vdir");
    }
    @SuppressWarnings("unused")
    private void addDebugLevel(ArrayList<String> drawList) {
        drawList.add("===LEVEL===");
        drawList.add("current level: " + levelManager.getCurrentLevel());
        drawList.add("Map Size: (" + gameBoard.getBoardColumns() + ", " + gameBoard.getBoardRows() + ")");
        drawList.add("level succeeded: " + gameBoard.allPuzzlePiecesConnected());
        drawList.add("transitioning: " + levelManager.transitioningBetweenLevels());
        drawList.add("tile size: " + gameBoard.getTileSize());
        drawList.add("game board draw pos: (" + gameBoard.getBoardSprite().getX() + ", " + gameBoard.getBoardSprite().getY() + ")");
    }
    @SuppressWarnings("unused")
    private void addDebugGameObjects(ArrayList<String> drawList) {
        drawList.add("===GAME OBJECTS===");
        drawList.add("number: " + gameBoard.getGameObjects().size());
        for (GameObject gameObject : gameBoard.getGameObjects()) {
            drawList.add("object: " + gameObject.getObjectType() + " | pos: (" + gameObject.getBoardX() + ", " + gameObject.getBoardY() + ")");
        }
    }
    @SuppressWarnings("unused")
    private void addDebugMovement(ArrayList<String> drawList) {
        drawList.add("===MOVEMENT===");
        drawList.add("dir to check: (" + hdir + ", " + vdir + ")");
        if (breakpoints != null) {
            drawList.add("breakpoints: ");
            for (GameObject[] gameObjects : breakpoints)
                drawList.add("breakpoint: " + gameObjects[0] + ", " + gameObjects[1]);
        }
        else 
            drawList.add("breakpoints: none - invalid movement");
    }
}
