/*
 *    ============Garden Water System============
 *    consists of : 1. Manual / Auto watering control.
 *                  2. Soil moisture monitoring.
 *                  3. Water usage tracker.
 *                  4. Rain detection (skip watering if raining).
 */

import javafx.beans.property.*;


public class GardenWaterSystem extends SmartDevice {
    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty wateringOn = new SimpleBooleanProperty(false);
    private final DoubleProperty humidity = new SimpleDoubleProperty(0);
    private final BooleanProperty rainDetected = new SimpleBooleanProperty(false);
    private final DoubleProperty waterUsedLiters = new SimpleDoubleProperty(0);
    private double dryThreshold = 30.0;
    private double wetThreshold = 70.0;

    // ──────Constructor───────────────────────────────────────
    public GardenWaterSystem(String deviceId, String name, String room,
                             boolean wateringOn,
                             boolean rainDetected, double humidity) {
        super(deviceId, name, room);
        this.rainDetected.set(rainDetected);
        this.humidity.set(humidity);

        if (wateringOn) startWatering();

        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format(
                "Watering=%s Moisture=%.1f%% Rain=%s WaterUsed=%.2fL",
                isWateringOn() ? "ON" : "OFF",
                isRainDetected() ? "YES" : "NO",
                getWaterUsedLiters()));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {
        switch (cmd.toLowerCase()) {

            case "start watering" -> startWatering();

            case "stop watering" -> stopWatering();

            case "reset water counter" -> {
                waterUsedLiters.set(0);
                updateStatus("Water usage counter reset");
            }

            default -> {
                if (cmd.toLowerCase().startsWith("schedule ")) {
                    String time = cmd.substring(9).trim();
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
                (rainDetected.get() ? " 🌧 " : " ☀ ");
    }

    // ──────Internal helpers───────────────────────────
    private void startWatering() {
        if (isRainDetected()) {
            updateStatus("Watering skipped — rain detected");
            return;
        }
        setWateringOn(true);
        updateStatus("Watering ON");
    }

    private void stopWatering() {
        setWateringOn(false);
        updateStatus("Watering OFF");
    }

    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty wateringOnProperty() {
        return wateringOn;
    }

    public DoubleProperty humidityProperty() {
        return humidity;
    }

    public BooleanProperty rainDetectedProperty() {
        return rainDetected;
    }

    public DoubleProperty waterUsedLitersProperty() {
        return waterUsedLiters;
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

    public boolean isRainDetected() {
        return rainDetected.get();
    }

    public void setRainDetected(boolean v) {
        rainDetected.set(v);
        if (v && isWateringOn()) stopWatering();
    }

    public double getWaterUsedLiters() {
        return waterUsedLiters.get();
    }

    public void setWaterUsedLiters(double v) {
        waterUsedLiters.set(v);
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

}