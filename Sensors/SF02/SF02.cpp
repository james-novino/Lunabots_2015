/*
	SF02.h - Library for communicating with the Lightware SF02 laser module.
	Created by Keith Kirton, Lightware Optoelectronics, April 15, 2014.
	Released into the public domain.
*/
#include "Arduino.h"
#include "SF02.h"

//******************************************************************************
//* Constructors
//******************************************************************************

SF02::SF02()
:	_auxUartBaudRate(9600),
	_00vDistance(0.0f),
	_33vDistance(33.0f),
	_slope(10.0f),
	_analogInputPin(-1),
	_alarmInputPin(-1),
	_serialSF02(0),
	_initialised(false),
	_bufferPos(0),
	_requestDistance(true),
	_lastResult(0.0f)
{
	
}

//******************************************************************************
//* Public Methods
//******************************************************************************

/* begin method using the default hardware serial communication interface. */
void SF02::begin()
{
	_serialSF02 = &Serial;
	_initialised = true;
}

/* begin method for overriding the default serial communication interface. */
void SF02::begin(Stream &SerialInterface)
{
	_serialSF02 = &SerialInterface;
	_initialised = true;
}

void SF02::end()
{
	
}

/* returns the baud rate to use for communication with the auxiliary uart. Default is 9600. */
/* Ensure this matches the setting in the SF02 module if used to setup a serial interface. */
long SF02::getAuxUartBaudRate()
{
	return _auxUartBaudRate;
}

/* sets the baud rate to use for communication with the auxiliary uart. Default is 9600. */
/* Ensure this matches the setting in the SF02 module if using the getter to setup a serial interface. */
void SF02::setAuxUartBaudRate(long BaudRate)
{
	switch (BaudRate)
	{
		case 4800:
		case 9600:
		case 19200:
		case 38400:
		case 57600:
		case 115200:
			_auxUartBaudRate = BaudRate;
			break;
		default:
			_auxUartBaudRate = 9600;
			break;
	}
}

/* returns the 0.0V distance used for analog output. */
float SF02::get00vDistance()
{
	return _00vDistance;
}

/* sets the 0.0V distance used for analog output. */
void SF02::set00vDistance(float Distance)
{
	_00vDistance = Distance;
	_slope = (_33vDistance - _00vDistance) / 3.3f;
}

/* returns the 3.3V distance used for analog output. */
float SF02::get33vDistance()
{
	return _33vDistance;
}

/* sets the 3.3V distance used for analog output. */
void SF02::set33vDistance(float Distance)
{
	_33vDistance = Distance;
	_slope = (_33vDistance - _00vDistance) / 3.3f;
}

/* returns the Arduino analog pin that will receive the analog signal. Default is -1 and will be ignored. */
int SF02::getAnalogInputPin()
{
	return _analogInputPin;
}

/* set the Arduino analog pin that should receive the analog signal. */
void SF02::setAnalogInputPin(int Pin)
{
	_analogInputPin = Pin;
}

/* returns the Arduino pin that will receive the alarm signal. Default is -1 and will be ignored. */
int SF02::getAlarmInputPin()
{
	return _alarmInputPin;
}

/* set the Arduino pin that should receive the alarm signal. */
void SF02::setAlarmInputPin(int Pin)
{
	_alarmInputPin = Pin;
	pinMode(_alarmInputPin, INPUT);
}

/* returns the distance in meters, using the serial interface, currently being read by the SF02 module. */
float SF02::getDistance(int Timeout)
{
	if (!_initialised)
		return -1.0f;

	_serialSF02->write("d");
	
	int i = 0;
	char c = 0;
	char buf[16];

	unsigned long startMillis = millis();
	
	while (c != 10)
	{
		if (millis() - startMillis >= Timeout)
		{
			return -1.0f;
		}
		
		if (_serialSF02->available()) 
		{
			c = _serialSF02->read();
			buf[i] = c;
			++i;
		}
	}
	
	buf[i-2] = 0;

	return atof(buf);
}

/* returns the distance in meters, using the analog signal, currently being read by the SF02 module. */
float SF02::getAnalogDistance()
{
	if (_analogInputPin == -1)
		return -1.0f;
		
	int analog = analogRead(_analogInputPin);
	float analogVoltage = (float)analog * 0.0049f;
	
	return analogVoltage * _slope + _00vDistance;
}

/* returns the alarm output currently signalled on pin _alarmInputPin. */
int SF02::getAlarmOutput()
{
	if (_alarmInputPin == -1)
		return -1;
		
	return digitalRead(_alarmInputPin);
}

/* non-blocking method to retreive the distance reading via the serial interface from the SF02 module. */
bool SF02::updateResult()
{
	char c = 0;
	
	if (_requestDistance == true)
	{
		_serialSF02->write("d");
		_requestDistance = false;
	}
		
	if (_serialSF02->available())
	{
		c = _serialSF02->read();
		_buffer[_bufferPos++] = c;
		
		if (c == 10)
		{
			_buffer[_bufferPos - 2] = 0;
			_lastResult = atof(_buffer);
			_requestDistance = true;
			return true;
		}
	}
	
	return false;
}

/* returns last result obtained through the non-blocking update method. */
float SF02::getResult()
{
	return _lastResult;
}