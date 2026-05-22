#include <WiFi.h>
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include <DHT.h>
#include <Bounce2.h>

const char* ssid = "Karim";
const char* password = "karim2006";
const char* mqtt_server = "95beb7d5a2a34aa4a71acf23b150818d.s1.eu.hivemq.cloud";
const char* mqtt_user = "k4r1m";
const char* mqtt_pass = "Karim2006";

#define DHTPIN 15
DHT dht(DHTPIN, DHT11);
WiFiClientSecure espClient;
PubSubClient client(espClient);

const int numRooms = 5;
const int btnPins[numRooms] = {22, 23, 32, 26, 27}; //Living , Master, Kids, Kitchen, Bathroom
const int ledPins[numRooms] = {4, 5, 18, 19, 21};
bool roomStates[numRooms]   = {false, false, false, false, false};
const char* mqttTopics[numRooms] = {
  "home/livingroom/light",
  "home/masterroom/light",
  "home/kidsroom/light",
  "home/kitchen/light",
  "home/bathroom/light"
};

Bounce buttons[numRooms];

void setup(){
  Serial.begin(115200);
  dht.begin();
  setup_wifi();

  espClient.setInsecure();
  client.setServer(mqtt_server, 8883);
  client.setCallback(callback);

  for (int i = 0; i < numRooms; i++) {
    pinMode(ledPins[i], OUTPUT);
    buttons[i].attach(btnPins[i], INPUT_PULLUP);
    buttons[i].interval(50); 
  }
}

void loop(){
  connect();
  handleButtons();
  readTemp();
}

void connect(){
  if(!client.connected()) reconnect();
  client.loop();
}

void handleButtons(){
  for (int i = 0; i < numRooms; i++){
    buttons[i].update();
    if (buttons[i].fell()) {
      roomStates[i] = !roomStates[i];
      digitalWrite(ledPins[i], roomStates[i] ? HIGH : LOW);
      
      const char* msg = roomStates[i] ? "ON" : "OFF";
      client.publish(mqttTopics[i], msg, true);
    }
  }
}

void readTemp(){
  static unsigned long lastMsg = 0;
  if(millis() - lastMsg > 2000){
    lastMsg = millis();
    float temp = dht.readTemperature();
    if(!isnan(temp)){
      client.publish("home/all/temp", String(temp, 1).c_str());
    }
  }
}

void callback(char* topic, byte* payload, unsigned int length){
  String message = "";
  for(int i = 0; i < length; i++) message += (char)payload[i];
  bool state = (message == "ON");

  for(int i = 0; i < numRooms; i++){
    if(String(topic) == mqttTopics[i]){
      roomStates[i] = state;
      digitalWrite(ledPins[i], state);
      break;
    }
  }
}

void setup_wifi(){
  Serial.print("\nConnecting to Wifi");
  WiFi.begin(ssid, password, 0, NULL, true);
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected!");
}

void reconnect(){
  while (!client.connected()){
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP32Client-" + String(random(0xffff), HEX);
    if(client.connect(clientId.c_str(), mqtt_user, mqtt_pass)){
      Serial.println(" connected!");
      client.subscribe("home/+/light");
    } else {
      delay(5000);
    }
  }
}
