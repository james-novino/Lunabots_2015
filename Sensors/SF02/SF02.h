/*
	SF02.h - Library for communicating with the Lightware SF02 laser module.
	Created by Keith Kirton, Lightware Optoelectronics, April 15, 2014.
	Released into the public domain.
*/
#ifndef SF02_H
#define SF02_H

#include "Arduino.h"

class SF02
{
public:
	SF02();

/* Arduino constructors */
	void begin();
	void begin(Stream &SerialInterface);
	void end();
	
/* SF02 measurement property getters */
	long getAuxUartBaudRate();
	float get00vDistance();
	float get33vDistance();
	
/* SF02 measurement property setters */
	void setAuxUartBaudRate(long BaudRate);
	void set00vDistance(float Distance);
	void set33vDistance(float Distance);
	
/* Arduino input pin getters */
	int getAnalogInputPin();
	int getAlarmInputPin();
	
/* Arduino input pin setters */
	void setAlarmInputPin(int Pin);
	void setAnalogInputPin(int Pin);

/* Query functions */
	float getDistance(int Timeout = 100);
	float getAnalogDistance();
	int getAlarmOutput();
	
	bool updateResult();
	float getResult();

private:
/* SF02 measurement properties */
	long _auxUartBaudRate;
	float _00vDistance;
	float _33vDistance;
	float _slope;
	
/* Arduino input pins */
	int _analogInputPin;
	int _alarmInputPin;

/* Serial comms object */
	Stream *_serialSF02;

/* System variables */
	bool _initialised;
	
	char _buffer[16];
	int _bufferPos;
	
	bool _requestDistance;
	float _lastResult;
};

#endif // SF02_H