

// Sensor Board Sketch for Arduino DUE in Temple University 2014 Lunabot

#define NUM_SENSORS 5

byte outputByte = (byte)0;


int lowAndHighValuesForEachSensor[5][2] =
{ {200, 700},  // IR Sensor 0
  {200, 700},   // IR Sensor 1
  {200, 700},   // IR Sensor 2
  {200, 700},   // IR Sensor 3
  {200, 700}
};   // Hall Effect Sensor

boolean raiseBeaconPoleBool = false;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(7, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(5, OUTPUT);
  
  
  digitalWrite(7, LOW);
  digitalWrite(6, LOW);
  digitalWrite(5, LOW);
}

void loop() {
  // put your main code here, to run repeatedly:
  readIncomingBeaconPoleData();
  actuateIncomingBeaconPoleData();
}


void readSensorData() {
  int twoPow = 1;
  outputByte = (byte) 0;
  for (int i = 0; i < 8; i++) {
    if (i < NUM_SENSORS) {
      // this is within the range of valid sensors
      if (obstacleAtGivenSensor(i)) {
        outputByte += (byte) twoPow;
      } // end of if
    } // end of if
    twoPow *= 2;
  } // end of for loop
} // end of readSensorData Method

void transmitSensorData() {
  Serial.write(outputByte);
}  // end of transmitSensorData Method


// This function reads information off of the serial line and determines whether
// the beacon pole should be raised or lowered
void readIncomingBeaconPoleData() {
  if (Serial.available()) {
    if (Serial.read() == (byte)1) {
      // we should lower the beacon pole
      raiseBeaconPoleBool = true;
    } else {
      // we should raise the beacon pole
      raiseBeaconPoleBool = false;
    } // end of if - else
  } // end of if
} // end of readIncomingBeaconPoleData Method

void actuateIncomingBeaconPoleData() {
  if (raiseBeaconPoleBool){
    raiseBeaconPole();
  } else {
    lowerBeaconPole();
  } // end of if - else
} // end of actuateIncomingBeaconPoleData method

void raiseBeaconPole() {
    digitalWrite(7, LOW);
    digitalWrite(6, LOW);  // high to go down
    digitalWrite(5, HIGH);   // high to go up
} // end of raiseBeaconPole Method

void lowerBeaconPole() {
    digitalWrite(7, LOW);
    digitalWrite(6, HIGH);  // high to go down
    digitalWrite(5, LOW);   // high to go up
} // end of lowerBeaconPole method



boolean obstacleAtGivenSensor(int sensorNum) {
  int value = analogRead(sensorNum);
  if (value < lowAndHighValuesForEachSensor[sensorNum][0] ||
      value > lowAndHighValuesForEachSensor[sensorNum][1]) {
    return true;
  } else {
    return false;
  } // end of if - else
}// end of obstacleAtGivenSensor method
