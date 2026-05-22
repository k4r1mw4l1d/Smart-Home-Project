import javax.sound.sampled.*;
import java.time.LocalTime;

public class AlarmTime implements Runnable {

    private static final String SOUND_PATH = "alarmSound/iphone_alarm.wav";
    private final LocalTime alarmTime;
    private Clip clip;

    public AlarmTime(LocalTime alarmTime) {

        this.alarmTime = alarmTime;
    }

    public void stopAlarm() {

        if (clip != null && clip.isRunning()) {

            clip.stop();
            clip.close();
        }
    }

    @Override
    public void run() {

        while (LocalTime.now().isBefore(alarmTime)) {

            try {

                Thread.sleep(1000);

            } catch (InterruptedException e) {

                System.out.println("Sleep interrupted");
            }
        }

        playSound();
    }

    private void playSound() {

        try (AudioInputStream audioStream =
                     AudioSystem.getAudioInputStream(
                             getClass().getResource(SOUND_PATH))) {

            clip = AudioSystem.getClip();

            clip.open(audioStream);

            clip.loop(Clip.LOOP_CONTINUOUSLY);

            clip.start();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}