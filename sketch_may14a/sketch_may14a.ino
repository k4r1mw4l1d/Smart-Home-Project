#include <WiFi.h>
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include "DHT.h"

// إعدادات الشبكة والكلاود
const char* ssid = "Galaxy A207FFE";
const char* password = "kdji8713";
const char* mqtt_server = "f74c544c3a2841d99d97115e3b8db081.s1.eu.hivemq.cloud";
const char* mqtt_user = "Ayman_Mo";
const char* mqtt_pass = "Stream54321";

// إعدادات الـ DHT والـ LEDs
#define DHTPIN 15      // الدبوس المتصل بالسلك الأوسط للـ DHT
#define DHTTYPE DHT11  // أو DHT22 حسب اللي معاك
DHT dht(DHTPIN, DHTTYPE);

const int LED_LIVING = 2;   
const int LED_MASTER = 4;   
const int LED_KIDS   = 5;   
const int LED_KITCHEN = 18; 
const int LED_BATH   = 19; 

WiFiClientSecure espClient;
PubSubClient client(espClient);

void setup_wifi() {
  delay(10);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) delay(500);
}

void callback(char* topic, byte* payload, unsigned int length) {
  String message = "";
  for (int i = 0; i < length; i++) message += (char)payload[i];
  String strTopic = String(topic);
  bool state = (message == "ON");

  if (strTopic == "home/livingroom/light") digitalWrite(LED_LIVING, state);
  else if (strTopic == "home/masterroom/light") digitalWrite(LED_MASTER, state);
  else if (strTopic == "home/kidsroom/light") digitalWrite(LED_KIDS, state);
  else if (strTopic == "home/kitchen/light") digitalWrite(LED_KITCHEN, state);
  else if (strTopic == "home/bathroom/light") digitalWrite(LED_BATH, state);
}

void reconnect() {
  while (!client.connected()) {
    String clientId = "ESP32Client-" + String(random(0xffff), HEX);
    if (client.connect(clientId.c_str(), mqtt_user, mqtt_pass)) {
      client.subscribe("home/+/light"); // الاشتراك في كل أنوار الغرف
    } else {
      delay(5000);
    }
  }
}

void setup() {
  pinMode(LED_LIVING, OUTPUT);
  pinMode(LED_MASTER, OUTPUT);
  pinMode(LED_KIDS, OUTPUT);
  pinMode(LED_KITCHEN, OUTPUT);
  pinMode(LED_BATH, OUTPUT);
  dht.begin();
  setup_wifi();
  espClient.setInsecure();
  client.setServer(mqtt_server, 8883);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) reconnect();
  client.loop();

  static unsigned long lastMsg = 0;
  if (millis() - lastMsg > 2000) { // قراءة كل ثانيتين
    lastMsg = millis();
    float t = dht.readTemperature();
    if (!isnan(t)) {
      char tempString[8];
      dtostrf(t, 1, 1, tempString);
      client.publish("home/all/temp", tempString); // Topic عام لكل البيت
    }
  }
}