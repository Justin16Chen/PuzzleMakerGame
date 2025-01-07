package utils.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class KeyInput extends KeyAdapter {

    public static final KeyInput NOTHING_INPUT = new KeyInput(Type.NOTHING);

    private enum Type {
        NOTHING,
        ACTIVE
    }

    private ArrayList<String> keyList = new ArrayList<String>(Arrays.asList(
        "Back Quote","1","2","3","4","5","6","7","8","9","0","Minus","Equals","Backspace",
        "Q","W","E","R","T","Y","U","I","O","P","Open Bracket","Close Bracket","Back Slash",
        "Caps Lock","A","S","D","F","G","H","J","K","L","Semicolon","Quote","Enter",
        "Shift","Z","X","C","V","B","N","M","Comma","Period","Slash",
        "Ctrl","Windows","Alt","Space","Page Up","Page Down","Up","Left","Down","Right"
    ));

    private HashMap<String, Key> keyMap = new HashMap<String, Key>();
    private Type type;
    
    public KeyInput() {
        setupKeyMap();
        type = Type.ACTIVE;
    }
    public KeyInput(Type type) {
        setupKeyMap();
        this.type = type;
    }

    // setup key map from key list
    private void setupKeyMap() {
        for (String key : keyList) {
            keyMap.put(key, new Key(key));
        }
    }
    
    // update all keys
    public void update() {
        if (type == Type.NOTHING)
            return;

        for (String keyName : keyList) {
            keyMap.get(keyName).update();
        }
    }

    // key input getters
    public HashMap<String, Key> getKeyMap() {
        return keyMap;
    }
    public boolean keyDown(String keyName) {
        return keyMap.get(keyName).down();
    }
    public int keyDownInt(String keyName) {
        return keyDown(keyName) ? 1 : 0;
    }
    public boolean keyClicked(String keyName) {
        return keyMap.get(keyName).clicked();
    }
    public int keyClickedInt(String keyName) {
        return keyClicked(keyName) ? 1 : 0;
    }
    public boolean keyReleased(String keyName) {
        return keyMap.get(keyName).released();
    }
    public int keyReleasedInt(String keyName) {
        return keyReleased(keyName) ? 1 : 0;
    }
    public ArrayList<String> getAllKeys(InputBase.State keyState) {
        ArrayList<String> keyNames = new ArrayList<>();

        for (String keyName : keyList) {
            Key key = keyMap.get(keyName);

            if (key.getState() == keyState) keyNames.add(keyName);
        }
        return keyNames;
    }

    public String parseKeyEvent(KeyEvent e) {
        // convert ascii value to character
        return KeyEvent.getKeyText(e.getKeyCode());
    }

    // recieve key input
    @Override
    public void keyPressed(KeyEvent e) {
        if (type == Type.NOTHING)
            return;
        keyMap.get(parseKeyEvent(e)).setDown(true);
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (type == Type.NOTHING)
            return;
        keyMap.get(parseKeyEvent(e)).setDown(false);
    }
}
