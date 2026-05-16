#include <WiFi.h>
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include "DHT.h"

// إعدادات الشبكة والكلاود
const char* ssid = "Kw@@@2006";
const char* password = "WalidAbdelraouf@@@19721983";
const char* mqtt_server = "95beb7d5a2a34aa4a71acf23b150818d.s1.eu.hivemq.cloud";
const char* mqtt_user = "k4r1m";
const char* mqtt_pass = "Karim2006";

// إعدادات الـ DHT والـ LEDs
#define DHTPIN 15      // الدبوس المتصل بالسلك الأوسط للـ DHT
#define DHTTYPE DHT11  // أو DHT22 حسب اللي معاك
DHT dht(DHTPIN, DHTTYPE);

const int LED_LIVING = 4;   
const int LED_MASTER = 5;   
const int LED_KIDS   = 18;   
const int LED_KITCHEN = 19; 
const int LED_BATH   = 21; 

WiFiClientSecure espClient;
PubSubClient client(espClient);

void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to hidden network: ");
  Serial.println(ssid);

  // البارامتر الأخير (true) يتيح الاتصال بالشبكات المخفية
  WiFi.begin(ssid, password, 0, NULL, true);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected!");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) {
  String message = "";
  for (int i = 0; i < length; i++) message += (char)payload[i];
  
  Serial.print("Message arrived on topic [");
  Serial.print(topic);
  Serial.print("]: ");
  Serial.println(message);

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
    Serial.print("Attempting HiveMQ MQTT connection...");
    String clientId = "ESP32Client-" + String(random(0xffff), HEX);
    
    if (client.connect(clientId.c_str(), mqtt_user, mqtt_pass)) {
      Serial.println(" connected!");
      client.subscribe("home/+/light"); // الاشتراك في كل أنوار الغرف
      Serial.println("Subscribed to: home/+/light");
    } else {
      Serial.print(" failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void setup() {
  // تفعيل المسلسل لمراقبة البيانات
  Serial.begin(115200);
  delay(500); // مهلة قصيرة لاستقرار الـ Serial
  Serial.println("Starting ESP32 Smart Home...");

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
      
      Serial.print("Publishing temperature: ");
      Serial.println(tempString);
      
      client.publish("home/all/temp", tempString); // Topic عام لكل البيت
    } else {
      Serial.println("Failed to read from DHT sensor!");
    }
  }
}