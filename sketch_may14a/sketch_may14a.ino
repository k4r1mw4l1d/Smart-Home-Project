#include <WiFi.h>
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include "DHT.h"

// إعدادات الشبكة والكلاود
const char* ssid = "Karim";
const char* password = "karim2006";
const char* mqtt_server = "95beb7d5a2a34aa4a71acf23b150818d.s1.eu.hivemq.cloud";
const char* mqtt_user = "k4r1m";
const char* mqtt_pass = "Karim2006";

// إعدادات الـ DHT والـ LEDs
#define DHTPIN 15
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

const int LED_LIVING  = 4;
const int LED_MASTER  = 5;
const int LED_KIDS    = 18;
const int LED_KITCHEN = 19;
const int LED_BATH    = 21;

const int BTN_LIVING  = 22;
const int BTN_MASTER  = 23;
const int BTN_KIDS    = 32;
const int BTN_KITCHEN = 26;
const int BTN_BATH    = 27;

bool state_living  = false;
bool state_master  = false;
bool state_kids    = false;
bool state_kitchen = false;
bool state_bath    = false;

const unsigned long DEBOUNCE_DELAY = 50;

struct Button {
  int pin;
  bool lastReading;
  bool *roomState;
  const char* topic;
  int ledPin;
  unsigned long lastDebounce;
  bool triggered;
};

Button buttons[5] = {
  {BTN_LIVING,  HIGH, &state_living,  "home/livingroom/light", LED_LIVING,  0, false},
  {BTN_MASTER,  HIGH, &state_master,  "home/masterroom/light", LED_MASTER,  0, false},
  {BTN_KIDS,    HIGH, &state_kids,    "home/kidsroom/light",   LED_KIDS,    0, false},
  {BTN_KITCHEN, HIGH, &state_kitchen, "home/kitchen/light",    LED_KITCHEN, 0, false},
  {BTN_BATH,    HIGH, &state_bath,    "home/bathroom/light",   LED_BATH,    0, false},
};

WiFiClientSecure espClient;
PubSubClient client(espClient);

void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to: ");
  Serial.println(ssid);
  WiFi.begin(ssid, password, 0, NULL, true);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected!");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void publishLight(const char* topic, bool state) {
  const char* msg = state ? "ON" : "OFF";
  client.publish(topic, msg, true);
  Serial.print("Published [");
  Serial.print(topic);
  Serial.print("]: ");
  Serial.println(msg);
}

void handleButtons() {
  for (int i = 0; i < 5; i++) {
    Button &b = buttons[i];
    bool reading = digitalRead(b.pin);

    if (reading != b.lastReading) {
      b.lastDebounce = millis();
      b.triggered = false;
    }

    if (!b.triggered && (millis() - b.lastDebounce) > DEBOUNCE_DELAY) {
      if (reading == LOW) {
        *b.roomState = !(*b.roomState);
        digitalWrite(b.ledPin, *b.roomState ? HIGH : LOW);
        publishLight(b.topic, *b.roomState);
        b.triggered = true;
      }
    }

    b.lastReading = reading;
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  String message = "";
  for (int i = 0; i < length; i++) message += (char)payload[i];

  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("]: ");
  Serial.println(message);

  bool state = (message == "ON");
  String strTopic = String(topic);

  if      (strTopic == "home/livingroom/light") { state_living  = state; digitalWrite(LED_LIVING,  state); }
  else if (strTopic == "home/masterroom/light") { state_master  = state; digitalWrite(LED_MASTER,  state); }
  else if (strTopic == "home/kidsroom/light")   { state_kids    = state; digitalWrite(LED_KIDS,    state); }
  else if (strTopic == "home/kitchen/light")    { state_kitchen = state; digitalWrite(LED_KITCHEN, state); }
  else if (strTopic == "home/bathroom/light")   { state_bath    = state; digitalWrite(LED_BATH,    state); }
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP32Client-" + String(random(0xffff), HEX);

    if (client.connect(clientId.c_str(), mqtt_user, mqtt_pass)) {
      Serial.println(" connected!");
      client.subscribe("home/+/light");
      Serial.println("Subscribed to: home/+/light");
    } else {
      Serial.print(" failed, rc=");
      Serial.print(client.state());
      Serial.println(" — retrying in 5s");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  delay(500);
  Serial.println("Starting ESP32 Smart Home...");

  pinMode(LED_LIVING,  OUTPUT);
  pinMode(LED_MASTER,  OUTPUT);
  pinMode(LED_KIDS,    OUTPUT);
  pinMode(LED_KITCHEN, OUTPUT);
  pinMode(LED_BATH,    OUTPUT);

  pinMode(BTN_LIVING,  INPUT_PULLUP);
  pinMode(BTN_MASTER,  INPUT_PULLUP);
  pinMode(BTN_KIDS,    INPUT_PULLUP);
  pinMode(BTN_KITCHEN, INPUT_PULLUP);
  pinMode(BTN_BATH,    INPUT_PULLUP);

  dht.begin();
  setup_wifi();

  espClient.setInsecure();
  client.setServer(mqtt_server, 8883);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) reconnect();
  client.loop();

  handleButtons();

  static unsigned long lastMsg = 0;
  if (millis() - lastMsg > 2000) {
    lastMsg = millis();
    float t = dht.readTemperature();
    if (!isnan(t)) {
      char tempString[8];
      dtostrf(t, 1, 1, tempString);
      Serial.print("Publishing temperature: ");
      Serial.println(tempString);
      client.publish("home/all/temp", tempString);
    } else {
      Serial.println("Failed to read from DHT sensor!");
    }
  }
}