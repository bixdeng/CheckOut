#include <SoftwareSerial.h>
#include <Stepper.h>
#define RxD 6 //pin that the bluetooth (BT_TX) will transmit to the Arduino (RxD)
#define TxD 7 //pin that blue (BT_RX) will receive from the Arduino (TxD)
#define DEBUG_ENABLED 1
#define RELAY 4

SoftwareSerial blueToothSerial(RxD, TxD);
const int numReadings = 10;

int analogPin = A0;
float load;

int sensorValue = 0;
//int cValueHigh = 0;
//int cValueLow = 1023;
int initialValue = 0;

float loadA = 2;
int analogValueA = 6.5;
float loadB = 4;
int analogValueB = 5;
float analogValueAverage = 0;

void setup() {
  Serial.begin(38400);
  // ----SETUP FOR BLUETOOTH----
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
  Serial.println("Initialize Slave BT...");  
  setupBlueToothConnection();
  while(millis() < 20000) {
    initialValue = analogRead(analogPin);
  }
  // ----END SETUP FOR BLUETOOTH----  
  Serial.flush(); 
}

void loop(){
  char recvChar;
  sensorValue = analogRead(analogPin);
 
//  analogValueAverage = 0.99*analogValueAverage + 0.01*sensorValue;
     
  if(blueToothSerial.available()){
      recvChar = blueToothSerial.read();   
      Serial.print(recvChar);
      load = analogToLoad(sensorValue);
//      load =((sensorValue - initialValue) / 7.6666);
      blueToothSerial.println(load);
      delay(5000);
      switch(recvChar) {
        case 'r': //Read load
//          load =((sensorValue - initialValue) / 7.6666);
          load = analogToLoad(sensorValue);
          blueToothSerial.print(load, 2);
          delay(1000);
        break;
      }
  }  
} 

float analogToLoad(float analogval) {
  float load = mapfloat(analogval - initialValue, analogValueA, analogValueB, loadA, loadB);
  return load;
}

float mapfloat(float x, float in_min, float in_max, float out_min, float out_max) {
  return (x - in_min)*(out_max - out_min) / (in_max - in_min) + out_min;
}
 
//The following code is necessary to setup the bluetooth shield ------copy and paste----------------
void setupBlueToothConnection() {
  blueToothSerial.begin(38400);// BluetoothBee BaudRate to default baud rate 38400
  blueToothSerial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
  blueToothSerial.print("\r\n+STNA=Cart #25\r\n"); //set the bluetooth name as "Cart #25"
  blueToothSerial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
  blueToothSerial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
  blueToothSerial.print("\r\n+STPIN=1234\r\n"); //set pin to 1234
  blueToothSerial.print("\r\n+RTPIN=1234\r\n");//ask to input pin
  delay(2000); // This delay is required.
  blueToothSerial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable 
  Serial.println("The slave bluetooth is inquirable!");
  delay(2000); // This delay is required.
  blueToothSerial.flush(); 
}

