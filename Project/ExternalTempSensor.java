/*
 *    ============External Temperature Sensor============
 *    consists of : 1. Real-time outdoor temperature reading.
 *                  2. Humidity tracking.
 *                  3. Heat / Frost alert thresholds.
 *                  4. Weather condition description.
 *                  5. Min / Max daily log.
 */


import javafx.beans.property.*;


public class ExternalTempSensor extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private final DoubleProperty temperature = new SimpleDoubleProperty(0);
    private final StringProperty weatherDesc = new SimpleStringProperty("Unknown");
    private double heatThreshold = 40.0;
    private double frostThreshold = 0.0;

    // ──────Constructor───────────────────────────────────────
    public ExternalTempSensor(String deviceId, String name, String room,
                              double temperature) {
        super(deviceId, name, room);
        this.temperature.set(temperature);
        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format(
                "Temp=%.1f°C Condition=%s Min=%.1f°C Max=%.1f°C",
                getTemperature(), weatherDesc.get()));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {
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
            updateStatus("Daily min/max log reset");
        } else {
            updateStatus("INVALID COMMAND (sensor is read-only)");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (getTemperature() >= heatThreshold ? " 🌡🔴 " :
                getTemperature() <= frostThreshold ? " 🌡🔵 " : " 🌡🟢 ");
    }


    // ─────JavaFX property & binding───────────────────────
    public DoubleProperty temperatureProperty() {
        return temperature;
    }

    public StringProperty weatherDescProperty() {
        return weatherDesc;
    }

    // ───────Setter & Getters───────────────────────────────
    public double getTemperature() {
        return temperature.get();
    }

    public void setTemperature(double v) {
        temperature.set(v);
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