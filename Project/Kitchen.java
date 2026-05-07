/*
 *    ============Kitchen============
 *    consists of : 1. lighting control system.
 *                  2. Fire detector system.
 *                  3. Stove control system.
 *                  4. Dishwasher control system.
 *                  5. Fridge control system.
 */

import javafx.beans.property.*;

public class Kitchen extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty lightsOn = new SimpleBooleanProperty(false);
    private final BooleanProperty fireDetected = new SimpleBooleanProperty(false);
    private final BooleanProperty stoveOn = new SimpleBooleanProperty(false);
    private final DoubleProperty stoveTemperature = new SimpleDoubleProperty(0);
    private final DoubleProperty temperature = new SimpleDoubleProperty(0);
    private final BooleanProperty dishWasherOn = new SimpleBooleanProperty(false);
    private final DoubleProperty fridgeTemperature = new SimpleDoubleProperty(0);
    private final BooleanProperty fridgeOn = new SimpleBooleanProperty(false);

    // ──────Constructor───────────────────────────────────────
    public Kitchen(String deviceId, String name, String room, boolean lightsOn, boolean fireDetected, boolean stoveOn, double stoveTemperature, boolean dishWasherOn, double fridgeTemperature, boolean fridgeOn, double temperature) {
        super(deviceId, name, room);
        this.lightsOn.set(lightsOn);
        this.fireDetected.set(fireDetected);
        this.stoveOn.set(stoveOn);
        this.stoveTemperature.set(stoveTemperature);
        this.dishWasherOn.set(dishWasherOn);
        this.fridgeTemperature.set(fridgeTemperature);
        this.fridgeOn.set(fridgeOn);
        this.temperature.set(temperature);
        updateStatus("Device Initialized");
    }

    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty fireDetectedProperty() {
        return fireDetected;
    }

    public BooleanProperty lightsOnProperty() {
        return lightsOn;
    }

    public DoubleProperty temperatureProperty() {
        return temperature;
    }

    public BooleanProperty stoveOnProperty() {
        return stoveOn;
    }

    public DoubleProperty stoveTemperatureProperty() {
        return stoveTemperature;
    }

    public BooleanProperty dishWasherOnProperty() {
        return dishWasherOn;
    }

    public DoubleProperty fridgeTemperatureProperty() {
        return fridgeTemperature;
    }

    public BooleanProperty fridgeOnProperty() {
        return fridgeOn;
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format("Lights=%s FireDetected=%s Stove=%s DishWasher=%s Fridge=%s",
                isLightsOn() ? "ON" : "OFF",
                isFireDetected() ? "YES" : "NO",
                isStoveOn() ? "ON" : "OFF",
                isDishWasherOn() ? "ON" : "OFF",
                isFridgeOn() ? "ON" : "OFF"));
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

            case "stove on" -> {
                setStoveOn(true);
                updateStatus("Stove ON");
            }

            case "stove off" -> {
                setStoveOn(false);
                updateStatus("Stove OFF");
            }

            case "dishwasher on" -> {
                setDishWasherOn(true);
                updateStatus("DishWasher On");
            }

            case "dishwasher off" -> {
                setDishWasherOn(false);
                updateStatus("DishWasher Off");
            }

            case "fridge on" -> {
                setFridgeOn(true);
                updateStatus("Fridge ON");
            }

            case "fridge off" -> {
                setFridgeOn(false);
                updateStatus("Fridge OFF");
            }

            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (lightsOn.get() ? " 💡 " : " 🌑 ") +
                (fireDetected.get() ? " 🔥️ " : " ❄️ ") +
                (stoveOn.get() ? " ✅ " : " ❌ ") +
                (fridgeOn.get() ? " ✅ " : " ❌ ") +
                (dishWasherOn.get() ? " ✅ " : " ❌ ");
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isLightsOn() {
        return lightsOn.get();
    }

    public void setLightsOn(boolean lightsOn) {
        this.lightsOn.set(lightsOn);
    }

    public boolean isFireDetected() {
        return fireDetected.get();
    }

    public void setFireDetected(boolean fireDetected) {
        this.fireDetected.set(fireDetected);
    }

    public boolean isFridgeOn() {
        return fridgeOn.get();
    }

    public void setFridgeOn(boolean fridgeOn) {
        this.fridgeOn.set(fridgeOn);
    }

    public boolean isStoveOn() {
        return stoveOn.get();
    }

    public void setStoveOn(boolean stoveOn) {
        this.stoveOn.set(stoveOn);
    }

    public double getTemperature() {
        return temperature.get();
    }

    public void setTemperature(double temperature) {
        this.temperature.set(temperature);
    }

    public double getStoveTemperature() {
        return stoveTemperature.get();
    }

    public void setStoveTemperature(double stoveTemperature) {
        this.stoveTemperature.set(stoveTemperature);
    }

    public boolean isDishWasherOn() {
        return dishWasherOn.get();
    }

    public void setDishWasherOn(boolean dishWasherOn) {
        this.dishWasherOn.set(dishWasherOn);
    }

    public double getFridgeTemperature() {
        return fridgeTemperature.get();
    }

    public void setFridgeTemperature(double fridgeTemperature) {
        this.fridgeTemperature.set(fridgeTemperature);
    }

    // ──────Fire detector────────────────────────
    public boolean FireDetected() {

        return temperature.get() >= 60.0;
    }

    // ──────Checking fire──────────────────────────
    public void checkFireAlert() {

        if (FireDetected()) {
            setStoveOn(false);
            setDishWasherOn(false);
            setFridgeOn(false);

            updateStatus("FIRE ALERT! All devices turned OFF");
        }
    }
}