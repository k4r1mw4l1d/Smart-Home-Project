public interface MQTT {
    public void publish(String topic, String payload);
    public void publishState();
    public String buildPayload();
}
