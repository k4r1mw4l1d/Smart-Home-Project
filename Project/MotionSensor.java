/*
 *    ============Motion Sensor============
 *    consists of : 1. PIR motion detection.
 *                  2. Sensitivity level control.
 *                  3. Active / Inactive zones.
 *                  4. Alert & callback to linked devices.
 *                  5. Detection history log.
 */

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MotionSensor extends SmartDevice implements Alertable {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty sensorArmed = new SimpleBooleanProperty(true);
    private final IntegerProperty detectionCount = new SimpleIntegerProperty(0);
    private final List<String> alertHistory = new ArrayList<>();
    private final List<String> detectionHistory = new ArrayList<>();
    private Sensitivity sensitivity = Sensitivity.MEDIUM;
    /**
     * Optional callback fired on every new motion event — link cameras, alarms, etc.
     */
    private Consumer<String> onMotionCallback;

    // ──────Constructor───────────────────────────────────────
    public MotionSensor(String deviceId, String name, String room,
                        boolean armed, Sensitivity sensitivity) {
        super(deviceId, name, room);
        this.sensorArmed.set(armed);
        this.sensitivity = sensitivity;

        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format(
                "Motion=%s Armed=%s Sensitivity=%s TotalDetections=%d",
                isSensorArmed() ? "ARMED" : "DISARMED",
                sensitivity.name(),
                detectionCount.get()));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {
        switch (cmd.toLowerCase()) {

            case "arm" -> {
                setSensorArmed(true);
                updateStatus("Sensor ARMED");
            }
            case "disarm" -> {
                setSensorArmed(false);
                updateStatus("Sensor DISARMED");
            }
            case "motion detected" -> {
                updateStatus("Motion DETECTED");
            }
            case "motion clear" -> {
                updateStatus("Motion CLEARED");
            }
            case "sensitivity low" -> {
                sensitivity = Sensitivity.LOW;
                updateStatus("Sensitivity set to LOW");
            }
            case "sensitivity medium" -> {
                sensitivity = Sensitivity.MEDIUM;
                updateStatus("Sensitivity set to MEDIUM");
            }
            case "sensitivity high" -> {
                sensitivity = Sensitivity.HIGH;
                updateStatus("Sensitivity set to HIGH");
            }
            case "reset count" -> {
                detectionCount.set(0);
                updateStatus("Detection count reset");
            }
            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (sensorArmed.get() ? " 🔒 " : " 🔓 ") +
                sensitivityIcon();
    }

    private String sensitivityIcon() {
        return switch (sensitivity) {
            case LOW -> " 📶🔈 ";
            case MEDIUM -> " 📶🔉 ";
            case HIGH -> " 📶🔊 ";
        };
    }

    // ──────Alertable interface──────────────────────────
    @Override
    public void triggerAlert(String message) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + message;
        alertHistory.add(entry);
        System.out.println("MOTION SENSOR ALERT: " + entry);
        updateStatus(message);
    }

    @Override
    public List<String> getAlertHistory() {
        return new ArrayList<>(alertHistory);
    }

    // ──────Internal helpers───────────────────────────
    private void onMotionEvent() {
        String timestamp = LocalDateTime.now().format(FMT);
        String msg = "Motion detected in " + getRoom() + " [" + timestamp + "]";

        detectionCount.set(detectionCount.get() + 1);
        detectionHistory.add(msg);

        triggerAlert("🚨 " + msg);

        if (onMotionCallback != null) {
            onMotionCallback.accept(msg);
        }
    }

    /**
     * Register a device or lambda to be notified on motion events.
     */
    public void setOnMotionCallback(Consumer<String> callback) {
        this.onMotionCallback = callback;
    }

    public List<String> getDetectionHistory() {
        return new ArrayList<>(detectionHistory);
    }

    // ─────JavaFX property & binding───────────────────────


    public BooleanProperty sensorArmedProperty() {
        return sensorArmed;
    }

    public IntegerProperty detectionCountProperty() {
        return detectionCount;
    }

    // ───────Setter & Getters───────────────────────────────

    public boolean isSensorArmed() {
        return sensorArmed.get();
    }

    public void setSensorArmed(boolean v) {
        sensorArmed.set(v);
    }

    public int getDetectionCount() {
        return detectionCount.get();
    }

    public Sensitivity getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Sensitivity v) {
        sensitivity = v;
    }


    // ─── Sensitivity enum ─────────────────────────────────
    public enum Sensitivity {LOW, MEDIUM, HIGH}
}