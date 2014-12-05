#include <Servo.h>
#include <Wire.h>
#include <PVision.h> // http://www.stephenhobley.com


#define ANGLE_LOWER_BOUND 1 // Standard Servo Input
#define ANGLE_UPPER_BOUND 180 // Standard Servo Input
#define DEGREE_PER_PIXEL = 0.0294118  // 1.0/34.0; // degrees per pixel
#define SERVO_PIN          3  // standard servo attached to pin 3
#define minServoUpdateTimeDiffInMillis     1      // limits the time that the servo will be updated from the Arduino

#define dataTransmissionTimeOutInMillis    100    // after this length in milliseconds, data will be transmitted

#define JUMPER_INPUT_PIN 9
#define JUMPER_HIGH_OUTPUT_PIN 4

#define HIGH_PIN_FOR_NANO   13

Servo standardServo;

byte result;
PVision ircam;

int x1, x2, x3, x4;
int y1, y2, y3, y4;
int s1, s2, s3, s4;

int servoArg = 84;


int LED_PIN = 12;
int numNull = 0;
unsigned long lastServoUpdateInMillis = 0;
unsigned long lastDataTransmissionMillis = 0;

#define numNullLimit 50


unsigned long startTime;
boolean LED_on = false;
boolean isConnected;
int ifNorthScannerEqual_1_ifSouthScannerEqual_2 = 0;

void setup(){
 Serial.begin(115200);
 //Serial.flush();
 
 isConnected = true;
 ircam.init();
 standardServo.attach(SERVO_PIN);
 standardServo.write(servoArg);
 setupScannerIdentifierJumper();
 pinMode(HIGH_PIN_FOR_NANO, OUTPUT);
 digitalWrite(HIGH_PIN_FOR_NANO, HIGH);
 
 
}

void loop(){
  
  while(Serial.available() >= 2){
   updateServo();
    
    
   readFromIRCam();
   
   sendData();
   reInitIRCamCheck();
  }
  
  checkDataTransTimeOut();
  
  
  
}

void setupScannerIdentifierJumper(){
 pinMode(JUMPER_HIGH_OUTPUT_PIN, OUTPUT);
 digitalWrite(JUMPER_HIGH_OUTPUT_PIN,HIGH);
 pinMode(JUMPER_INPUT_PIN, INPUT);
 readNorthSouthJumper();
}

void readNorthSouthJumper(){
 if (digitalRead(JUMPER_INPUT_PIN)){
  ifNorthScannerEqual_1_ifSouthScannerEqual_2 = 1;
 } else {
  ifNorthScannerEqual_1_ifSouthScannerEqual_2  = 2;
 }
}

byte getByteToSendToIdentifyNorthOrSouthScanner(){
 readNorthSouthJumper();
 byte retVal = (byte) 0;
 if ( ifNorthScannerEqual_1_ifSouthScannerEqual_2 == 1){
   retVal = (byte) 78;
 }
 if (ifNorthScannerEqual_1_ifSouthScannerEqual_2 == 2){
   retVal = (byte) 83;
 }
 return retVal;
}

void checkDataTransTimeOut(){
  if (millis() - lastDataTransmissionMillis > dataTransmissionTimeOutInMillis){
   readFromIRCam();
   sendData();
   reInitIRCamCheck(); 
  }
}

void updateServo(){
 if (Serial.available() >= 2){
   
  byte inputByte = Serial.read();
  servoArg   = (int)inputByte;
  if (servoArg != 0){
    if (servoArg >= ANGLE_UPPER_BOUND){
     servoArg = ANGLE_UPPER_BOUND; 
    }
    if (servoArg <= ANGLE_LOWER_BOUND){
     servoArg = ANGLE_LOWER_BOUND; 
    }
  
    long timeDelta = millis() - lastServoUpdateInMillis;
    if (timeDelta < 0 || timeDelta >= minServoUpdateTimeDiffInMillis){
     standardServo.write(servoArg); 
     lastServoUpdateInMillis = millis();
    }
  }
  
  inputByte = Serial.read();
  if (inputByte == (byte)0){
    LED_on = false;
  } else {
    LED_on = true;
  }
  
 } 
}

void reInitIRCamCheck(){
  if (numNull >= numNullLimit){
   ircam.init();
   numNull = 0; 
  }
}




void readFromIRCam(){
  boolean nullThisTime = true;
  result = ircam.read();
  if (result & BLOB1){
    nullThisTime = false;
    x1 = (int) ircam.Blob1.X;
    y1 = (int) ircam.Blob1.Y;
    s1 = (int) ircam.Blob1.Size;
  } else {
    x1 = 0;
    y1 = 0;
    s1 = 0; 
  }
  if (result & BLOB2){
    nullThisTime = false;
    x2 = (int) ircam.Blob2.X;
    y2 = (int) ircam.Blob2.Y;
    s2 = (int) ircam.Blob2.Size;
  } else {
    x2 = 0;
    y2 = 0;
    s2 = 0; 
  }
  if (result & BLOB3){
    nullThisTime = false;
    x3 = (int) ircam.Blob3.X;
    y3 = (int) ircam.Blob3.Y;
    s3 = (int) ircam.Blob3.Size;
  } else {
    x3 = 0;
    y3 = 0;
    s3 = 0; 
  }
  if (result & BLOB4){
    nullThisTime = false;
    x4 = (int) ircam.Blob4.X;
    y4 = (int) ircam.Blob4.Y;
    s4 = (int) ircam.Blob4.Size;
  } else {
    x4 = 0;
    y4 = 0;
    s4 = 0; 
  }
  if (nullThisTime){
   numNull++; 
  } else {
   numNull = 0; 
  }
}

void sendData(){ 
  Serial.flush();
  sendStartSignal();
  
  sendInt_Unsigned16BitLength(x1);
  sendInt_Unsigned16BitLength(y1);
  sendInt_Unsigned16BitLength(s1);
  
  sendInt_Unsigned16BitLength(x2);
  sendInt_Unsigned16BitLength(y2);
  sendInt_Unsigned16BitLength(s2);
  
  sendInt_Unsigned16BitLength(x3);
  sendInt_Unsigned16BitLength(y3);
  sendInt_Unsigned16BitLength(s3);
  
  sendInt_Unsigned16BitLength(x4);
  sendInt_Unsigned16BitLength(y4);
  sendInt_Unsigned16BitLength(s4);
  
  if (LED_on){
    Serial.write((byte) 255);
  } else {
    Serial.write((byte) 0);
  }
  lastDataTransmissionMillis = millis();
}

void sendInt_Unsigned16BitLength(int i){
  byte lsb = (byte)(i % 256);
  byte msb = (byte)(i / 256);
  Serial.write(lsb);
  Serial.write(msb);
}

void sendStartSignal(){
 byte b = (byte)87;   // W
 Serial.write(b);
 b = (byte)105;       // i
 Serial.write(b); 
 b = (byte)105;       // i
 Serial.write(b); 
 b = (byte)68;        // D
 Serial.write(b); 
 b = (byte)97;        // a
 Serial.write(b); 
 b = (byte)116;       // t
 Serial.write(b); 
 b = (byte)97;        // a
 Serial.write(b); 
 
 b = getByteToSendToIdentifyNorthOrSouthScanner();
 Serial.write(b);
 
}

