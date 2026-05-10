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
    private final BooleanProperty doorLocked = new SimpleBooleanProperty(false);
    private final BooleanProperty alarmTriggered = new SimpleBooleanProperty(false);
    private final StringProperty lastEvent = new SimpleStringProperty("None");
    private final List<String> alertHistory = new ArrayList<>();
    private final List<String> entryLog = new ArrayList<>();

    // ──────Constructor───────────────────────────────────────
    public Door_security(String deviceId, String name, String room,
                         boolean doorOpen, boolean doorLocked) {
        super(deviceId, name, room);
        this.doorOpen.set(doorOpen);
        this.doorLocked.set(doorLocked);

        // Listener: if door opens while locked → intrusion alert
        this.doorOpen.addListener((obs, oldV, newV) -> {
            if (newV && isDoorLocked()) {
                triggerAlert("⚠ INTRUSION ALERT: Door forced open while locked!");
            }
            logEntry(newV ? "Door OPENED" : "Door CLOSED");
        });

        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format("Door=%s Locked=%s Alarm=%s LastEvent=%s",
                isDoorOpen() ? "OPEN" : "CLOSED",
                isDoorLocked() ? "LOCKED" : "UNLOCKED",
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
            case "lock door" -> {
                if (isDoorOpen()) {
                    updateStatus("Cannot lock: door is open!");
                } else {
                    setDoorLocked(true);
                    updateStatus("Door LOCKED");
                }
            }
            case "unlock door" -> {
                setDoorLocked(false);
                setAlarmTriggered(false);
                updateStatus("Door UNLOCKED");
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
                (doorLocked.get() ? " 🔒 " : " 🔓 ") +
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

    public BooleanProperty doorLockedProperty() {
        return doorLocked;
    }

    public BooleanProperty alarmTriggeredProperty() {
        return alarmTriggered;
    }

    public StringProperty lastEventProperty() {
        return lastEvent;
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isDoorOpen() {
        return doorOpen.get();
    }

    public void setDoorOpen(boolean v) {
        doorOpen.set(v);
    }

    public boolean isDoorLocked() {
        return doorLocked.get();
    }

    public void setDoorLocked(boolean v) {
        doorLocked.set(v);
    }

    public boolean isAlarmTriggered() {
        return alarmTriggered.get();
    }

    public void setAlarmTriggered(boolean v) {
        alarmTriggered.set(v);
    }
}