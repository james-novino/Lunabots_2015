#define DEBUG false

#include <Wire.h>

#include <Ethernet.h>

#include <Dns.h>

#include <socket.h>

#include <util.h>

#include <w5100.h>

#include <Dhcp.h>

#include <EthernetServer.h>

#include <EthernetClient.h>

/*

 UDP beacon control: set the beacon on/off in response to UDP messages,
   then send an ack back to the transmitter
 
 #### Based on UDPSendReceive.pde:
 This sketch receives UDP message strings, prints them to the serial port
 and sends an "acknowledge" string back to the sender

 A Processing sketch is included at the end of file that can be used to send
 and received messages for testing with a computer.

 created 21 Aug 2010
 by Michael Margolis

 This code is in the public domain.
 */


#include <SPI.h>         // needed for Arduino versions later than 0018

#include <EthernetUdp.h>         // UDP library from: bjoern@cs.stanford.edu 12/30/2008
#include <string.h> // memcpy





// pin definitions
#define LED_BEACON_CHANNEL_1_PIN 2
#define LED_BEACON_CHANNEL_2_PIN 3
#define LED_BEACON_CHANNEL_3_PIN 4
#define LED_BEACON_CHANNEL_4_PIN 5
#define LED_BEACON_CHANNEL_5_PIN 6
#define LED_BEACON_CHANNEL_6_PIN 7
#define LED_BEACON_CHANNEL_7_PIN 8
#define LED_BEACON_CHANNEL_8_PIN 9


#define NUM_BYTES_TO_SEND  4

#define CTRL_REG1 0x20
#define CTRL_REG2 0x21
#define CTRL_REG3 0x22
#define CTRL_REG4 0x23
#define CTRL_REG5 0x24
#define FIFO_CTRL_REG 0x2E

#define NUM_MILLIS_PER_GYRO_READ 100
#define Z_MAX 15424000
#define NUM_Z_TO_AVERAGE 100

unsigned long lastGyroRead = 0;

boolean thisGyroDataAvailable = false;
boolean lastGyroDataAvailable = false;

int L3G4200D_Address = 105; //I2C address of the L3G4200D




long z = 7712000;   // provide initial gyro offset of 1000000
float zAverageBias = 0.0;

float zFloat = 7712000.0;
long thisZ = 0;

long zOut = 0;


float alpha = 0.00001;
long y_sub_i = 0;
long y_sub_i_minus_1 = 0;
long x_sub_i_minus_1 = 0;




// Enter a MAC address and IP address for your controller below.
// The IP address will be dependent on your local network:
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED
};
IPAddress ip(192, 168, 0, 20);

unsigned int localPort = 8888;      // local port to listen on

// buffers for receiving and sending data
byte packetBuffer[UDP_TX_PACKET_MAX_SIZE]; // *24 BYTE* buffer to hold incoming packet,
byte ReplyBuffer[NUM_BYTES_TO_SEND];       // data to send back

// An EthernetUDP instance to let us send and receive packets over UDP
EthernetUDP Udp;



void setup() {
  
  delay(7000);
  
  // start the Ethernet and UDP:
  Ethernet.begin(mac, ip);
  Udp.begin(localPort);

  setupBeaconChannelPins();
  setupGyro();
  if (DEBUG){
      Serial.begin(115200);
      Serial.println("Debug Started");
  }
  lastGyroRead = millis();
  updateGyroValues();
  setZAverageBias();
  packReplyBuffer();
}

void loop() {
  // if there's data available, read a packet
  updateGyroValues();
  packReplyBuffer();
  int packetSize = Udp.parsePacket();
  if (packetSize)
  {
//    Serial.print("Received packet of size ");
//    Serial.println(packetSize);
//    Serial.print("From ");
//    IPAddress remote = Udp.remoteIP();
//    for (int i = 0; i < 4; i++)
//    {
//      Serial.print(remote[i], DEC);
//      if (i < 3)
//      {
//        Serial.print(".");
//      }
//    }
//    Serial.print(", port ");
//    Serial.println(Udp.remotePort());

    // read the packet into packetBufffer
    Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
//    Serial.println("Contents:");
//    Serial.println(packetBuffer);

    //Serial.print("Packet received");
    handle_message();
    
    // *** read the compass here
    //updateGyroValues();
    
    // simply echo message as an ACK
    //memcpy(ReplyBuffer, packetBuffer, 4);
    

    //memcpy(ReplyBuffer, &compassOutputHeadingAngleInTenthsOfDegree, 2); // copy the struct to the output buffer
    
    // send a reply, to the IP address and port that sent us the packet we received
    
    Udp.beginPacket(Udp.remoteIP(), Udp.remotePort());
    
    Udp.write(ReplyBuffer, NUM_BYTES_TO_SEND); // since we are using binary, need to specify size
    Udp.endPacket();
  } 
  
}

