/*
 *    ============Camera============
 *    consists of : 1. Live / Off recording state.
 *                  2. Night vision mode.
 *                  3. Motion-triggered recording.
 *                  4. Pan / Tilt angle control.
 *                  5. Storage usage tracking.
 */

// ───Imports────────────────────────────────────────────

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Camera extends SmartDevice implements Alertable {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty recording = new SimpleBooleanProperty(false);
    private final BooleanProperty nightVision = new SimpleBooleanProperty(false);
    private final BooleanProperty motionDetected = new SimpleBooleanProperty(false);
    private final BooleanProperty motionRecord = new SimpleBooleanProperty(true);  // auto-record on motion
    private final DoubleProperty storageUsedGB = new SimpleDoubleProperty(0);
    private final DoubleProperty storageMaxGB = new SimpleDoubleProperty(128);
    private final BooleanProperty emergency = new SimpleBooleanProperty(false);
    private final List<String> alertHistory = new ArrayList<>();

    // ──────Constructor───────────────────────────────────────
    public Camera(String deviceId, String name, String room,
                  boolean recording, boolean nightVision,
                  boolean motionRecord,
                  double storageMaxGB, boolean emergency) {
        super(deviceId, name, room);
        this.nightVision.set(nightVision);
        this.motionRecord.set(motionRecord);
        this.storageMaxGB.set(storageMaxGB);
        this.emergency.set(emergency);
        this.motionDetected.addListener((obs, oldV, newV) -> {
            if (newV) onMotionDetected();
        });

        if (recording) startRecording();
        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format(
                "Recording=%s NightVision=%s Motion=%s Storage=%.1f/%.1fGB",
                isRecording() ? "ON" : "OFF",
                isNightVision() ? "ON" : "OFF",
                isMotionDetected() ? "YES" : "NO",
                getStorageUsedGB(), getStorageMaxGB()
        ));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {
        switch (cmd.toLowerCase()) {

            case "start recording" -> startRecording();
            case "stop recording" -> stopRecording();

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

            case "reset view" -> {
                updateStatus("Camera view reset to center");
            }
            case "format storage" -> {
                storageUsedGB.set(0);
                updateStatus("Storage formatted");
            }

            default -> {
                if (cmd.toLowerCase().startsWith("set resolution ")) {
                    updateStatus("Resolution set to 1080p ");
                } else {
                    updateStatus("INVALID COMMAND");
                }
            }
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (recording.get() ? " 🔴 " : " ⚫ ") +
                (nightVision.get() ? " 🌙 " : " ☀ ") +
                (motionDetected.get() ? " 🚶 " : " 🏠 ") +
                (isStorageFull() ? " 💾⚠ " : " 💾✅ ");
    }

    // ──────Alertable interface──────────────────────────
    @Override
    public void triggerAlert(String message) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + message;
        alertHistory.add(entry);
        System.out.println("CAMERA ALERT: " + entry);
        updateStatus(message);
    }

    @Override
    public List<String> getAlertHistory() {
        return new ArrayList<>(alertHistory);
    }

    // ──────Internal helpers───────────────────────────
    private void startRecording() {
        if (isStorageFull()) {
            triggerAlert("⚠ Cannot record — storage is full!");
            return;
        }
        setRecording(true);
    }

    private void stopRecording() {
        setRecording(false);
        updateStatus("Recording OFF");
    }

    private void onMotionDetected() {
        triggerAlert("🚶 Motion detected by camera " + getName());
        if (isMotionRecord() && !isRecording()) {
            startRecording();
        }
    }

    public boolean isStorageFull() {
        return storageUsedGB.get() >= storageMaxGB.get();
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

    public DoubleProperty storageUsedGBProperty() {
        return storageUsedGB;
    }

    public DoubleProperty storageMaxGBProperty() {
        return storageMaxGB;
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

    public double getStorageUsedGB() {
        return storageUsedGB.get();
    }

    public double getStorageMaxGB() {
        return storageMaxGB.get();
    }

    public boolean getEmergency() {
        return emergency.get();
    }

    public void setEmergency(boolean v) {
        this.emergency.set(v);
    }
}