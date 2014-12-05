/*
Introduction to the SF02 laser range finder
--------------------------------------------
  The SF02 is a low cost laser range finder (LRF) that works on the time-of-flight principle. The main specifications
  are:
  
    1. Range                  40m
    2. Update rate            12 readings per second
    3. Resolution             1cm
    4. Weight                 <80g
    5. Laser classification   Class 1M
  
  The SF02 can be used in many different applications. Primary communication is via a USB connection that gives
  the user access to the main settings available on the SF02. Before connecting the Arduino we recommend that
  you take a moment to configure these settings to meet the requirements of your project.


Introduction to the SF02_Arduino_01 sketch
------------------------------------------
  This sketch demonstrates three ways of connecting the SF02 laser range finder to the Arduino. These are:
  
    1. Connecting the auxiliary UART of the SF02 to a second serial port on the Arduino.
    2. Connecting the analog output of the SF02 to one of the ADC channels of the Arduino.
    3. Connecting the alarm output of the SF02 to a digital input of the Arduino.
  
  Which method you choose to use depends on what kind of project you're working on. For highest accuracy
  the serial port connection works best. For a quick distance estimate using minimal code the analog signal is the
  easiest to use. If you just need a warning when something gets close then the alarm signal is perfect for the job.


Method #1:: Using the auxiliary UART on the SF02
-------------------------------------------------
  The SF02 has a serial port on pins 8(TXD) and 9(RXD) of the screw terminal connector.
  The baud rate of this port can be set using the USB interface through a terminal program (see the
  SF02 Manual for the instructions on how to do this). The default baud rate is 9600.
  
  The Arduino has a built-in serial port that is connected to its USB interface.
  This is available for sending and receiving data from a PC. To communicate with the SF02 a
  second serial port needs to be instantiated using the SoftwareSerial library. Once this port is
  available, distance readings from the SF02 will be transmitted whenever a "d" character is sent to the SF02
  from the Arduino.


Method #2:: Using the analog output from the SF02
-------------------------------------------------
  The SF02 has an analog output on pin 7(0V-3.3V) of the screw terminal connector. The voltage on
  this output is updated whenever a new distance measurement is made. The Arduino can read the
  voltage at any time using one its analog input pins.


Method #3:: Using the alarm output from the SF02
------------------------------------------------
  The SF02 has an alarm signal output on pin 6 (0V/3.3V) of the screw terminal connector. This output changes state
  when the distance falls below the preset value define in the settings menu of the SF02. The Arduino can
  read this state at any time using one of its digital input pins.


Wiring
------
  Pin 10 is RXD on the Arduino and connects to terminal 8 on the SF02.
  Pin 11 is TXD on the Arduino and connects to terminal 9 on the SF02.
  Pin A0 is an ADC on the Arduino and connects to terminal 7 on the SF02.
  Pin 12 is a digital input on the Arduino and connects to terminal 6 on the SF02.
  GND on the Arduino connects to terminal 10 on the SF02.


The program
-----------
  This program reads the auxiliary UART, the analog output and the alarm of the SF02 every 0.5 seconds.
  It prints the result on the Arduino terminal using the USB port. The results are also stored as variables
  that can be accessed by other parts of the program. The results are displayed by the terminal as follows:

    Serial = [raw data] <distance in meters> :: Analog = [raw voltage] <distance in meters> :: Alarm = [raw bit] <alarm state>

  Before using this program please make sure that the settings in the SF02 match the definitions given below.
*/

// -- Include files --
#include <SoftwareSerial.h>                                             // This library is needed to create the second serial port on the Arduino

//-- System definitions --
#define terminal_baud_rate                115200                        // This baud rate should match the terminal application settings

//-- Arduino pin definitions --
#define serial_port_rxd_pin               10                            // These pin definitions can be changed to match your preferred Arduino connections
#define serial_port_txd_pin               11
#define analog_input_pin                  0
#define alarm_input_pin                   12

//-- SF02 settings definitions --
#define sf02_select_the_uart_baud_rate    9600                         // These SF02 settings must be changed to match the values entered into the SF02
#define sf02_set_the_0_0V_distance        0.0
#define sf02_set_the_3_3V_distance        33.00