void setZAverageBias(){
 for (int i = 0; i<NUM_Z_TO_AVERAGE; i++){
   readGyroToThisZ();
   zAverageBias += (float)thisZ;
   delay(NUM_MILLIS_PER_GYRO_READ);
 }
 zAverageBias /= (float)NUM_Z_TO_AVERAGE;
}

void packReplyBuffer(){
  
  long zToSend = z;
  
  byte b0 = (byte)(zToSend % 256);
  zToSend = zToSend / 256;
  byte b1 = (byte)(zToSend % 256);
  
  zToSend = zToSend / 256;
  byte b2 = (byte)(zToSend % 256);
  
  zToSend = zToSend / 256;
  byte b3 = (byte)(zToSend % 256);
  
  
  ReplyBuffer[0] = b0;
  ReplyBuffer[1] = b1;
  ReplyBuffer[2] = b2;
  ReplyBuffer[3] = b3;
  
  /*
  ReplyBuffer[0] = (byte) z;
  ReplyBuffer[1] = (byte) z >> 8;
  ReplyBuffer[2] = (byte) z >> 16;
  ReplyBuffer[3] = (byte) z >> 24;
  */
}

int handle_message(){
  // convert back to 
  unsigned int seq_num = (packetBuffer[0]<<8) + packetBuffer[1];
  
  
  // determine message code
  switch (packetBuffer[2]){
    case 0: // set led
      byte brightness = packetBuffer[3];
      
      
      
      if (brightness != 0){
        
        //turnBeaconOn(brightness);
        turnBeaconOn((byte)brightness);
      } else {
        
        turnBeaconOff();
      }
      break;
  }
  
  // setup return message here?
  
}

// this function sets all beacon channel pins to output
void setupBeaconChannelPins(){
 pinMode( LED_BEACON_CHANNEL_1_PIN, OUTPUT);
 pinMode( LED_BEACON_CHANNEL_2_PIN, OUTPUT);
 pinMode( LED_BEACON_CHANNEL_3_PIN, OUTPUT);
 pinMode( LED_BEACON_CHANNEL_4_PIN, OUTPUT);
 pinMode( LED_BEACON_CHANNEL_5_PIN, OUTPUT);
 pinMode( LED_BEACON_CHANNEL_6_PIN, OUTPUT);
 pinMode( LED_BEACON_CHANNEL_7_PIN, OUTPUT);
 pinMode( LED_BEACON_CHANNEL_8_PIN, OUTPUT);
}


void setupGyro(){
  Wire.begin();
  setupL3G4200D(250); // Configure L3G4200  - 250, 500 or 2000 deg/sec

  delay(1500); //wait for the sensor to be ready 
}


// this function will turn on the beacon with varying levels of brightness
void turnBeaconOn(byte brightness){
  // brightness is passed in as a byte. the six least-significant bits of the
  // brightness byte each determine the state of the six beacon brightness
  // channels
  
  // NOTE: the two most-significant bits of the brightness byte should never be
  // high. Therefore, the brightness byte should always be a positive number
  byte b = brightness;
 if (b % 2 == 1){
   digitalWrite( LED_BEACON_CHANNEL_1_PIN, HIGH);
 }
 b = b / 2;
 if (b % 2 == 1){
   digitalWrite( LED_BEACON_CHANNEL_2_PIN, HIGH);
 }
 b = b / 2;
 if (b % 2 == 1){
   digitalWrite( LED_BEACON_CHANNEL_3_PIN, HIGH);
 }
 b = b / 2;
 if (b % 2 == 1){
   digitalWrite( LED_BEACON_CHANNEL_4_PIN, HIGH);
 }
 b = b / 2;
 if (b % 2 == 1){
   digitalWrite( LED_BEACON_CHANNEL_5_PIN, HIGH);
 }
 b = b / 2;
 if (b % 2 == 1){
   digitalWrite( LED_BEACON_CHANNEL_6_PIN, HIGH);
 }
 b = b / 2;
 if (b % 2 == 1){
   digitalWrite( LED_BEACON_CHANNEL_7_PIN, HIGH);
 }
 b = b / 2;
 if (b % 2 == 1){
   digitalWrite( LED_BEACON_CHANNEL_8_PIN, HIGH);
 }
}


