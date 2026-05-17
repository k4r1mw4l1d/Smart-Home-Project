import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import javafx.application.Platform;

public class MQTT {
    private String broker = "ssl://95beb7d5a2a34aa4a71acf23b150818d.s1.eu.hivemq.cloud:8883";
    private String clientId = "JavaFX_SmartHome_" + System.currentTimeMillis();
    private String username = "k4r1m";
    private String password = "Karim2006";

    private MqttClient client;

    private LivingRoom livingRoom;
    private MasterRoom masterRoom;
    private KidsRoom childrenRoom;
    private Kitchen kitchen;
    private Bathroom bathroom;

    public void setModels(LivingRoom lr, MasterRoom mr, Kitchen k, Bathroom b) {
        this.livingRoom = lr;
        this.masterRoom = mr;
        this.kitchen = k;
        this.bathroom = b;
    }

    public void connect() {
        try {
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setSocketFactory(javax.net.ssl.SSLSocketFactory.getDefault());
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            connOpts.setCleanSession(true);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());

                    Platform.runLater(() -> {
                        if (topic.equals("home/all/temp")) {
                            double temp = Double.parseDouble(payload);
                            if (livingRoom != null) livingRoom.setTemperature(temp);
                            if (masterRoom != null) masterRoom.setTemperature(temp);
                            if (kitchen != null) kitchen.setTemperature(temp);
                        } else if (topic.contains("/light")) {
                            boolean state = payload.equalsIgnoreCase("ON");
                            if (topic.contains("livingroom")) livingRoom.setLightsOn(state);
                            else if (topic.contains("masterroom")) masterRoom.setLightsOn(state);
                            else if (topic.contains("kitchen")) kitchen.setLightsOn(state);
                            else if (topic.contains("bathroom")) bathroom.setLightsOn(state);
                        }
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            client.connect(connOpts);
            client.subscribe("home/#", 0);
            System.out.println("MQTT Connected & Subscribed to all rooms!");

        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    public void publish(String topic, String content) {
        try {
            if (client != null && client.isConnected()) {
                client.publish(topic, content.getBytes(), 0, false);
            }
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }
}