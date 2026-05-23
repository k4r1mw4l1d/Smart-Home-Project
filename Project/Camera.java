/*
 *    ============Camera============
 *    consists of : 1. Live / Off recording state.
 *                  2. Night vision mode.
 *                  3. Motion-triggered recording.
 */

// ───Imports────────────────────────────────────────────

import javafx.beans.property.*;

public class Camera extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty recording = new SimpleBooleanProperty(false);
    private final BooleanProperty nightVision = new SimpleBooleanProperty(false);
    private final BooleanProperty motionDetected = new SimpleBooleanProperty(false);
    private final BooleanProperty motionRecord = new SimpleBooleanProperty(true);
    private final BooleanProperty emergency = new SimpleBooleanProperty(false);

    // ──────Constructor───────────────────────────────────────
    public Camera(String deviceId,
                  String name,
                  String room,
                  boolean nightVision,
                  boolean motionRecord,
                  boolean emergency) {
        super(deviceId, name, room);
        this.nightVision.set(nightVision);
        this.motionRecord.set(motionRecord);
        this.emergency.set(emergency);
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format(
                "Recording=%s NightVision=%s Motion=%s",
                isRecording() ? "ON" : "OFF",
                isNightVision() ? "ON" : "OFF",
                isMotionDetected() ? "YES" : "NO"
        ));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {
        switch (cmd.toLowerCase()) {

            case "record on" -> {
                setRecording(true);
                updateStatus("Recording ON");
            }

            case "record off" -> {
                setRecording(false);
                updateStatus("Recording OFF");
            }
            case "night vision on" -> {
                setNightVision(true);
                updateStatus("Night vision ON");
            }
            case "night vision off" -> {
                setNightVision(false);
                updateStatus("Night vision OFF");
            }
            case "motion record on" -> {
                setMotionRecord(true);
                updateStatus("Auto motion-record ON");
            }
            case "motion record off" -> {
                setMotionRecord(false);
                updateStatus("Auto motion-record OFF");
            }
            case "motion detected" -> {
                setMotionDetected(true);
                updateStatus("Motion detected");
            }
            case "motion clear" -> {
                setMotionDetected(false);
                updateStatus("Motion clear");
            }

            default -> {
                updateStatus("INVALID COMMAND");
            }
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (recording.get() ? " 🔴 " : " ⚫ ") +
                (nightVision.get() ? " 🌙 " : " ☀ ") +
                (motionDetected.get() ? " 🚶 " : " 🏠 ");
    }


    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty recordingProperty() {
        return recording;
    }

    public BooleanProperty nightVisionProperty() {
        return nightVision;
    }

    public BooleanProperty motionDetectedProperty() {
        return motionDetected;
    }

    public BooleanProperty motionRecordProperty() {
        return motionRecord;
    }

    public BooleanProperty emergencyProperty() {
        return emergency;
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isRecording() {
        return recording.get();
    }

    public void setRecording(boolean v) {
        recording.set(v);
    }

    public boolean isNightVision() {
        return nightVision.get();
    }

    public void setNightVision(boolean v) {
        nightVision.set(v);
    }

    public boolean isMotionDetected() {
        return motionDetected.get();
    }

    public void setMotionDetected(boolean v) {
        motionDetected.set(v);
    }

    public boolean isMotionRecord() {
        return motionRecord.get();
    }

    public void setMotionRecord(boolean v) {
        motionRecord.set(v);
    }

    public boolean getEmergency() {
        return emergency.get();
    }

    public void setEmergency(boolean v) {
        this.emergency.set(v);
    }
}