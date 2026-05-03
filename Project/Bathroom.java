/*
 *     ===========Bathroom==========
 *     consists of: 1. Lighting control system.
 *                  2. Heater and water temperature.
 *                  3. Checking is occupied.
 *                  4. Different modes.
 */

public class Bathroom extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private boolean isLightsOn;
    private boolean isOccupied;
    private boolean isHeaterOn;
    private boolean isDoorLocked;
    private double waterTemperature;

    // ──────Constructor───────────────────────────────────────
    public Bathroom(String deviceId, String name, String room,
                    boolean isLightsOn, boolean isOccupied, boolean isHeaterOn, double waterTemperature, boolean isDoorLocked) {
        super(deviceId, name, room);
        this.isLightsOn = isLightsOn;
        this.isOccupied = isOccupied;
        this.isHeaterOn = isHeaterOn;
        this.waterTemperature = waterTemperature;
        this.isDoorLocked = isDoorLocked;
    }

    // ─────Reading device state───────────────────────────
    @Override
    public void readState() {
        updateStatus(getStatusIcon());
    }

    // ────Sending commands to devices─────────────────────
    @Override
    public void sendCommand(String cmd) {

        switch (cmd.toLowerCase()) {

            case ("lights on") -> {
                setLightsOn(true);
                updateStatus("lights on");
            }

            case ("lights off") -> {
                setLightsOn(false);
                updateStatus("lights off");
            }

            case ("heater on") -> {
                setHeaterOn(true);
                updateStatus("The bathroom heater is on");
            }

            case ("heater off") -> {
                setHeaterOn(false);
                updateStatus("The bathroom heater is off");
            }

            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (isLightsOn ? "💡" : "🌑") +
                (isHeaterOn ? "🔥" : "❄") +
                (isOccupied ? "🚶" : "🚪") +
                (isDoorLocked ? "🔒" : "🔓");
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isLightsOn() {
        return isLightsOn;
    }

    public void setLightsOn(boolean lightsOn) {
        isLightsOn = lightsOn;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public double getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(double waterTemperature) {
        this.waterTemperature = waterTemperature;
        configHeater();
    }

    public boolean isHeaterOn() {
        return isHeaterOn;
    }

    public void setHeaterOn(boolean heaterOn) {
        isHeaterOn = heaterOn;
    }

    public boolean isDoorLocked() {
        return isDoorLocked;
    }

    public void setDoorLocked(boolean doorLocked) {
        isDoorLocked = doorLocked;
        updateStatus("Door state changed");
    }

    //───────Enter bathroom─────────────────────────────
    public void enteringBathroom() {

        if (!isDoorLocked) {
            setOccupied(true);
            setLightsOn(true);
            updateStatus("Bathroom occupied");
        } else {
            updateStatus("Door is locked!");
        }
    }

    //───────Configuration of AC─────────────────────────────
    public void configHeater() {
        if (waterTemperature <= 25) {
            setHeaterOn(true);
            updateStatus("Heater ON (cold water)");
        } else if (waterTemperature >= 60) {
            setHeaterOn(false);
            updateStatus("Heater OFF (warm water)");
        }
    }
}
