package utils.drawing.particles;

import utils.JMath;

public class Particle {
    
    private double x, y, vx, vy;
    private ParticleProperty angle, speed, size;
    private double startTime, lifeTime, normalizedCurrentTime;

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;
        vx = 0;
        vy = 0;
        startTime = 0;
        lifeTime = 0;
        normalizedCurrentTime = 0;
    }
    public void setAngle(double start, double end) {
        angle = new ParticleProperty(start, end);
    }
    public void setSpeed(double start, double end) {
        speed = new ParticleProperty(start, end);
    }
    public void setSize(double start, double end) {
        size = new ParticleProperty(start, end);
    }
    public void setLifeTime(double lifeTime) {
        this.lifeTime = lifeTime;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getVx() {
        return vx;
    }
    public void setVx(double vx) {
        this.vx = vx;
    }
    public double getVy() {
        return vy;
    }
    public void setVy(double vy) {
        this.vy = vy;
    }
    public double getAngle() {
        return angle.getValue(normalizedCurrentTime);
    }
    public double getSpeed() {
        return speed.getValue(normalizedCurrentTime);
    }
    public double getSize() {
        return size.getValue(normalizedCurrentTime);
    }

    public void update(double currentTime) {
        x += vx;
        y += vy;
        normalizedCurrentTime = JMath.normalize(startTime, lifeTime, currentTime);
    }

    public boolean isDone() {
        return normalizedCurrentTime >= 1;
    }
}
