/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiiMoteArduinoInterface;

import TCP.TCPClient;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;
import wiiMoteParticleFilter.IRBlob;
import wiiMoteParticleFilter.singleWiiMoteParticleFilter;
import wiiMoteParticleFilter.wiiMoteListener;


public class rocketBrandArduinoUno {
    
    private static final boolean DEBUG = false;
    
    public final int minMillisPerDataRead = 10;
    
    private long lastDataReadMillis;
    
    private IRBlob [] blobs;
    private boolean LED_on;
    private arduinoReadByte arduino;
    
    private LinkedList<wiiMoteListener> Listeners;
    
    
    private singleWiiMoteParticleFilter particleFilter;
    private int ifNorthScanner_1_ifSouthScanner2;
    
    public rocketBrandArduinoUno(String serialPort, int baudRate){
        
        this.arduino = new arduinoReadByte(serialPort, baudRate);
        
        this.blobs = new IRBlob[4];
        for (int i = 0; i<4; i++){
            this.blobs[i] = new IRBlob();
        }
        this.LED_on = false;
        
        this.Listeners = new LinkedList();
        this.ifNorthScanner_1_ifSouthScanner2 = 0;
        this.lastDataReadMillis = 0;
    }
    
    public arduinoReadByte getArduinoReadByte(){
        return this.arduino;
    }
    
    public void setSingleWiiMoteParticleFilter(singleWiiMoteParticleFilter pf){
        this.particleFilter = pf;
    }
    
    public singleWiiMoteParticleFilter getSingleWiiMoteParticleFilter(){
        return this.particleFilter;
    }
    
    public void setLED_Beacon_On(boolean LED_BeaconOn){
        this.LED_on = LED_BeaconOn;
    }
    
    public boolean getLED_BeaconOn(){
        return this.LED_on;
    }
    
    public boolean areThereNullPointers(){
        if (this.blobs == null){
            return true;
        }
        if (this.arduino == null){
            return true;
        }
        if (this.particleFilter == null){
            return true;
        }
        if (this.Listeners == null){
            return true;
        }
        
        return false;
    }
    
    
    
    
    
    public void addListener(wiiMoteListener w){
        this.Listeners.add(w);
    }
    
    public void updateListeners(){
        if (this.Listeners.isEmpty()){
            return;
        } else {
            for (wiiMoteListener l : Listeners){
                if (l != null){
                    l.blobUpdate(this.blobs, this.LED_on);
                }
            }
        }
    }
    
    public boolean isNorthScanner(){
        if (this.ifNorthScanner_1_ifSouthScanner2 ==1){
            return true;
        }else {
            return false;
        }
    }
    
    public boolean isSouthScanner(){
        if (this.ifNorthScanner_1_ifSouthScanner2 == 2){
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isScannerNorthSouthKnown(){
        if (this.ifNorthScanner_1_ifSouthScanner2 == 1){
            return true;
        } else if (this.ifNorthScanner_1_ifSouthScanner2 == 2){
            return true;
        } else {
            return false;
        }
    }
    
    public void getDataFromWiiMote(boolean LED_BeaconOn){
        if (!this.areThereNullPointers()){
            this.LED_on = LED_BeaconOn;
            long timeSinceLastRead = (System.currentTimeMillis() - this.lastDataReadMillis);
            if (timeSinceLastRead < this.minMillisPerDataRead){
                try {
                    Thread.sleep(this.minMillisPerDataRead - timeSinceLastRead);
                } catch (InterruptedException ex) {
                    Logger.getLogger(rocketBrandArduinoUno.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            // this is where info is written to the Arduino.
            byte b1 = (byte)this.particleFilter.getNextServoArgument();
            this.arduino.write(b1);
            if (this.LED_on){
                b1 = (byte)255;
                this.arduino.write(b1);
            } else {
                b1 = (byte)0;
                this.arduino.write(b1);
            }
            
            
            
            while (this.arduino.numReceivedBytes() < 25+9){
                // wait for data to come in
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(rocketBrandArduinoUno.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            while (this.arduino.numReceivedBytes() >= 25+9){
                if (this.arduino.getNextReceivedByte() == (byte)87){ // W
                    if (this.arduino.getNextReceivedByte() == (byte)105){  // i
                        if (this.arduino.getNextReceivedByte() == (byte)105){  // i
                            if (this.arduino.getNextReceivedByte() == (byte)68){  // D
                                if (this.arduino.getNextReceivedByte() == (byte)97){  // a
                                    if (this.arduino.getNextReceivedByte() == (byte)116){  // t
                                        if (this.arduino.getNextReceivedByte() == (byte)97){  // a
                                            byte rx = this.arduino.getNextReceivedByte();  // this receives the north / south byte determination
                                            if (rx == (byte)78){
                                                this.ifNorthScanner_1_ifSouthScanner2 = 1;
                                            } else if (rx == (byte) 83){
                                                this.ifNorthScanner_1_ifSouthScanner2 = 2;
                                            } else {
                                                this.ifNorthScanner_1_ifSouthScanner2 = 0;
                                            }
                                            for (int i = 0; i<4; i++){
                                                int xPos = ByteToInteger(this.arduino.getNextReceivedByte());
                                                xPos += 256*ByteToInteger(this.arduino.getNextReceivedByte());

                                                int yPos = ByteToInteger(this.arduino.getNextReceivedByte());
                                                yPos += 256*ByteToInteger(this.arduino.getNextReceivedByte());

                                                int size = ByteToInteger(this.arduino.getNextReceivedByte());
                                                size += 256*ByteToInteger(this.arduino.getNextReceivedByte());

                                                this.blobs[i].setSize(size);
                                                this.blobs[i].setXPosition(xPos);
                                                this.blobs[i].setYPosition(yPos);
                                                this.blobs[i].setnumBlob(i);

                                                if (DEBUG){
                                                    System.out.println(this.blobs[i]);
                                                }

                                            }


                                            byte b = this.arduino.getNextReceivedByte();
                                            
                                            if (b == (byte)0){
                                                this.LED_on = false;
                                            } else {
                                                this.LED_on = true;
                                            }
                                            this.updateListeners();
                                        } else {
                                            this.clearSerialPort();
                                        }
                                    } else {
                                        this.clearSerialPort();
                                    }
                                } else {
                                    this.clearSerialPort();
                                }
                            } else {
                                this.clearSerialPort();
                            }
                        } else {
                            this.clearSerialPort();
                        }
                    } else {
                        this.clearSerialPort();
                    }
                } else {
                    this.clearSerialPort();
                }  
            } 
        }
        this.lastDataReadMillis = System.currentTimeMillis();
    }
    
    private void clearSerialPort(){
        try {
            this.arduino.getSerialPort().purgePort(jssc.SerialPort.PURGE_RXCLEAR);
        } catch (SerialPortException ex) {
            Logger.getLogger(rocketBrandArduinoUno.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.arduino.getSerialPort().purgePort(jssc.SerialPort.PURGE_TXCLEAR);
        } catch (SerialPortException ex) {
            Logger.getLogger(rocketBrandArduinoUno.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    public static int ByteToInteger(byte x) {
        int a = 0;

        for (byte i = 0; i < 8; i++) {
            a |= (x & (1 << i));
        }

        return a;
    }
    
}
