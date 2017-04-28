#include <Arduino.h>
#include <SoftwareSerial.h>

#define TX 10
#define RX 11
#define LASER_VCC 2
#define LASER_INPUT 3
#define LASER_GND 4
#define LED_PIN 13
//#define TEST_ENABLED

SoftwareSerial mySerial(RX, TX);

void printMillis(){
   mySerial.print(millis());
   mySerial.print("|");
}

#ifdef TEST_ENABLED
void test(){
   int millisecond = random(0, 1000*20); // from 0 to 20 sec
   delay(millisecond);
   printMillis();
}
#endif

void setup(){
   Serial.begin(9600);
   Serial.println("inizio!");
  
   pinMode(LASER_INPUT, INPUT);
   pinMode(LASER_VCC,OUTPUT);
   digitalWrite(LASER_VCC,HIGH);
   pinMode(LASER_GND,OUTPUT);
   digitalWrite(LASER_GND,LOW);

   mySerial.begin(9600);
   Serial.println("ARDUINO SPEEDOMETER started");
   mySerial.println("ARDUINO SPEEDOMETER started");
}

inline bool laserInterruped(){
   return !digitalRead(LASER_INPUT);
}

void checkForStartMessages(){
   if (mySerial.available()){
      int inByte = mySerial.read();
      printMillis();
   }
}

bool laserInterrupedState = laserInterruped();

void loop() {
#ifdef TEST_ENABLED
   test();
#else
   digitalWrite(LED_PIN,laserInterrupedState);
   while (laserInterruped() == laserInterrupedState) checkForStartMessages();
   laserInterrupedState = !laserInterrupedState;
   printMillis();
#endif
}