// This function is called when the beacon is turned off.
// All signal pins controlling the beacon are set low.
void turnBeaconOff(){
  digitalWrite( LED_BEACON_CHANNEL_1_PIN, LOW);
  digitalWrite( LED_BEACON_CHANNEL_2_PIN, LOW);
  digitalWrite( LED_BEACON_CHANNEL_3_PIN, LOW);
  digitalWrite( LED_BEACON_CHANNEL_4_PIN, LOW);
  digitalWrite( LED_BEACON_CHANNEL_5_PIN, LOW);
  digitalWrite( LED_BEACON_CHANNEL_6_PIN, LOW);
  digitalWrite( LED_BEACON_CHANNEL_7_PIN, LOW);
  digitalWrite( LED_BEACON_CHANNEL_8_PIN, LOW);
}


//////////////////////////////////////////////////////////////////
// All Gyro Stuff 

void normalizeZ(){
  if (z > Z_MAX){
   z -= Z_MAX; 
  } else {
   if (z < 0){
    z += Z_MAX;
   } 
  }
}



long numToAddThisTime = 0;

void updateGyroValues(){
  
  int timeDelta = millis() - lastGyroRead;
  
  if (timeDelta >= NUM_MILLIS_PER_GYRO_READ){
      thisGyroDataAvailable = true;
  } else {
      thisGyroDataAvailable = false;
  }
  
  if (thisGyroDataAvailable){
      
      
      byte zMSB  = readRegister(L3G4200D_Address, 0x2D);
  
      byte zLSB  = readRegister(L3G4200D_Address, 0x2C);
      lastGyroRead = millis();
      
      z = ((zMSB << 8) | zLSB);
      zFloat += (float)z;
      //zFloat -= (6.8423 + 3.2290);
      zFloat -= zAverageBias;
  
      zOut = (long) zFloat;
      
      z = zOut;
      
      
      //z = nextHighPassFilter(z);
      if (DEBUG){
       Serial.println(z); 
      }
      
      
      
      
      //z -= 2;
      normalizeZ();
  }
  lastGyroDataAvailable = thisGyroDataAvailable;
}

void readGyroToThisZ(){
  byte zMSB  = readRegister(L3G4200D_Address, 0x2D);
  
  byte zLSB  = readRegister(L3G4200D_Address, 0x2C);
  thisZ = ((zMSB << 8) | zLSB);
}



long nextHighPassFilter(long x_sub_i){
  y_sub_i = (long)(alpha * (y_sub_i_minus_1 + x_sub_i - x_sub_i_minus_1));
  y_sub_i_minus_1 = y_sub_i;
  x_sub_i_minus_1 = x_sub_i;
  return y_sub_i;
}

int setupL3G4200D(int scale){
  //From  Jim Lindblom of Sparkfun's code

  // Enable x, y, z and turn off power down:
  writeRegister(L3G4200D_Address, CTRL_REG1, 0b00001111);

  // If you'd like to adjust/use the HPF, you can edit the line below to configure CTRL_REG2:
  writeRegister(L3G4200D_Address, CTRL_REG2, 0b00010000);

  // Configure CTRL_REG3 to generate data ready interrupt on INT2
  // No interrupts used on INT1, if you'd like to configure INT1
  // or INT2 otherwise, consult the datasheet:
  writeRegister(L3G4200D_Address, CTRL_REG3, 0b00001000);

  // CTRL_REG4 controls the full-scale range, among other things:

  if(scale == 250){
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00000000);
  }else if(scale == 500){
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00010000);
  }else{
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00110000);
  }

  // CTRL_REG5 controls high-pass filtering of outputs, use it
  // if you'd like:
  writeRegister(L3G4200D_Address, CTRL_REG5, 0b00000000);
}

void writeRegister(int deviceAddress, byte address, byte val) {
    Wire.beginTransmission(deviceAddress); // start transmission to device 
    Wire.write(address);       // send register address
    Wire.write(val);         // send value to write
    Wire.endTransmission();     // end transmission
}

boolean isNewZDataAvailable(){
 int zStatusReg = readRegister(L3G4200D_Address, 0x27);
 zStatusReg /= 4;
 if (zStatusReg % 2 == 1){
    return true; 
 } else {
    return false;
 }
}

int readRegister(int deviceAddress, byte address){

    int v;
    Wire.beginTransmission(deviceAddress);
    Wire.write(address); // register to read
    Wire.endTransmission();

    Wire.requestFrom(deviceAddress, 1); // read a byte

    while(!Wire.available()) {
        // waiting
    }

    v = Wire.read();
    return v;
}




