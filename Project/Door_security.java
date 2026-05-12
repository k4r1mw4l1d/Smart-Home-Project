/*
 *    ============Door Security============
 *    consists of : 1. Open / Closed state detection.
 *                  2. Lock / Unlock control.
 *                  3. Intrusion alert system.
 *                  4. Entry log history.
 *                  5. Auto-lock timer flag.
 */

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Door_security extends SmartDevice implements Alertable {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty doorOpen = new SimpleBooleanProperty(false);
    private final BooleanProperty motionDetected = new SimpleBooleanProperty(false);
    private final BooleanProperty alarmTriggered = new SimpleBooleanProperty(false);
    private final StringProperty lastEvent = new SimpleStringProperty("None");
    private final List<String> alertHistory = new ArrayList<>();
    private final List<String> entryLog = new ArrayList<>();

    // ──────Constructor───────────────────────────────────────
    public Door_security(String deviceId, String name, String room,
                         boolean doorOpen, boolean motionDetected) {
        super(deviceId, name, room);
        this.doorOpen.set(doorOpen);
        this.motionDetected.set(motionDetected);


        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format("Door=%s Locked=%s Alarm=%s LastEvent=%s",
                isDoorOpen() ? "OPEN" : "CLOSED",
                isAlarmTriggered() ? "TRIGGERED" : "CLEAR",
                lastEvent.get()));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {
        switch (cmd.toLowerCase()) {

            case "open door" -> {
                setDoorOpen(true);
                updateStatus("Door OPENED");
            }
            case "close door" -> {
                setDoorOpen(false);
                updateStatus("Door CLOSED");
            }

            case "reset alarm" -> {
                setAlarmTriggered(false);
                updateStatus("Alarm RESET");
            }
            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (doorOpen.get() ? " 🚪 " : " 🔐 ") +
                (alarmTriggered.get() ? " 🚨 " : " ✅ ");
    }

    // ──────Alertable interface──────────────────────────
    @Override
    public void triggerAlert(String message) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + message;
        alertHistory.add(entry);
        setAlarmTriggered(true);
        lastEvent.set(message);
        System.out.println("DOOR SECURITY ALERT: " + entry);
    }

    @Override
    public List<String> getAlertHistory() {
        return new ArrayList<>(alertHistory);
    }

    // ──────Entry log──────────────────────────────────
    private void logEntry(String event) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + event;
        entryLog.add(entry);
        lastEvent.set(event);
    }

    public List<String> getEntryLog() {
        return new ArrayList<>(entryLog);
    }

    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty doorOpenProperty() {
        return doorOpen;
    }

    public BooleanProperty alarmTriggeredProperty() {
        return alarmTriggered;
    }

    public StringProperty lastEventProperty() {
        return lastEvent;
    }

    public BooleanProperty motionDetectedProperty() {
        return motionDetected;
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isDoorOpen() {
        return doorOpen.get();
    }

    public void setDoorOpen(boolean v) {
        doorOpen.set(v);
    }

    public boolean isAlarmTriggered() {
        return alarmTriggered.get();
    }

    public void setAlarmTriggered(boolean v) {
        alarmTriggered.set(v);
    }

    public boolean isMotionDetected() {
        return motionDetected.get();
    }


    public void setMotionDetected(boolean v) {
        motionDetected.set(v);
    }
}