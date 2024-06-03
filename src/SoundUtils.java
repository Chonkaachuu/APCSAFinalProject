import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundUtils {

    private static Clip currentClip;
    private static Clip originalClip;
    private static Clip hiddenClip;
    private static long clipPosition = 0;
    private static boolean isPaused = false;
    private static boolean isOriginalPaused = false;

    public static void playSound(String soundFileName) {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
        }
        playClip(soundFileName, false);
    }

    public static void loopSound(String soundFileName) {
        if (originalClip != null && originalClip.isRunning()) {
            originalClip.stop();
        }
        playOriginalClip(soundFileName);
    }

    private static void playClip(String soundFileName, boolean loop) {
        try (InputStream audioSrc = SoundUtils.class.getResourceAsStream(soundFileName);
             InputStream bufferedIn = new BufferedInputStream(audioSrc)) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            currentClip = clip;
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private static void playOriginalClip(String soundFileName) {
        try (InputStream audioSrc = SoundUtils.class.getResourceAsStream(soundFileName);
             InputStream bufferedIn = new BufferedInputStream(audioSrc)) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            originalClip = clip;
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void pauseSound() {
        if (currentClip != null && currentClip.isRunning()) {
            clipPosition = currentClip.getMicrosecondPosition();
            currentClip.stop();
            isPaused = true;
        } else if (originalClip != null && originalClip.isRunning()) {
            clipPosition = originalClip.getMicrosecondPosition();
            originalClip.stop();
            isOriginalPaused = true;
        }
    }

    public static void resumeSound() {
        if (currentClip != null && isPaused) {
            currentClip.setMicrosecondPosition(clipPosition);
            currentClip.start();
            isPaused = false;
        } else if (originalClip != null && isOriginalPaused) {
            originalClip.setMicrosecondPosition(clipPosition);
            originalClip.start();
            originalClip.loop(Clip.LOOP_CONTINUOUSLY);
            isOriginalPaused = false;
        }
    }

    public static void stopSound() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            isPaused = false;
        } else if (originalClip != null) {
            originalClip.stop();
            originalClip.close();
            originalClip = null;
            isOriginalPaused = false;
        }
    }

    public static void playHiddenSound(String soundFileName) {
        if (hiddenClip != null && hiddenClip.isRunning()) {
            hiddenClip.stop();
            hiddenClip.close();
        }
        try (InputStream audioSrc = SoundUtils.class.getResourceAsStream(soundFileName);
             InputStream bufferedIn = new BufferedInputStream(audioSrc)) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            hiddenClip = AudioSystem.getClip();
            hiddenClip.open(audioStream);
            hiddenClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void stopHiddenSound() {
        if (hiddenClip != null && hiddenClip.isRunning()) {
            hiddenClip.stop();
            hiddenClip.close();
            hiddenClip = null;
        }
    }
}
