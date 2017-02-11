#include <Arduino.h>
#include <SoftwareSerial.h>

#define TX 10
#define RX 11

#define LASER_INPUT A0

SoftwareSerial mySerial(RX, TX);

void setup() {

  Serial.begin(9600); Serial.println("inizio!");

  pinMode(LASER_INPUT,INPUT);



  mySerial.begin(9600);
  Serial.println("ARDUINO SPEEDOMETER started");
  mySerial.println("ARDUINO SPEEDOMETER started");
}

// da 370 a 1024 void loop() { //digitalWrite(13,digitalRead(4));
void loop(){
  if (mySerial.available())

    Serial.write(mySerial.read());

  if (Serial.available()){ //char s = Serial.read(); //Serial.write(s);
  mySerial.write(Serial.read()); }

}
