import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.*;

public abstract class SmartDevice {

    // Identity
    private final String deviceId;
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty room = new SimpleStringProperty();

    // State
    private final BooleanProperty online = new SimpleBooleanProperty(false);
    private final StringProperty  status = new SimpleStringProperty("UNKNOWN");
    private final ObjectProperty<LocalDateTime> lastUpdated =
            new SimpleObjectProperty<>(LocalDateTime.now());

    // Alert history
    private final List<String> alertHistory = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    protected SmartDevice(String deviceId, String name, String room) {
        this.deviceId = deviceId;
        this.name.set(name);
        this.room.set(room);
    }

    // ── Abstract methods ──────────────────────────────────────────────────────

    public abstract void readState();
    public abstract void sendCommand(String cmd);
    public abstract String getStatusIcon();

    // ── MQTT (simulated) ──────────────────────────────────────────────────────

    public void publish(String topic, String payload) {
        System.out.printf("[MQTT] topic=%-40s  payload=%s%n", topic, payload);
    }

    public void publishState() {
        publish("smarthome/" + room.get() + "/" + deviceId, buildPayload());
    }

    protected String buildPayload() {
        return String.format("{\"id\":\"%s\",\"status\":\"%s\",\"online\":%b}",
                deviceId, status.get(), online.get());
    }

    // ── Alerts ────────────────────────────────────────────────────────────────

    public void triggerAlert(String message) {
        String entry = LocalDateTime.now() + " | " + name.get() + " | " + message;
        alertHistory.add(entry);
        System.out.println("[ALERT] " + entry);
    }

    public List<String> getAlertHistory() {
        return Collections.unmodifiableList(alertHistory);
    }

    // ── Shared helper ─────────────────────────────────────────────────────────

    protected void updateStatus(String newStatus) {
        status.set(newStatus);
        lastUpdated.set(LocalDateTime.now());
    }

    // ── JavaFX property accessors ─────────────────────────────────────────────

    public String          getDeviceId()                        { return deviceId; }

    public String          getName()                            { return name.get(); }
    public void            setName(String v)                    { name.set(v); }
    public StringProperty  nameProperty()                       { return name; }

    public String          getRoom()                            { return room.get(); }
    public void            setRoom(String v)                    { room.set(v); }
    public StringProperty  roomProperty()                       { return room; }

    public boolean         isOnline()                           { return online.get(); }
    public void            setOnline(boolean v)                 { online.set(v); }
    public BooleanProperty onlineProperty()                     { return online; }

    public String          getStatus()                          { return status.get(); }
    public StringProperty  statusProperty()                     { return status; }

    public LocalDateTime                 getLastUpdated()       { return lastUpdated.get(); }
    public ObjectProperty<LocalDateTime> lastUpdatedProperty()  { return lastUpdated; }

    // ── toString ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("%s [%s] room=%s online=%b status=%s",
                getClass().getSimpleName(), deviceId, room.get(), online.get(), status.get());
    }
}