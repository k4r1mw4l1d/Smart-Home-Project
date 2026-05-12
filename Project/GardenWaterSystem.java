/*
 *    ============Garden Water System============
 *    consists of : 1. Manual / Auto watering control.
 *                  2. Soil moisture monitoring.
 *                  3. Scheduled watering sessions.
 *                  4. Water usage tracker.
 *                  5. Rain detection (skip watering if raining).
 */

import javafx.beans.property.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GardenWaterSystem extends SmartDevice {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty wateringOn = new SimpleBooleanProperty(false);
    private final DoubleProperty humidity = new SimpleDoubleProperty(0);
    private final DoubleProperty soilMoisture = new SimpleDoubleProperty(0);   // 0–100 %
    private final BooleanProperty rainDetected = new SimpleBooleanProperty(false);
    private final BooleanProperty autoModeOn = new SimpleBooleanProperty(false);
    private final DoubleProperty waterUsedLiters = new SimpleDoubleProperty(0);
    private final StringProperty scheduleTime = new SimpleStringProperty("06:00");
    private final List<String> wateringLog = new ArrayList<>();
    // Thresholds for auto-mode
    private double dryThreshold = 30.0;   // start watering below this moisture %
    private double wetThreshold = 70.0;   // stop watering above this moisture %

    // ──────Constructor───────────────────────────────────────
    public GardenWaterSystem(String deviceId, String name, String room,
                             boolean wateringOn, double soilMoisture,
                             boolean rainDetected, boolean autoModeOn, double humidity) {
        super(deviceId, name, room);
        this.rainDetected.set(rainDetected);
        this.autoModeOn.set(autoModeOn);
        this.humidity.set(humidity);

        // Listener: auto-mode reacts to soil moisture changes
        this.soilMoisture.addListener((obs, oldV, newV) -> {
            if (isAutoModeOn()) autoControl(newV.doubleValue());
        });
        this.soilMoisture.set(soilMoisture);

        // Apply initial watering state after listeners are set
        if (wateringOn) startWatering();

        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format(
                "Watering=%s Moisture=%.1f%% Rain=%s AutoMode=%s WaterUsed=%.2fL Schedule=%s",
                isWateringOn() ? "ON" : "OFF",
                getSoilMoisture(),
                isRainDetected() ? "YES" : "NO",
                isAutoModeOn() ? "ON" : "OFF",
                getWaterUsedLiters(),
                scheduleTime.get()));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {
        switch (cmd.toLowerCase()) {

            case "start watering" -> startWatering();

            case "stop watering" -> stopWatering();

            case "auto on" -> {
                setAutoModeOn(true);
                updateStatus("Auto mode ON");
            }
            case "auto off" -> {
                setAutoModeOn(false);
                updateStatus("Auto mode OFF");
            }
            case "reset water counter" -> {
                waterUsedLiters.set(0);
                updateStatus("Water usage counter reset");
            }

            default -> {
                // e.g. "schedule 07:30"
                if (cmd.toLowerCase().startsWith("schedule ")) {
                    String time = cmd.substring(9).trim();
                    scheduleTime.set(time);
                    updateStatus("Watering scheduled at " + time);
                } else {
                    updateStatus("INVALID COMMAND");
                }
            }
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (wateringOn.get() ? " 💧 " : " 🌵 ") +
                (rainDetected.get() ? " 🌧 " : " ☀ ") +
                (autoModeOn.get() ? " 🤖 " : " 👤 ");
    }

    // ──────Internal helpers───────────────────────────
    private void startWatering() {
        if (isRainDetected()) {
            updateStatus("Watering skipped — rain detected");
            return;
        }
        setWateringOn(true);
        logSession("Watering STARTED");
        updateStatus("Watering ON");
    }

    private void stopWatering() {
        setWateringOn(false);
        logSession("Watering STOPPED");
        updateStatus("Watering OFF");
    }

    private void autoControl(double moisture) {
        if (moisture < dryThreshold && !isWateringOn()) {
            startWatering();
        } else if (moisture >= wetThreshold && isWateringOn()) {
            stopWatering();
        }
    }

    /**
     * Call periodically (e.g. every second) to accumulate usage while watering.
     */
    public void tickWaterUsage(double litersPerTick) {
        if (isWateringOn()) {
            waterUsedLiters.set(waterUsedLiters.get() + litersPerTick);
        }
    }

    private void logSession(String event) {
        wateringLog.add("[" + LocalTime.now().format(FMT) + "] " + event +
                " | Moisture=" + String.format("%.1f%%", soilMoisture.get()));
    }

    public List<String> getWateringLog() {
        return new ArrayList<>(wateringLog);
    }

    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty wateringOnProperty() {
        return wateringOn;
    }

    public DoubleProperty humidityProperty() {
        return humidity;
    }

    public DoubleProperty soilMoistureProperty() {
        return soilMoisture;
    }

    public BooleanProperty rainDetectedProperty() {
        return rainDetected;
    }

    public BooleanProperty autoModeOnProperty() {
        return autoModeOn;
    }

    public DoubleProperty waterUsedLitersProperty() {
        return waterUsedLiters;
    }

    public StringProperty scheduleTimeProperty() {
        return scheduleTime;
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isWateringOn() {
        return wateringOn.get();
    }

    public void setWateringOn(boolean v) {
        wateringOn.set(v);
    }

    public double getHumidity() {
        return humidity.get();
    }

    public void setHumidity(double v) {
        humidity.set(v);
    }

    public double getSoilMoisture() {
        return soilMoisture.get();
    }

    public void setSoilMoisture(double v) {
        soilMoisture.set(v);
    }

    public boolean isRainDetected() {
        return rainDetected.get();
    }

    public void setRainDetected(boolean v) {
        rainDetected.set(v);
        if (v && isWateringOn()) stopWatering();  // auto-stop on rain
    }

    public boolean isAutoModeOn() {
        return autoModeOn.get();
    }

    public void setAutoModeOn(boolean v) {
        autoModeOn.set(v);
    }

    public double getWaterUsedLiters() {
        return waterUsedLiters.get();
    }

    public double getDryThreshold() {
        return dryThreshold;
    }

    public void setDryThreshold(double v) {
        dryThreshold = v;
    }

    public double getWetThreshold() {
        return wetThreshold;
    }

    public void setWetThreshold(double v) {
        wetThreshold = v;
    }

    public String getScheduleTime() {
        return scheduleTime.get();
    }

    public void setScheduleTime(String v) {
        scheduleTime.set(v);
    }
}