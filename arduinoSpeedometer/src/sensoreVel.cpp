#include <Arduino.h>
#include <SoftwareSerial.h>

#define TX 10
#define RX 11
#define LASER_VCC 2
#define LASER_INPUT 3
#define LASER_GND 4

//#define TEST_ENABLED

SoftwareSerial mySerial(RX, TX);

#ifdef TEST_ENABLED
void test(){
   int millisecond = random(0, 1000*20); // from 0 to 20 sec
   delay(millisecond);
   mySerial.print(millis());
   mySerial.print("-");
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
// da 370 a 1024 void loop() { //digitalWrite(13,digitalRead(4));
void loop() {
#ifdef TEST_ENABLED
   test();
#else
   digitalWrite(13,LOW);
   while (laserInterruped());
   mySerial.print(millis()); mySerial.print("-");
   Serial.println(millis());
   digitalWrite(13,HIGH);
   while (!laserInterruped());
   mySerial.print(millis()); mySerial.print("-");
   Serial.println(millis());

#endif
}
