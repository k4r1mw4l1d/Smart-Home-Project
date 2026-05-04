/*
 *    ============Master Room============
 *    consists of : 1. lighting control system.
 *                  2. AC controller.
 *                  3. smart modes.
 *                  4. door safety.
 *                  5. TV controller.
 */

import javafx.beans.property.*;

public class MasterRoom extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty doorLocked = new SimpleBooleanProperty(false);
    private final BooleanProperty lightsOn = new SimpleBooleanProperty(false);
    private final DoubleProperty temperature = new SimpleDoubleProperty(0);
    private final StringProperty smartScene = new SimpleStringProperty("");
    private final BooleanProperty acOn = new SimpleBooleanProperty(false);
    private final BooleanProperty tvOn = new SimpleBooleanProperty(false);


    // ──────Constructor───────────────────────────────────────
    public MasterRoom(String deviceId, String name, String room, boolean lightsOn, double temperature,
                      boolean acOn, String smartScene, boolean doorLocked, boolean tvOn) {
        super(deviceId, name, room);

        this.lightsOn.set(lightsOn);
        this.acOn.set(acOn);
        this.smartScene.set(smartScene);
        this.doorLocked.set(doorLocked);
        this.tvOn.set(tvOn);

        this.temperature.addListener((obs, oldV, newV) -> configAC());
        this.temperature.set(temperature);

        updateStatus("Device Initialized");
    }

    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty doorLockedProperty() {
        return doorLocked;
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
        updateStatus(String.format("Lights=%s AC=%s Door=%s TV=%s",
                isLightsOn() ? "ON" : "OFF",
                isAcOn() ? "ON" : "OFF",
                isDoorLocked() ? "LOCKED" : "UNLOCKED",
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

            case "lock door" -> {
                setDoorLocked(true);
                updateStatus("Door Locked");
            }

            case "unlock door" -> {
                setDoorLocked(false);
                updateStatus("Door Unlocked");
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
                (acOn.get() ? " ❄ " : " 🔥 ") +
                (doorLocked.get() ? " 🔒 " : " 🔓 ") +
                (tvOn.get() ? " 📺 " : " 📴 ");
    }

    // ───────Setter & Getters───────────────────────────────
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

    public boolean isAcOn() {
        return acOn.get();
    }

    public void setAcOn(boolean acOn) {
        this.acOn.set(acOn);
    }

    public String getSmartScene() {
        return smartScene.get();
    }

    public void setSmartScene(String smartScene) {
        this.smartScene.set(smartScene);
        applyScene();
    }

    public boolean isDoorLocked() {
        return doorLocked.get();
    }

    public void setDoorLocked(boolean doorLocked) {
        this.doorLocked.set(doorLocked);
    }

    public boolean isTvOn() {
        return tvOn.get();
    }

    public void setTvOn(boolean tvOn) {
        this.tvOn.set(tvOn);
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

            case "sleep mode" -> {
                setLightsOn(false);
                setDoorLocked(true);
                setTvOn(false);
                updateStatus("Sleep Mode Activated");
            }

            case "romance mode" -> {
                setLightsOn(true);
                setDoorLocked(true);
                updateStatus("Romance Mode Activated");
            }

            case "relax mode" -> {
                setLightsOn(true);
                setTvOn(true);
                setDoorLocked(true);
                updateStatus("Relax Mode Activated");
            }

            default -> updateStatus("INVALID MODE");
        }
    }
}