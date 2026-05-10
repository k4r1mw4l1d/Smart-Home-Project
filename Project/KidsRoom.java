/*
 *     ===========Kids Room===========
 *     consists of: 1. Lighting control system.
 *                  2. AC controller.
 *                  3. safety Check.
 *                  4. Bedtime mode.
 *                  5. motion sensor for kids.
 */

import javafx.beans.property.*;

public class KidsRoom extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty lightsOn = new SimpleBooleanProperty(false);
    private final DoubleProperty temperature = new SimpleDoubleProperty(0);
    private final BooleanProperty bedTime = new SimpleBooleanProperty(false);
    private final BooleanProperty awake = new SimpleBooleanProperty(false);
    private final BooleanProperty acOn = new SimpleBooleanProperty(false);
    private final BooleanProperty babySafety = new SimpleBooleanProperty(false);
    private MasterRoom masterRoom;

    // ──────Constructor───────────────────────────────────────
    public KidsRoom(String deviceId, String name, String room,
                    boolean lightsOn, double temperature, boolean acOn, boolean babySafety, boolean bedTime,
                    boolean awake) {
        super(deviceId, name, room);
        this.lightsOn.set(lightsOn);
        this.acOn.set(acOn);
        this.babySafety.set(babySafety);
        this.bedTime.set(bedTime);
        this.awake.set(awake);

        this.temperature.addListener((obs, oldV, newV) -> configAC());
        this.temperature.set(temperature);

        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format("Lights=%s AC=%s BedTime=%s Safety=%s  Baby=%s",
                isLightsOn() ? "ON" : "OFF",
                isAcOn() ? "ON" : "OFF",
                isBedTime() ? "SLEEP TIME" : "NOT SLEEP TIME",
                isSafe() ? "SAFE" : "ALERT",
                isAwake() ? "AWAKE" : "ASLEEP"));
    }

    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty lightsOnProperty() {
        return lightsOn;
    }

    public BooleanProperty acOnProperty() {
        return acOn;
    }

    public BooleanProperty bedTimeProperty() {
        return bedTime;
    }

    public BooleanProperty awakeProperty() {
        return awake;
    }

    public BooleanProperty babySafetyProperty() {
        return babySafety;
    }

    public DoubleProperty temperatureProperty() {
        return temperature;
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {

        switch (cmd.toLowerCase()) {

            case ("lights on") -> {
                setLightsOn(true);
                updateStatus("Lights on");
            }

            case ("lights off") -> {
                setLightsOn(false);
                updateStatus("Lights off");
            }

            case ("bed time") -> {
                bedTimeMode();
                updateStatus("Bed time mode activated");
            }

            case ("motion detected") -> {
                motionDetector();
                updateStatus("Motion detected");
            }

            case ("ac on") -> {
                setAcOn(true);
                updateStatus("Ac on");
            }

            case ("ac off") -> {
                setAcOn(false);
                updateStatus("Ac off");
            }

            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (lightsOn.get() ? " 💡 " : " 🌑 ") +
                (acOn.get() ? " ❄ " : " 🔥 ") +
                (babySafety.get() ? " ✅ " : " ⚠ ") +
                (bedTime.get() ? " 🌙 " : " ☀ ") +
                (awake.get() ? " 👶 " : " 😴 ");
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

    public boolean isSafe() {
        return babySafety.get();
    }

    public void setBabySafety(boolean babySafety) {
        this.babySafety.set(babySafety);
    }

    public boolean isBedTime() {
        return bedTime.get();
    }

    public void setBedTime(boolean bedTime) {
        this.bedTime.set(bedTime);
    }

    public boolean isAwake() {
        return awake.get();
    }

    public void setAwake(boolean awake) {
        this.awake.set(awake);
    }

    public void setMasterRoom(MasterRoom masterRoom) {
        this.masterRoom = masterRoom;
    }

    //───────Bed time mode─────────────────────────────
    public void bedTimeMode() {

        setBedTime(true);
        setAwake(false);
        setLightsOn(false);
        setAcOn(false);
        setBabySafety(true);

        updateStatus("Sleep mode ON");
    }

    // ───────Motion handler for kids───────────────────
    public void motionDetector() {

        if (bedTime.get()) {
            updateStatus("Motion ignored (Sleep mode active)");
            return;
        }

        setAwake(true);
        setLightsOn(true);
        setBabySafety(true);
        setAcOn(false);

        if (masterRoom != null) {
            masterRoom.setLightsOn(true);
            masterRoom.setDoorOpen(true);
        }

        updateStatus("Motion detected -> Child awake");
    }

    // ───────Automatic AC─────────────────────────────
    public void configAC() {

        if (getTemperature() >= 30 && !isAcOn()) {
            setAcOn(true);
            updateStatus("AC activated");

        } else if (getTemperature() <= 26 && isAcOn()) {
            setAcOn(false);
            updateStatus("AC deactivated");
        }
    }
}