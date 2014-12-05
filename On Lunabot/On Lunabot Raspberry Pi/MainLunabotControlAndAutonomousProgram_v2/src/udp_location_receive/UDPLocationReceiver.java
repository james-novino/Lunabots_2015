/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_location_receive;

import java.io.*;
import java.net.*;
import struct.JavaStruct;
import java.sql.Time;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import struct.StructException;

/**
 *
 * @author chalbers2
 */
public class UDPLocationReceiver implements Runnable {
    private final boolean DEBUG = false;
    
    // the address and port
    private int SOCKET_TIMEOUT = 100; // ms
    private int port = 0;
    private InetAddress address = null;
    
    
    // rx and tx buffers
    private byte[] rxbfr = new byte[1024];
    
    
    // refs to rx and tx packets
    private DatagramPacket rxpacket;
    
    // recv address and recv port
    InetAddress raddr = null;
    int rport = 0;
    
    // the socket with which to listen/send on
    DatagramSocket ssocket;
    
    // data structures representing send and data
    // used to encode/decode data, resp.
    private RxLocationStruct rstruct = new RxLocationStruct();
    
    // Thread for receiving data
    private Thread rxThread;
    private boolean shouldThreadRun = true;
    private long RxThreadSleepTime = 1; // 10 ms
    
    private LinkedList<locationSystemListener> locationSystemListeners = new LinkedList();
    
    private boolean isConnectionEstablishedWithLocationSystem = false;
    
    public UDPLocationReceiver( int port){
        //SOCKET_TIMEOUT = 100; // ms
        // set the port
        this.port = port;
        
        // actual initialization code
        try {
            this.ssocket = new DatagramSocket(this.port);
            this.ssocket.setReuseAddress(true);
            this.ssocket.setSoTimeout(SOCKET_TIMEOUT);   // set the timeout in millisecounds.
        } catch (SocketException e) {
            // print errors on stderr
            System.err.print("Error when creating the socket: ");
            System.err.println(e);
            System.exit(1); // throw error
        }
        this.shouldThreadRun = true;
        rxThread = new Thread(this);
        rxThread.start();
    }
    
    public void addLocationSystemListener(locationSystemListener l){
        this.locationSystemListeners.add(l);
    }
    
    private void alertListenersOnLocationReceive(
            short xPositionInCentimeters, 
            short yPositionInCentimeters,
            float headingAngleInRadians){
        if (DEBUG){
            System.out.println(
                    "X: " + xPositionInCentimeters 
                    + " Y: " + yPositionInCentimeters + 
                    " Heading Angle: " + headingAngleInRadians);
        }
        for (locationSystemListener l: this.locationSystemListeners){
            l.updateXYPositionOfLunabotInCentimeters(
                    xPositionInCentimeters, 
                    yPositionInCentimeters, 
                    headingAngleInRadians);
        }
    }

    @Override
    public void run() {
        if (DEBUG){
            System.out.println("Thread Started");
        }
        while(this.shouldThreadRun){
            rxpacket = new DatagramPacket(rxbfr, rxbfr.length);
            this.isConnectionEstablishedWithLocationSystem = true;
            // recieve the ack.  If ack not received within timeout, keep sending until it is

                try {
                    ssocket.receive(rxpacket);
                    raddr = rxpacket.getAddress();
                    rport = rxpacket.getPort();

                } catch (SocketTimeoutException e){
                    // resend the packet
                    System.out.println("Data from location system not received within timeout period.");
                    this.isConnectionEstablishedWithLocationSystem = false;
                } catch (IOException e){
                    System.out.println("There was an error when receiving the packet: " + e);
                    this.isConnectionEstablishedWithLocationSystem = false;
                }
            
            
            // decode the received data
            try {
                // generate TX data
                rstruct = new RxLocationStruct();
                JavaStruct.unpack(rstruct, rxbfr);
            } catch (StructException ex) {
                System.out.println("Darn, struct error!!" + ex);
                this.isConnectionEstablishedWithLocationSystem = false;
                System.exit(2);
    //                    Logger.getLogger(Udp_beacon_control.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (this.isConnectionEstablishedWithLocationSystem){
                this.alertListenersOnLocationReceive(
                        rstruct.xPositionInCentimeters, 
                        rstruct.yPositionInCentimeters, 
                        rstruct.headingAngleInRadians);
            } else {
                this.alertListenersOnLocationReceive((short)-1, (short)-1, 0f);
            }
            try {
                Thread.sleep(RxThreadSleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(UDPLocationReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
}
