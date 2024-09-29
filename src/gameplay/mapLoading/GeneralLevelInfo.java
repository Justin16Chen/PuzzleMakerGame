package gameplay.mapLoading;

public class GeneralLevelInfo {
    
    private int lastLevel;
    private double transitionTime;

    public GeneralLevelInfo(int lastLevel, double transitionTime) {
        this.lastLevel = lastLevel;
        this.transitionTime = transitionTime;
    }

    @Override
    public String toString() {
        return "GeneralLevelInfo(lastLevel: " + lastLevel + " | transitionTime: " + transitionTime + ")";
    }

    public int getLastLevel() { return lastLevel; }
    public double getTransitionTime() { return transitionTime; }
}
