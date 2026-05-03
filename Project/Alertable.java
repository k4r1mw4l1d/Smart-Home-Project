import java.util.List;

public interface Alertable {
    public void triggerAlert(String message);
    public List<String> getAlertHistory();
}
