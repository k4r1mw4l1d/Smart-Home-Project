/*
 *     ===========Kids Room===========
 *     consists of: 1. Lighting control system.
 *                  2. AC controller.
 *                  3. safety Check.
 *                  4. Bedtime mode.
 *                  5. motion sensor for kids.
 */


public class KidsRoom extends SmartDevice {

    // ───Attributes────────────────────────────────────────────
    MasterRoom masterRoom;
    private boolean isLightsOn;
    private double temperature;
    private boolean isBedTime;
    private boolean isAwake;
    private boolean isACOn;
    private boolean safety;

    // ──────Constructor───────────────────────────────────────
    public KidsRoom(String deviceId, String name, String room,
                    boolean isLightsOn, double temperature, boolean isACOn, boolean safety, boolean isBedTime,
                    boolean isAwake, MasterRoom masterRoom) {
        super(deviceId, name, room);
        this.isLightsOn = isLightsOn;
        this.temperature = temperature;
        this.isACOn = isACOn;
        this.safety = safety;
        this.isBedTime = isBedTime;
        this.isAwake = isAwake;
        this.masterRoom = masterRoom;
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

            case ("the baby is awake") -> {
                motionDetector();
                updateStatus("The baby is awake");
            }

            case ("ac on") -> {
                setACOn(true);
                updateStatus("Ac on");
            }

            case ("ac off") -> {
                setACOn(false);
                updateStatus("Ac off");
            }

            default -> updateStatus("INVALID COMMAND");
        }
    }

    // ──────Visual icons for status──────────────────────
    @Override
    public String getStatusIcon() {
        return (isLightsOn ? "💡" : "🌑") +
                (isACOn ? "❄" : "🔥") +
                (safety ? "✅" : "⚠") +
                (isBedTime ? "🌙" : "☀") +
                (isAwake ? "👶" : "😴");
    }

    // ───────Setter & Getters───────────────────────────────
    public boolean isLightsOn() {
        return isLightsOn;
    }

    public void setLightsOn(boolean lightsOn) {
        isLightsOn = lightsOn;
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
    }

    public boolean isSafe() {
        return safety;
    }

    public void setSafety(boolean safety) {
        this.safety = safety;
    }

    public boolean isBedTime() {
        return isBedTime;
    }

    public void setBedTime(boolean bedTime) {
        isBedTime = bedTime;
    }

    public boolean isAwake() {
        return isAwake;
    }

    public void setAwake(boolean awake) {
        isAwake = awake;
    }

    //───────Bed time mode─────────────────────────────
    public void bedTimeMode() {

        if (isBedTime) return;
        setBedTime(true);
        setLightsOn(false);
        setACOn(false);
        setSafety(true);
        setAwake(false);
        updateStatus("It's bed time");
    }

    // ───────Motion handler for kids───────────────────
    public void motionDetector() {

        setAwake(true);
        setBedTime(false);
        setLightsOn(true);
        setSafety(true);
        if (masterRoom != null) {
            masterRoom.setLightsOn(true);
        }
        updateStatus("Motion detected -> Child awake");
    }

    // ───────Automatic AC─────────────────────────────
    public void configAC() {

        if (getTemperature() >= 30 && !isACOn()) {
            setACOn(true);
            updateStatus("AC activated");

        } else if (getTemperature() <= 26 && isACOn()) {
            setACOn(false);
            updateStatus("AC deactivated");
        }
    }
}