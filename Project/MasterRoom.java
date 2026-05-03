/*
 *    ============Master Room============
 *    consists of : 1. lighting control system.
 *                  2. AC controller.
 *                  3. smart modes.
 *                  4. door safety.
 *                  5. TV controller.
 */

public class MasterRoom extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    private boolean isDoorLocked;
    private boolean isLightsOn;
    private double temperature;
    private String smartScene;
    private boolean isACOn;
    private boolean isTVOn;


    // ──────Constructor───────────────────────────────────────
    public MasterRoom(String deviceId, String name, String room, boolean isLightsOn, double temperature,
                      boolean isACOn, String smartScene, boolean isDoorLocked, boolean isTVOn) {
        super(deviceId, name, room);

        this.isLightsOn = isLightsOn;
        this.temperature = temperature;
        this.isACOn = isACOn;
        this.smartScene = smartScene;
        this.isDoorLocked = isDoorLocked;
        this.isTVOn = isTVOn;

        updateStatus("Device Initialized");
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

            case "lights on" -> {
                setLightsOn(true);
                updateStatus("Lights ON");
            }

            case "lights off" -> {
                setLightsOn(false);
                updateStatus("Lights OFF");
            }

            case "ac on" -> {
                setACOn(true);
                updateStatus("AC ON");
            }

            case "ac off" -> {
                setACOn(false);
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
                setTVOn(true);
                updateStatus("TV ON");
            }

            case "tv off" -> {
                setTVOn(false);
                updateStatus("TV OFF");
            }

            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (isLightsOn ? "💡" : "🌑") +
                (isACOn ? "❄" : "🔥") +
                (isDoorLocked ? "🔒" : "🔓") +
                (isTVOn ? "📺" : "📴");
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isLightsOn() {
        return isLightsOn;
    }

    public void setLightsOn(boolean lightsOn) {
        isLightsOn = lightsOn;
        updateStatus("Lights updated");
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        configAC();
    }

    public boolean isACOn() {
        return isACOn;
    }

    public void setACOn(boolean ACOn) {
        isACOn = ACOn;
        updateStatus("AC state changed");
    }

    public String getSmartScene() {
        return smartScene;
    }

    public void setSmartScene(String smartScene) {
        this.smartScene = smartScene;
        applyScene();
    }

    public boolean isDoorLocked() {
        return isDoorLocked;
    }

    public void setDoorLocked(boolean doorLocked) {
        isDoorLocked = doorLocked;
        updateStatus("Door state changed");
    }

    public boolean isTVOn() {
        return isTVOn;
    }

    public void setTVOn(boolean isTVon) {
        this.isTVOn = isTVon;
        updateStatus("TV state changed");
    }

    // ───────Automatic AC─────────────────────────────
    public void configAC() {

        if (getTemperature() >= 30 && !isACOn()) {
            setACOn(true);
            updateStatus("AC activated");

        } else if (getTemperature() <= 22 && isACOn()) {
            setACOn(false);
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
                setTVOn(false);
                updateStatus("Sleep Mode Activated");
            }

            case "romance mode" -> {
                setLightsOn(true);
                setDoorLocked(true);
                updateStatus("Romance Mode Activated");
            }

            case "relax mode" -> {
                setLightsOn(true);
                setTVOn(true);
                setDoorLocked(true);
                updateStatus("Relax Mode Activated");
            }

            default -> updateStatus("INVALID MODE");
        }
    }
}