#include <WiFi.h>
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include <DHT.h>

const char* ssid = "Karim";
const char* password = "karim2006";
const char* mqtt_server = "95beb7d5a2a34aa4a71acf23b150818d.s1.eu.hivemq.cloud";
const char* mqtt_user = "k4r1m";
const char* mqtt_pass = "Karim2006";

#define DHTPIN       15
#define LED_LIVING   4
#define LED_MASTER   5
#define LED_KIDS     18
#define LED_KITCHEN  19
#define LED_BATH     21
#define BTN_LIVING   22
#define BTN_MASTER   23
#define BTN_KIDS     32
#define BTN_KITCHEN  26
#define BTN_BATH     27

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
};

Button buttons[5] = {
  {BTN_LIVING,  HIGH, &state_living,  "home/livingroom/light", LED_LIVING,  0},
  {BTN_MASTER,  HIGH, &state_master,  "home/masterroom/light", LED_MASTER,  0},
  {BTN_KIDS,    HIGH, &state_kids,    "home/kidsroom/light",   LED_KIDS,    0},
  {BTN_KITCHEN, HIGH, &state_kitchen, "home/kitchen/light",    LED_KITCHEN, 0},
  {BTN_BATH,    HIGH, &state_bath,    "home/bathroom/light",   LED_BATH,    0},
};

DHT dht(DHTPIN, DHT11);
WiFiClientSecure espClient;
PubSubClient client(espClient);

void setup(){
  Serial.begin(115200);
  delay(500);
  Serial.println("Smart Home...");

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

void loop(){
  if(!client.connected()) reconnect();
  client.loop();
  handleButtons();
  readTemp();
}


void setup_wifi(){
  delay(10);
  Serial.print("\nConnecting to Wifi");
  WiFi.begin(ssid, password, 0, NULL, true);
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected!");
}

void handleButtons(){
  for(int i = 0; i < 5; i++){
    Button *b = &buttons[i]; 
    bool reading = digitalRead(b->pin);

    if(reading != b->lastReading) b->lastDebounce = millis();

    if((millis() - b->lastDebounce) > DEBOUNCE_DELAY){
      if(reading == LOW && b->lastReading == HIGH){
        *(b->roomState) = !(*(b->roomState));
        digitalWrite(b->ledPin, *(b->roomState) ? HIGH : LOW);
        publishLight(b->topic, *(b->roomState));
      }
    }
    b->lastReading = reading;
  }
}

void publishLight(const char* topic, bool state){
  const char* msg = state ? "ON" : "OFF";
  client.publish(topic, msg, true);
}

void readTemp(){
  static unsigned long lastMsg = 0;
  
  if(millis() - lastMsg > 2000){
    lastMsg = millis();
    float temp = dht.readTemperature();
    
    if(!isnan(temp)){
      String tempStr = String(t, 1); 
      client.publish("home/all/temp", tempStr.c_str());
    } 
    else{
      Serial.println("Error in DHT");
    }
  }
}

void callback(char* topic, byte* payload, unsigned int length){
  String message = "";
  for(int i = 0; i < length; i++) message += (char)payload[i];

  bool state = (message == "ON");
  String strTopic = String(topic);

  if      (strTopic == "home/livingroom/light") {state_living  = state; digitalWrite(LED_LIVING,  state);}
  else if (strTopic == "home/masterroom/light") {state_master  = state; digitalWrite(LED_MASTER,  state);}
  else if (strTopic == "home/kidsroom/light")   {state_kids    = state; digitalWrite(LED_KIDS,    state);}
  else if (strTopic == "home/kitchen/light")    {state_kitchen = state; digitalWrite(LED_KITCHEN, state);}
  else if (strTopic == "home/bathroom/light")   {state_bath    = state; digitalWrite(LED_BATH,    state);}
}

void reconnect(){
  while (!client.connected()){
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP32Client-" + String(random(0xffff), HEX);

    if(client.connect(clientId.c_str(), mqtt_user, mqtt_pass)){
      Serial.println(" connected!");
      client.subscribe("home/+/light");
    } 
    else{
      Serial.println("Failed — retrying in 5s");
      delay(5000);
    }
  }
}