package gameplay.mapLoading;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

// stores info about transitioning between levels
public class GeneralLevelInfo {
    
    private int startLevel;
    private int endLevel;
    private double transitionTime, waitTime;

    public GeneralLevelInfo(String filePath) throws IOException, JSONException {
        File file = new File(filePath);
        String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
        JSONObject levelObject = new JSONObject(content);

        startLevel = levelObject.getInt("startLevel");
        endLevel = levelObject.getInt("endLevel");
        transitionTime = levelObject.getDouble("transitionTime");
        waitTime = levelObject.getDouble("waitTime");
    }

    @Override
    public String toString() {
        return "GeneralLevelInfo(endLevel: " + endLevel + " | totalTransitionTime: " + getTotalTransitionTime() + ")";
    }

    public int getStartLevel() { return startLevel; }
    public int getEndLevel() { return endLevel; }
    public double getTransitionTime() { return transitionTime; }
    public double getWaitTime() { return waitTime; }
    public double getTotalTransitionTime() { return transitionTime * 2 + waitTime; }
}


/*

calc bc - 1.5
stats - 1
physics c - 1.5
english - 1
econ - 1
lunch - 1
gym - 0.5
hon art

*/