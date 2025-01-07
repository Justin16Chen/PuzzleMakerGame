package utils.drawing.particles;

import java.awt.Graphics2D;

import utils.drawing.sprites.Sprite;

public class ParticleGroup extends Sprite {
    
    private Particle[] particles;
    private NumberProperty angleStart, angleEnd,
        speedStart, speedEnd, 
        sizeStart, sizeEnd,
        lifeTime;
    private ColorProperty color;
    public ParticleGroup(String name, int x, int y, String layerName) {
        super(name, x, y, 1, 1, layerName);
    }

    public ParticleGroup setParticleNumber(int num) {
        particles = new Particle[num];
        return this;
    }
    public ParticleGroup setAngle(NumberProperty angle) {
        angleStart = angle;
        angleEnd = angle;
        return this;
    }
    public ParticleGroup setAngle(NumberProperty angleStart, NumberProperty angleEnd) {
        this.angleStart = angleStart;
        this.angleEnd = angleEnd;
        return this;
    }
    public ParticleGroup setSpeed(NumberProperty speed) {
        speedStart = speed;
        speedEnd = speed;
        return this;
    }
    public ParticleGroup setSpeed(NumberProperty speedStart, NumberProperty speedEnd) {
        this.speedStart = speedStart;
        this.speedEnd = speedEnd;
        return this;
    }
    public ParticleGroup setSize(NumberProperty size) {
        sizeStart = size;
        sizeEnd = size;
        return this;
    }
    public ParticleGroup setSize(NumberProperty sizeStart, NumberProperty sizeEnd) {
        this.sizeStart = sizeStart;
        this.sizeEnd = sizeEnd;
        return this;
    }
    public ParticleGroup setLifeTime(NumberProperty lifeTime) {
        this.lifeTime = lifeTime;
        return this;
    }
    
    public ParticleGroup setColor(ColorProperty color) {
        this.color = color;
        return this;
    }

    public void createParticles() {
        for (int i=0; i<particles.length; i++) {
            particles[i] = new Particle(getX(), getY());
            particles[i].setAngle(angleStart.makeValue(), angleEnd.makeValue());
            particles[i].setSpeed(speedStart.makeValue(), speedEnd.makeValue());
            particles[i].setSize(sizeStart.makeValue(), sizeEnd.makeValue());
            particles[i].setLifeTime(lifeTime.makeValue());
        }
    }

    @Override
    public void draw(Graphics2D g) {
        double time = System.currentTimeMillis() / 1000.0;
        for (int i=0; i<particles.length; i++) {
            particles[i].update(time);
            
        }
    }
}
