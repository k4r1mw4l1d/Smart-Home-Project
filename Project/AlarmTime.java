import javax.sound.sampled.*;
import java.time.LocalTime;
import java.util.Scanner;

public class AlarmTime implements Runnable {

    private static final String SOUND_PATH = "/resources/alarmSound/iphone_alarm.wav";
    private final LocalTime alarmTime;
    private final Scanner scan;

    AlarmTime(LocalTime alarmTime, Scanner scan) {
        this.alarmTime = alarmTime;
        this.scan = scan;


    }

    @Override
    public void run() {

        while (LocalTime.now().isBefore(alarmTime)) {
            try {
                Thread.sleep(1000);

                LocalTime now = LocalTime.now();
                System.out.printf("\r%02d:%02d:%02d",
                        now.getHour(),
                        now.getMinute(),
                        now.getSecond());

            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted");
            }
        }

        System.out.println("\n*ALARM NOISES*");
        playSound();
    }

    private void playSound() {

        try (AudioInputStream audioStream =
                     AudioSystem.getAudioInputStream(
                             getClass().getResource(SOUND_PATH))) {

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            System.out.print("Press *Enter* to stop the alarm: ");
            scan.nextLine();

            clip.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
