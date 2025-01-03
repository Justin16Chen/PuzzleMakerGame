package utils.drawing.particles;

public class NumberProperty {
    
    private double min, max;

    public NumberProperty(double value) {
        this.min = value;
        this.max = value;
    }
    public NumberProperty(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }
    public double getMax() {
        return max;
    }
    public double makeValue() {
        return Math.random() * (max - min) + min;
    }
}
