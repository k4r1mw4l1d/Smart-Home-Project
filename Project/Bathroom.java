/*
 *     ===========Bathroom==========
 *     consists of: 1. Lighting control system.
 *                  2. Heater and water temperature.
 *                  3. Checking is occupied.
 *                  4. Different modes.
 */

import javafx.beans.property.*;

public class Bathroom extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private final BooleanProperty lightsOn = new SimpleBooleanProperty(false);
    private final BooleanProperty occupied = new SimpleBooleanProperty(false);
    private final BooleanProperty heaterOn = new SimpleBooleanProperty(false);
    private final BooleanProperty doorOpen = new SimpleBooleanProperty(false);
    private final DoubleProperty waterTemperature = new SimpleDoubleProperty(0);

    // ──────Constructor───────────────────────────────────────
    public Bathroom(String deviceId, String name, String room,
                    boolean lightsOn, boolean occupied, boolean heaterOn, double waterTemperature, boolean doorOpen) {
        super(deviceId, name, room);
        this.lightsOn.set(lightsOn);
        this.occupied.set(occupied);
        this.heaterOn.set(heaterOn);
        this.doorOpen.set(doorOpen);

        this.waterTemperature.addListener((obs, oldV, newV) -> configHeater());
        this.waterTemperature.set(waterTemperature);

        updateStatus("Device Initialized");
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(String.format(
                "Lights=%s Occupied=%s Door=%s Heater=%s WaterTemp=%.2f",
                isLightsOn() ? "ON" : "OFF",
                isOccupied() ? "YES" : "NO",
                isDoorOpen() ? "LOCKED" : "UNLOCKED",
                isHeaterOn() ? "ON" : "OFF",
                getWaterTemperature()));
    }

    // ─────JavaFX property & binding───────────────────────
    public BooleanProperty lightsOnProperty() {
        return lightsOn;
    }

    public BooleanProperty occupiedProperty() {
        return occupied;
    }

    public BooleanProperty heaterOnProperty() {
        return heaterOn;
    }

    public DoubleProperty waterTemperatureProperty() {
        return waterTemperature;
    }

    public BooleanProperty doorOpenProperty() {
        return doorOpen;
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
            case "heater on" -> {
                setHeaterOn(true);
                updateStatus("Heater ON");
            }
            case "heater off" -> {
                setHeaterOn(false);
                updateStatus("Heater OFF");
            }

            case "enter" -> enteringBathroom();
            case "exit" -> exitingBathroom();

            case "lock door" -> {
                setDoorOpen(true);
                updateStatus("Door locked");
            }
            case "unlock door" -> {
                setDoorOpen(false);
                updateStatus("Door unlocked");
            }

            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (lightsOn.get() ? " 💡 " : " 🌑 ") +
                (heaterOn.get() ? " 🔥 " : " ❄ ") +
                (occupied.get() ? " 🚶 " : " 🚪 ") +
                (doorOpen.get() ? " 🔒 " : " 🔓 ");
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isLightsOn() {
        return lightsOn.get();
    }

    public void setLightsOn(boolean lightsOn) {
        this.lightsOn.set(lightsOn);
    }

    public boolean isOccupied() {
        return occupied.get();
    }

    public void setOccupied(boolean occupied) {
        this.occupied.set(occupied);
    }

    public double getWaterTemperature() {
        return waterTemperature.get();
    }

    public void setWaterTemperature(double waterTemperature) {
        this.waterTemperature.set(waterTemperature);
    }

    public boolean isHeaterOn() {
        return heaterOn.get();
    }

    public void setHeaterOn(boolean heaterOn) {
        this.heaterOn.set(heaterOn);
    }

    public boolean isDoorOpen() {
        return doorOpen.get();
    }

    public void setDoorOpen(boolean doorOpen) {
        this.doorOpen.set(doorOpen);
    }

    //───────Enter & Exit bathroom─────────────────────────────
    public void enteringBathroom() {

        if (!doorOpen.get()) {
            setOccupied(true);
            setLightsOn(true);
            updateStatus("Bathroom occupied");
        } else {
            updateStatus("Door is locked!");
        }
    }

    public void exitingBathroom() {
        if (!doorOpen.get()) {
            setOccupied(false);
            setLightsOn(false);
            updateStatus("Bathroom is free");
        } else {
            updateStatus("Door is locked!");
        }
    }

    //───────Configuration of Heater────────────────────────────
    public void configHeater() {
        if (waterTemperature.get() <= 25 && !isHeaterOn()) {
            setHeaterOn(true);
            updateStatus("Heater ON (cold water)");
        } else if (waterTemperature.get() >= 40 && isHeaterOn()) {
            setHeaterOn(false);
            updateStatus("Heater OFF (warm water)");
        }
    }
}
