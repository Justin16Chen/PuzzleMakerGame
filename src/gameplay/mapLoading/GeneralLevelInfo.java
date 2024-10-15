package gameplay.mapLoading;

public class GeneralLevelInfo {
    
    private int startLevel;
    private int lastLevel;
    private double transitionTime;

    public GeneralLevelInfo(int startLevel, int lastLevel, double transitionTime) {
        this.startLevel = startLevel;
        this.lastLevel = lastLevel;
        this.transitionTime = transitionTime;
    }

    @Override
    public String toString() {
        return "GeneralLevelInfo(lastLevel: " + lastLevel + " | transitionTime: " + transitionTime + ")";
    }

    public int getStartLevel() { return startLevel; }
    public int getLastLevel() { return lastLevel; }
    public double getTransitionTime() { return transitionTime; }
}
