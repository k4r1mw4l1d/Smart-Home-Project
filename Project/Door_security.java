/*
 *    ============Door Security============
 *    consists of : 1. Open / Closed state detection.
 *                  2. Intrusion alert system.
 *                  3. motion detection system
 */


import javafx.beans.property.*;

public class Door_security extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty doorOpen = new SimpleBooleanProperty(false);
    private final BooleanProperty motionDetected = new SimpleBooleanProperty(false);
    private final BooleanProperty alarmTriggered = new SimpleBooleanProperty(false);

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
        updateStatus(String.format("Door=%s Alarm=%s",
                isDoorOpen() ? "OPEN" : "CLOSED",
                isAlarmTriggered() ? "TRIGGERED" : "CLEAR"));
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
            case "motion detected" -> {
                setMotionDetected(true);
                updateStatus("Motion detected near door");

                if (!isDoorOpen()) {
                    setAlarmTriggered(true);
                }
            }

            case "motion clear" -> {
                setMotionDetected(false);
                updateStatus("Motion cleared");
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
                (motionDetected.get() ? " 🚶 " : " 🏠 ") +
                (alarmTriggered.get() ? " 🚨 " : " ✅ ");
    }


    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty doorOpenProperty() {
        return doorOpen;
    }

    public BooleanProperty alarmTriggeredProperty() {
        return alarmTriggered;
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