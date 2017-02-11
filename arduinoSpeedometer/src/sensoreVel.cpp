#include <Arduino.h>
#include <SoftwareSerial.h>

#define TX 10
#define RX 11
#define LASER_INPUT A0

#define TEST_ENABLED

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

   mySerial.begin(9600);
   Serial.println("ARDUINO SPEEDOMETER started");
   mySerial.println("ARDUINO SPEEDOMETER started");
}

inline bool laserInterruped(){
   return digitalRead(LASER_INPUT);
}

void loop() {
#ifdef TEST_ENABLED
   test();
#else
   while (!laserInterruped());
   mySerial.print(millis()); mySerial.print("-");
   while (laserInterruped());
#endif
}
