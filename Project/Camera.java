/*
 *    ============Security Camera============
 *    consists of : 1. Live / Off recording state.
 *                  2. Night vision mode.
 *                  3. Motion-triggered recording.
 *                  4. Pan / Tilt angle control.
 *                  5. Storage usage tracking.
 */

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
    private final IntegerProperty panAngle = new SimpleIntegerProperty(0);     // –180 to +180 degrees
    private final IntegerProperty tiltAngle = new SimpleIntegerProperty(0);     // –90  to +90  degrees
    private final DoubleProperty storageUsedGB = new SimpleDoubleProperty(0);
    private final DoubleProperty storageMaxGB = new SimpleDoubleProperty(128);
    private final StringProperty resolution = new SimpleStringProperty("1080p");
    private final BooleanProperty emergency = new SimpleBooleanProperty(false);
    private final List<String> alertHistory = new ArrayList<>();
    private final List<String> recordingLog = new ArrayList<>();

    // ──────Constructor───────────────────────────────────────
    public Camera(String deviceId, String name, String room,
                  boolean recording, boolean nightVision,
                  boolean motionRecord, String resolution,
                  double storageMaxGB, boolean emergency) {
        super(deviceId, name, room);
        this.nightVision.set(nightVision);
        this.motionRecord.set(motionRecord);
        this.resolution.set(resolution);
        this.storageMaxGB.set(storageMaxGB);
        this.emergency.set(emergency);

        // Listener: auto-record when motion is detected (if enabled)
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
                "Recording=%s NightVision=%s Motion=%s Pan=%d° Tilt=%d° Storage=%.1f/%.1fGB Res=%s",
                isRecording() ? "ON" : "OFF",
                isNightVision() ? "ON" : "OFF",
                isMotionDetected() ? "YES" : "NO",
                getPanAngle(), getTiltAngle(),
                getStorageUsedGB(), getStorageMaxGB(),
                resolution.get()));
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
            case "pan left" -> pan(-15);
            case "pan right" -> pan(+15);
            case "tilt up" -> tilt(+10);
            case "tilt down" -> tilt(-10);
            case "reset view" -> {
                panAngle.set(0);
                tiltAngle.set(0);
                updateStatus("Camera view reset to center");
            }
            case "format storage" -> {
                storageUsedGB.set(0);
                recordingLog.clear();
                updateStatus("Storage formatted");
            }

            default -> {
                // e.g. "set resolution 4k"
                if (cmd.toLowerCase().startsWith("set resolution ")) {
                    resolution.set(cmd.substring(15).trim().toUpperCase());
                    updateStatus("Resolution set to " + resolution.get());
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
        logRecording("Recording STARTED");
        updateStatus("Recording ON (" + resolution.get() + ")");
    }

    private void stopRecording() {
        setRecording(false);
        logRecording("Recording STOPPED");
        updateStatus("Recording OFF");
    }

    private void onMotionDetected() {
        triggerAlert("🚶 Motion detected by camera " + getName());
        if (isMotionRecord() && !isRecording()) {
            startRecording();
        }
    }

    private void pan(int delta) {
        int next = Math.max(-180, Math.min(180, panAngle.get() + delta));
        panAngle.set(next);
        updateStatus("Pan angle: " + next + "°");
    }

    private void tilt(int delta) {
        int next = Math.max(-90, Math.min(90, tiltAngle.get() + delta));
        tiltAngle.set(next);
        updateStatus("Tilt angle: " + next + "°");
    }

    /**
     * Simulate storage usage growing while recording.
     */
    public void tickStorageUsage(double gbPerTick) {
        if (isRecording()) {
            double used = Math.min(storageUsedGB.get() + gbPerTick, storageMaxGB.get());
            storageUsedGB.set(used);
            if (isStorageFull()) {
                stopRecording();
                triggerAlert("⚠ Storage full — recording stopped!");
            }
        }
    }

    public boolean isStorageFull() {
        return storageUsedGB.get() >= storageMaxGB.get();
    }

    private void logRecording(String event) {
        recordingLog.add("[" + LocalDateTime.now().format(FMT) + "] " + event);
    }

    public List<String> getRecordingLog() {
        return new ArrayList<>(recordingLog);
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

    public IntegerProperty panAngleProperty() {
        return panAngle;
    }

    public IntegerProperty tiltAngleProperty() {
        return tiltAngle;
    }

    public DoubleProperty storageUsedGBProperty() {
        return storageUsedGB;
    }

    public DoubleProperty storageMaxGBProperty() {
        return storageMaxGB;
    }

    public StringProperty resolutionProperty() {
        return resolution;
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

    public int getPanAngle() {
        return panAngle.get();
    }

    public int getTiltAngle() {
        return tiltAngle.get();
    }

    public double getStorageUsedGB() {
        return storageUsedGB.get();
    }

    public double getStorageMaxGB() {
        return storageMaxGB.get();
    }

    public String getResolution() {
        return resolution.get();
    }

    public void setResolution(String v) {
        resolution.set(v);
    }

    public boolean getEmergency() {
        return emergency.get();
    }

    public void setEmergency(boolean v) {
        this.emergency.set(v);
    }
}