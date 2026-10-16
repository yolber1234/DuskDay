package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {
    private static Clip backgroundClip;
    private static Clip bossClip;

    public static void playSound(String soundFilePath, boolean loop) {
        try {
            InputStream is = SoundManager.class.getResourceAsStream(soundFilePath);
            if (is == null) {
                System.out.println("No se encontró el archivo de audio: " + soundFilePath);
                return;
            }
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playSoundThen(String firstSoundPath, Runnable onFinished) {
        try {
            InputStream is = SoundManager.class.getResourceAsStream(firstSoundPath);
            if (is == null) return;

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    if (onFinished != null) {
                        onFinished.run();
                    }
                }
            });

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playBackgroundMusic(String soundFilePath) {
        stopBackgroundMusic();
        try {
            InputStream is = SoundManager.class.getResourceAsStream(soundFilePath);
            if (is == null) return;

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInput);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }

    public static void playBossMusic(String soundFilePath) {
        if (bossClip != null && bossClip.isRunning()) return;
        stopBackgroundMusic();
        try {
            InputStream is = SoundManager.class.getResourceAsStream(soundFilePath);
            if (is == null) return;

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            bossClip = AudioSystem.getClip();
            bossClip.open(audioInput);
            bossClip.loop(Clip.LOOP_CONTINUOUSLY);
            bossClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopBossMusic() {
        if (bossClip != null && bossClip.isRunning()) {
            bossClip.stop();
            bossClip.close();
        }
    }
}