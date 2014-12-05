/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiiMoteArduinoInterface;

import LocationSystem.LED_BeaconMasterController;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortEventListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortEvent;


public class arduinoReadByte implements SerialPortEventListener  {
    
    
    private static final boolean DEBUG = false;
    private SerialPort sp;
    private serialPortExitOnCloseThread closeThread;
    private LinkedList receivedBytes;
    
    private LED_BeaconMasterController masterController;
    
    private String serialPortName;
    private int    baudRate;
    
    private int numNullBytes = 0;
    
    public arduinoReadByte(String portName, int baudRate){
        this.numNullBytes = 0;
        this.serialPortName = portName;
        this.baudRate = baudRate;
        sp = new SerialPort(this.serialPortName);
        
        this.receivedBytes = new LinkedList();
        try {
            this.open();
        } catch (SerialPortException ex) {
            Logger.getLogger(arduinoReadByte.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sp.purgePort(jssc.SerialPort.PURGE_RXCLEAR);
        } catch (SerialPortException ex) {
            Logger.getLogger(arduinoReadByte.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sp.purgePort(jssc.SerialPort.PURGE_TXCLEAR);
        } catch (SerialPortException ex) {
            Logger.getLogger(arduinoReadByte.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sp.setParams(this.baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (SerialPortException ex) {
            Logger.getLogger(arduinoReadByte.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.closeThread = new serialPortExitOnCloseThread(this.sp);
        try {
            sp.addEventListener(this);
        } catch (SerialPortException ex) {
            Logger.getLogger(arduinoReadByte.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }
    
    public void setLED_BeaconMasterController(LED_BeaconMasterController mc){
        this.masterController = mc;
    }
    
    public SerialPort getSerialPort(){
        return this.sp;
    }
    
    public int numReceivedBytes(){
        return this.receivedBytes.size();
    }
    
    public byte getNextReceivedByte(){
        Object byteObj = this.receivedBytes.poll();
        while (byteObj == null){
            this.numNullBytes++;
            System.out.println("Null Byte");
            
            if (this.numNullBytes >= 100){
            
                try {
                    try {
                        this.sp.closePort();
                    } catch (SerialPortException ex) {
                        Logger.getLogger(arduinoReadByte.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(arduinoReadByte.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (this.masterController != null){
                    this.masterController.resetLocationSystem();
                }
            }
            
            
        }
        Byte b = (Byte) byteObj;
        byte retVal = b.byteValue();
        this.numNullBytes = 0;
        return retVal;
    }
    
    public byte[] read() throws SerialPortException{
        return this.sp.readBytes();
    }
    
    public boolean write(byte b){
        try {
            this.sp.writeByte(b);
        } catch (SerialPortException ex) {
            return false;
            //Logger.getLogger(arduinoReadByte.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
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
        if (DEBUG){
            System.out.println("Serial Data Received");
        }
        if (event.isRXCHAR()){
            try {
                byte [] r = this.read();
                for (int i = 0; i< r.length; i++){
                    Byte b = new Byte(r[i]);
                    this.receivedBytes.add(b);
                }
                if (DEBUG){
                    System.out.println("Serial Data Received" + Arrays.toString(r));
                }
            } catch (Exception e){
                System.out.println(e);
            }
        }
    }
    
    
    

    
    
    
    
}
