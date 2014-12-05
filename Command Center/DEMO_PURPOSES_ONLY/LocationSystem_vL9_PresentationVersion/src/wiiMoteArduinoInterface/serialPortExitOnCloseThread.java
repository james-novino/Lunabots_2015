/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiiMoteArduinoInterface;

import jssc.SerialPort;

/**
 *
 * @author chalbers2
 */
public class serialPortExitOnCloseThread implements Runnable {
    
    private SerialPort sp;
    private Thread t;
    
    public serialPortExitOnCloseThread(SerialPort sp){
        this.sp = sp;
        t = new Thread(this);
        Runtime.getRuntime().addShutdownHook(t);
    }
    
    public Thread getThread(){
        return this.t;
    }

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        try {
            
            
            
            this.sp.closePort();
            System.out.println("The port has been closed");
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
}
