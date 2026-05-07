/*
 *    ============Living Room============
 *    consists of : 1. lighting control system.
 *                  2. AC controller.
 *                  3. smart modes.
 *                  4. curtains.
 *                  5. TV controller.
 */

import javafx.beans.property.*;

public class LivingRoom extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty lightsOn = new SimpleBooleanProperty(false);
    private final DoubleProperty temperature = new SimpleDoubleProperty(0);
    private final BooleanProperty acOn = new SimpleBooleanProperty(false);
    private final BooleanProperty curtainsOn = new SimpleBooleanProperty(false);
    private final BooleanProperty tvOn = new SimpleBooleanProperty(false);
    private final StringProperty smartScene = new SimpleStringProperty("");

    // ──────Constructor───────────────────────────────────────
    public LivingRoom(String deviceId, String name, String room, boolean lightsOn, double temperature, boolean acOn, boolean curtainsOn, boolean tvOn) {
        super(deviceId, name, room);
        this.lightsOn.set(lightsOn);
        this.temperature.addListener((obs, oldV, newV) -> configAC());
        this.temperature.set(temperature);
        updateStatus("Device Initialized");
        this.acOn.set(acOn);
        this.curtainsOn.set(curtainsOn);
        this.tvOn.set(tvOn);

    }

    // ─────JavaFX property & binding───────────────────────
    public boolean isLightsOn() {
        return lightsOn.get();
    }

    public void setLightsOn(boolean lightsOn) {
        this.lightsOn.set(lightsOn);
    }

    public double getTemperature() {
        return temperature.get();
    }

    public void setTemperature(double temperature) {
        this.temperature.set(temperature);
    }

    // ───────Setter & Getters───────────────────────────────

    public BooleanProperty curtainsOnProperty() {
        return curtainsOn;
    }

    public BooleanProperty lightsOnProperty() {
        return lightsOn;
    }

    public DoubleProperty temperatureProperty() {
        return temperature;
    }

    public StringProperty smartSceneProperty() {
        return smartScene;
    }

    public BooleanProperty acOnProperty() {
        return acOn;
    }

    public BooleanProperty tvOnProperty() {
        return tvOn;
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format("Lights=%s AC=%s Curtains=%s TV=%s",
                isLightsOn() ? "ON" : "OFF",
                isAcOn() ? "ON" : "OFF",
                isCurtainsOn() ? "OPENED" : "CLOSED",
                isTvOn() ? "ON" : "OFF"));
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {

        switch (cmd.toLowerCase()) {

            case "lights on" -> {
                setLightsOn(true);
                updateStatus("Lights ON");
            }

            case "lights off" -> {
                setLightsOn(false);
                updateStatus("Lights OFF");
            }

            case "ac on" -> {
                setAcOn(true);
                updateStatus("AC ON");
            }

            case "ac off" -> {
                setAcOn(false);
                updateStatus("AC OFF");
            }

            case "open curtains" -> {
                setCurtainsOn(true);
                updateStatus("Curtains Opened");
            }

            case "close curtains" -> {
                setCurtainsOn(false);
                updateStatus("Curtains Closed");
            }

            case "tv on" -> {
                setTvOn(true);
                updateStatus("TV ON");
            }

            case "tv off" -> {
                setTvOn(false);
                updateStatus("TV OFF");
            }

            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (lightsOn.get() ? " 💡 " : " 🌑 ") +
                (acOn.get() ? " ❄️ " : " 🔥 ") +
                (curtainsOn.get() ? " 🪟 " : " ❌ ") +
                (tvOn.get() ? " 📺 " : " 📴 ");
    }

    public boolean isAcOn() {
        return acOn.get();
    }

    public void setAcOn(boolean acOn) {
        this.acOn.set(acOn);
    }

    public boolean isTvOn() {
        return tvOn.get();
    }

    public void setTvOn(boolean tvOn) {
        this.tvOn.set(tvOn);
    }

    public String getSmartScene() {
        return smartScene.get();
    }

    public void setSmartScene(String smartScene) {
        this.smartScene.set(smartScene);
        applyScene();
    }

    public boolean isCurtainsOn() {
        return curtainsOn.get();
    }

    public void setCurtainsOn(boolean curtainsOn) {
        this.curtainsOn.set(curtainsOn);
    }

    // ───────Automatic AC─────────────────────────────
    public void configAC() {

        if (getTemperature() >= 30 && !isAcOn()) {
            setAcOn(true);
            updateStatus("AC activated");

        } else if (getTemperature() <= 22 && isAcOn()) {
            setAcOn(false);
            updateStatus("AC deactivated");
        }
    }

    // ──────Applying room modes────────────────────────
    private void applyScene() {

        if (getSmartScene() == null) return;

        switch (getSmartScene().toLowerCase()) {

            case "movie mode" -> {
                setLightsOn(false);
                setTvOn(true);
                updateStatus("Movie mode Activated");
            }

            case "night mode" -> {
                setLightsOn(false);
                setTvOn(false);
                updateStatus("Night Mode Activated");
            }

            default -> updateStatus("INVALID MODE");
        }

    }
}