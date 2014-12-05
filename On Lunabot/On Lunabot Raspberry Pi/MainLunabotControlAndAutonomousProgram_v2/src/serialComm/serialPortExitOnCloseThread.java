/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serialComm;

import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author chalbers2
 */
public class serialPortExitOnCloseThread implements Runnable {
    
    private arduinoReadByte ard;
    private SerialPort sp;
    private Thread t;
    
    public serialPortExitOnCloseThread(SerialPort sp){
        this.sp = sp;
        t = new Thread(this);
        Runtime.getRuntime().addShutdownHook(t);
    }
    
    public void setArduinoReadByte(arduinoReadByte ard){
        this.ard = ard;
    }
    
    public Thread getThread(){
        return this.t;
    }

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        byte[] stopMotors = new byte[16];
        stopMotors[0] = (byte)128;
        stopMotors[1] = (byte)0;
        stopMotors[2] = (byte)0;
        stopMotors[3] = 
                (byte) ((byte)(stopMotors[0] + stopMotors[1] + stopMotors[2]) & ((byte)127));
        stopMotors[4] = (byte)128;
        stopMotors[5] = (byte)4;
        stopMotors[6] = (byte)0;
        stopMotors[7] = 
                (byte) ((byte)(stopMotors[4] + stopMotors[5] + stopMotors[6]) & ((byte)127));
        stopMotors[8] = (byte)135;
        stopMotors[9] = (byte)0;
        stopMotors[10] = (byte)0;
        stopMotors[11] = 
                (byte) ((byte)(stopMotors[8] + stopMotors[9] + stopMotors[10]) & ((byte)127));
        stopMotors[12] = (byte)135;
        stopMotors[13] = (byte)4;
        stopMotors[14] = (byte)0;
        stopMotors[15] = 
                (byte) ((byte)(stopMotors[12] + stopMotors[13] + stopMotors[14]) & ((byte)127));
        try {
            this.ard.write(stopMotors);
        } catch (SerialPortException ex) {
            Logger.getLogger(serialPortExitOnCloseThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            this.sp.closePort();
            System.out.println("The port has been closed");
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
}
