/*
 *    ============External Temperature Sensor============
 *    consists of : 1. Real-time outdoor temperature reading.
 *                  2. Humidity tracking.
 *                  3. Heat / Frost alert thresholds.
 *                  4. Weather condition description.
 *                  5. Min / Max daily log.
 */

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExternalTempSensor extends SmartDevice implements Alertable {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // ───Attributes────────────────────────────────────────────
    private final DoubleProperty temperature = new SimpleDoubleProperty(0);
    private final StringProperty weatherDesc = new SimpleStringProperty("Unknown");
    private final DoubleProperty dailyMinTemp = new SimpleDoubleProperty(Double.MAX_VALUE);
    private final DoubleProperty dailyMaxTemp = new SimpleDoubleProperty(Double.MIN_VALUE);
    private final BooleanProperty alertActive = new SimpleBooleanProperty(false);
    private final List<String> alertHistory = new ArrayList<>();
    private double heatThreshold = 40.0;   // °C — configurable
    private double frostThreshold = 0.0;    // °C — configurable

    // ──────Constructor───────────────────────────────────────
    public ExternalTempSensor(String deviceId, String name, String room,
                              double temperature, double humidity) {
        super(deviceId, name, room);

        // Listener fires threshold checks on every temperature change
        this.temperature.addListener((obs, oldV, newV) -> {
            updateDailyMinMax(newV.doubleValue());
            checkThresholds(newV.doubleValue());
        });
        this.temperature.set(temperature);

        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format(
                "Temp=%.1f°C Condition=%s Min=%.1f°C Max=%.1f°C",
                getTemperature(), weatherDesc.get(),
                getDailyMinTemp(), getDailyMaxTemp()));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {
        // Sensors are read-only devices; only configuration commands accepted
        if (cmd.toLowerCase().startsWith("set heat threshold ")) {
            try {
                double val = Double.parseDouble(cmd.substring(19).trim());
                heatThreshold = val;
                updateStatus("Heat threshold set to " + val + "°C");
            } catch (NumberFormatException e) {
                updateStatus("INVALID threshold value");
            }
        } else if (cmd.toLowerCase().startsWith("set frost threshold ")) {
            try {
                double val = Double.parseDouble(cmd.substring(20).trim());
                frostThreshold = val;
                updateStatus("Frost threshold set to " + val + "°C");
            } catch (NumberFormatException e) {
                updateStatus("INVALID threshold value");
            }
        } else if (cmd.toLowerCase().equals("reset daily log")) {
            dailyMinTemp.set(Double.MAX_VALUE);
            dailyMaxTemp.set(Double.MIN_VALUE);
            updateStatus("Daily min/max log reset");
        } else {
            updateStatus("INVALID COMMAND (sensor is read-only)");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        double t = getTemperature();
        String tempIcon = t >= heatThreshold ? " 🌡🔴 " :
                t <= frostThreshold ? " 🌡🔵 " : " 🌡🟢 ";
        return tempIcon + " 💧 " + (alertActive.get() ? " ⚠ " : " ✅ ");
    }

    // ──────Alertable interface──────────────────────────
    @Override
    public void triggerAlert(String message) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + message;
        alertHistory.add(entry);
        alertActive.set(true);
        System.out.println("TEMP SENSOR ALERT: " + entry);
        updateStatus(message);
    }

    @Override
    public List<String> getAlertHistory() {
        return new ArrayList<>(alertHistory);
    }

    // ──────Internal helpers───────────────────────────
    private void updateDailyMinMax(double temp) {
        if (temp < dailyMinTemp.get()) dailyMinTemp.set(temp);
        if (temp > dailyMaxTemp.get()) dailyMaxTemp.set(temp);
    }

    private void checkThresholds(double temp) {
        if (temp >= heatThreshold) {
            triggerAlert("⚠ HEAT ALERT: External temperature is " + temp + "°C");
        } else if (temp <= frostThreshold) {
            triggerAlert("⚠ FROST ALERT: External temperature is " + temp + "°C");
        } else {
            alertActive.set(false);
        }
    }

    private void updateWeatherDesc(double temp, double hum) {
        if (temp >= 35) weatherDesc.set("Scorching Hot");
        else if (temp >= 28) weatherDesc.set("Hot");
        else if (temp >= 20) weatherDesc.set(hum > 70 ? "Warm & Humid" : "Warm");
        else if (temp >= 10) weatherDesc.set("Cool");
        else if (temp >= 0) weatherDesc.set("Cold");
        else weatherDesc.set("Freezing");
    }

    // ─────JavaFX property & binding───────────────────────
    public DoubleProperty temperatureProperty() {
        return temperature;
    }

    public StringProperty weatherDescProperty() {
        return weatherDesc;
    }

    public DoubleProperty dailyMinTempProperty() {
        return dailyMinTemp;
    }

    public DoubleProperty dailyMaxTempProperty() {
        return dailyMaxTemp;
    }

    public BooleanProperty alertActiveProperty() {
        return alertActive;
    }

    // ───────Setter & Getters───────────────────────────────
    public double getTemperature() {
        return temperature.get();
    }

    public void setTemperature(double v) {
        temperature.set(v);
    }

    public double getDailyMinTemp() {
        return dailyMinTemp.get();
    }

    public double getDailyMaxTemp() {
        return dailyMaxTemp.get();
    }

    public double getHeatThreshold() {
        return heatThreshold;
    }

    public void setHeatThreshold(double v) {
        heatThreshold = v;
    }

    public double getFrostThreshold() {
        return frostThreshold;
    }

    public void setFrostThreshold(double v) {
        frostThreshold = v;
    }

    public String getWeatherDesc() {
        return weatherDesc.get();
    }
}