//-- Arduino pin assignments --
SoftwareSerial sf02_serial(serial_port_rxd_pin, serial_port_txd_pin);  // Define the pins to be used for the second serial port
int sf02_analog_pin = analog_input_pin;                                // Define the pin to be used for the analog input of the ADC
int sf02_alarm_pin = alarm_input_pin;                                  // Define the pin to be used for the alarm input

//-- Variables --
float dist_meters_serial;                                              // This is the distance read through the second serial port
float dist_meters_analog;                                              // This is the distance calculated from the analog measurement
float analog_voltage;                                                  // The raw analog value
int alarm;                                                             // The alarm variable

char sf02_string[16], c;                                               // These are used to fetch the ASCII string from the SF02 using the second serial port
int i, analog;                                                         // Miscellaneous variables
float slope;

//-- The setup function --
void setup()                                          
{
  Serial.begin(terminal_baud_rate);                                     // Open the main USB serial port on the Arduino ready for the terminal application
  while (!Serial);                                                      // Wait for serial port to connect.
  sf02_serial.begin(sf02_select_the_uart_baud_rate);                    // Open the second serial port to connect to the SF02
  pinMode(sf02_alarm_pin, INPUT);                                       // Prepare the digital input pin for the SF02 alarm

  slope = (sf02_set_the_3_3V_distance-sf02_set_the_0_0V_distance)/3.3;  // The slope value is used later to convert the analog voltage into a distance
  Serial.println("\r\nSF02 interface test 01\r\n");                     // Print a test message to the Arduino terminal
}

//-- The main program runs forever --
void loop()
{  
  delay(500);                                                           // This is a 0.5 second delay to set the update rate at approximately two readings per second
  
//-- Read the auxiliary UART of the SF02 using the second serial port of the Arduino --
  sf02_serial.write("d");                                               // Trigger the auxiliary UART on the SF02 and...
  while (!sf02_serial.available());                                     // Wait until the next distance measurement is ready
  
                                                                        // Prepare to read the serial port...
  i=0;                                                                  // i is an indexer for the string storage variable and...
  c=0;                                                                  // c holds the latest ASCII character from the SF02
  
  while(c != 10)                                                        // Read the ASCII string from the SF02 until a line feed character (\n) is detected
  {
    while (!sf02_serial.available());                                   // Wait here for the next character
    c = sf02_serial.read();                                             // Fetch the character and store it in c
    sf02_string[i] = c;                                                 // Add the character to the existing string from the SF02
    i++;                                                                // Point to the next character storage location in the string
  }                                                                     // Once the string has been captured...
  sf02_string[i-2] = 0;                                                 // Create a null terminated string and remove the \r\n characters from the end 
  dist_meters_serial = atof(sf02_string);                               // Convert the ASCII string from the SF02 into a floating point number

//-- Read the anaolg output of the SF02 using an ADC input of the Arduino --
  analog = analogRead(sf02_analog_pin);                                // Read the ADC value of the analog input pin
  analog_voltage = analog * 0.0049;                                    // Convert this into a voltage 
  dist_meters_analog=analog_voltage*slope+sf02_set_the_0_0V_distance;  // Convert the voltage into a distance using the SF02 settings
  
//-- Read the alarm output of the SF02 --
  alarm = digitalRead(sf02_alarm_pin);                                 // Read the SF02 alarm
    
//-- Display the results on the terminal --
  Serial.print("Serial = [");                                          // Print the serial port results
  Serial.print(sf02_string);
  Serial.print("]  ");
  Serial.print(dist_meters_serial, 2);
  Serial.print("m  ::  Analog = [");                                   // Print the analog results
  Serial.print(analog_voltage);
  Serial.print("V]  ");  
  Serial.print(dist_meters_analog, 2);
  Serial.print("m  :: Alarm = [");                                     // Print the alarm status
  Serial.print(alarm);
  Serial.print("] ");
  if(alarm == 0)
    Serial.print("OFF\r\n");
  else
    Serial.print("ON\r\n");    
}
//-- End of program --
