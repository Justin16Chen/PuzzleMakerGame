package gameplay.mapLoading;

public class GeneralLevelInfo {
    
    private int numLevels;
    private double transitionTime;

    public GeneralLevelInfo(int numLevels, double transitionTime) {
        this.numLevels = numLevels;
        this.transitionTime = transitionTime;
    }

    @Override
    public String toString() {
        return "GeneralLevelInfo(numLevels: " + numLevels + " | transitionTime: " + transitionTime + ")";
    }

    public int getNumLevels() { return numLevels; }
    public double getTransitionTime() { return transitionTime; }
}
