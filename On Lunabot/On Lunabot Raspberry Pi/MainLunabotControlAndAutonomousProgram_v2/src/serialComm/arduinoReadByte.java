/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serialComm;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortEventListener;
import java.util.Arrays;
import jssc.SerialPortEvent;

/**
 *
 * @author chalbers2
 */
public class arduinoReadByte implements SerialPortEventListener  {
    
    private SerialPort sp;
    private serialPortExitOnCloseThread closeThread;
    
    public arduinoReadByte(String portName, int baudRate) throws SerialPortException{
        sp = new SerialPort(portName);
        this.open();
        sp.setParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        this.closeThread = new serialPortExitOnCloseThread(this.sp);
        this.closeThread.setArduinoReadByte(this);
        
    }
    
    public byte[] read() throws SerialPortException{
        return this.sp.readBytes();
    }
    
    public void write(byte b) throws SerialPortException{
        this.sp.writeByte(b);
    }
    
    public void write(byte [] b) throws SerialPortException{
        this.sp.writeBytes(b);
    }
    
    
    
    private void open() throws SerialPortException{
        this.sp.openPort();
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Serial Data Received");
        if (event.isRXCHAR()){
            try {
                System.out.println("Serial Data Received");
            } catch (Exception e){
                System.out.println(e);
            }
        }
    }

    
    
    
    
}
