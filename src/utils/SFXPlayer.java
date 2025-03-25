package utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SFXPlayer {
    public static void main(String[] args) {
        play("res/sfx/player move.wav"); // Replace with your file path
    }

    public static void play(String filePath) {
        try {
            // Open audio file
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            
            // Get a sound clip resource
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // Start playback
            clip.start();
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public static void play(String filePath, double volume) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Get volume control
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue((float) volume); // Value in decibels (-80 to 6 dB)

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void play(String filePath, double volume, double randomPitchRange) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Get volume control
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue((float) volume); // Value in decibels (-80 to 6 dB)

            // Randomize pitch by adjusting sample rate
            if (clip.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
                FloatControl sampleRateControl = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
                float baseRate = sampleRateControl.getValue();
                float randomFactor = (float) (1 + Math.random() * randomPitchRange / 2);
                sampleRateControl.setValue(baseRate * randomFactor);
            }
            else 
                throw new IllegalArgumentException("sfx at " + filePath + " does not support random pitch");

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
