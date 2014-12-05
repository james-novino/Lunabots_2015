/*
 * @author Eric Schisselbauer
 * 2014-3-8 
 *
 * Code for setting the beacon on/off on the detection RPi
 * This should be refactored into a class (eg like another thread) and
     integrated with the detection system
 */

package udpbeaconcontrol;

import LocationSystem.beaconACKListener;
import java.io.*;
import java.net.*;
import struct.JavaStruct;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
import struct.StructException;


public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        final int SOCKET_TIMEOUT = 100; // ms
//        final int PORT = 8888;
//        InetAddress ADDR = null;
//        try{
//            ADDR = InetAddress.getByName("192.168.2.2");
//        } catch (UnknownHostException e){
//            System.out.println("Error: Unknown address" + e);
//        }
        
//        byte[] taddr = {192,168,2,2};
//        ADDR = InetAddress.getByAddress(taddr);
        
        
        myListener l = new myListener();
        // instantiate a beacon controller (this class blocks until the ACK comes back)
        UDPBeaconControl bCtrl = new UDPBeaconControl("192.168.0.2", 8888);
        // add the listener
        bCtrl.addBeaconACKListeners((beaconACKListener) l);
        
        while (true){
            bCtrl.turnBeaconOff();
            System.out.println("");
            try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            
            bCtrl.turnBeaconOn((byte)7);
            
            try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            System.out.println("");
        }
    }
    
}



//////////

                
                
                
                
                
                
