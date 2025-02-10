package utils.drawing.particles;

import utils.math.JMath;

public class ParticleProperty {
    private double start, end;

    public ParticleProperty(double start, double end) {
        this.start = start;
        this.end = end;
    }
    private boolean isConstant() {
        return Math.abs(start - end) < JMath.THRESHOLD;
    }
    public double getValue(double normalizedTime) {
        return isConstant() ? start : JMath.simpleLerp(start, end, normalizedTime);
    }
}
