package gameplay.mapLoading;

// stores info about transitioning between levels
public class GeneralLevelInfo {
    
    private int startLevel;
    private int endLevel;
    private double transitionTime;

    public GeneralLevelInfo(int startLevel, int endLevel, double transitionTime) {
        this.startLevel = startLevel;
        this.endLevel = endLevel;
        this.transitionTime = transitionTime;
    }

    @Override
    public String toString() {
        return "GeneralLevelInfo(endLevel: " + endLevel + " | transitionTime: " + transitionTime + ")";
    }

    public int getStartLevel() { return startLevel; }
    public int getendLevel() { return endLevel; }
    public double getTransitionTime() { return transitionTime; }
}
