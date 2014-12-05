/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_autonomous_receive;

import java.io.*;
import java.net.*;
import struct.JavaStruct;
import java.sql.Time;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lunarenagui.autonomousSystemListener;
import struct.StructException;

/**
 *
 * @author chalbers2
 */
public class UDPAutonomousReceiver implements Runnable {
    private final boolean DEBUG = true;
    
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
    private RxAutonomousStruct rstruct = new RxAutonomousStruct();
    
    // Thread for receiving data
    private Thread rxThread;
    private boolean shouldThreadRun = true;
    private long RxThreadSleepTime = 1; // 10 ms
    
    private boolean isReceiverConnected = false;
    
    private LinkedList<autonomousSystemListener> autonomousSystemListeners = new LinkedList(); 
    
    public UDPAutonomousReceiver(String serverIPAddress, int port){
        //SOCKET_TIMEOUT = 100; // ms
        // set the port
        this.port = port;
        
        // set the address
        try{
            this.address = InetAddress.getByName(serverIPAddress);
        } catch (UnknownHostException e){
            System.out.println("Error: Unknown address" + e);
        }
        
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
    
    public void addAutonomousSystemListeners(autonomousSystemListener l){
        this.autonomousSystemListeners.add(l);
    }
    
    private void alertListenersOnAutonomousInfoReceive(
            short xPositionInCentimeters, 
            short yPositionInCentimeters,
            float headingAngleInRadians,
            String currentAutonomousSystemState){
        if (DEBUG){
            System.out.println(
                    "X: " + xPositionInCentimeters 
                    + " Y: " + yPositionInCentimeters + 
                    " Heading Angle: " + headingAngleInRadians + 
                    "\n String: " + currentAutonomousSystemState);
        }
        for (autonomousSystemListener l:this.autonomousSystemListeners){
            l.updateDataFromAutonomousSystem(
                    xPositionInCentimeters, 
                    yPositionInCentimeters, 
                    new LinkedList(), 
                    new LinkedList(), 
                    headingAngleInRadians, 
                    currentAutonomousSystemState);
        }
    }

    @Override
    public void run() {
        if (DEBUG){
            System.out.println("Thread Started");
        }
        while(this.shouldThreadRun){
            rxpacket = new DatagramPacket(rxbfr, rxbfr.length);
            // recieve the ack.  If ack not received within timeout, keep sending until it is

                try {
                    this.isReceiverConnected = true;
                    ssocket.receive(rxpacket);
                    raddr = rxpacket.getAddress();
                    rport = rxpacket.getPort();

                } catch (SocketTimeoutException e){
                    // resend the packet
                    this.isReceiverConnected = false;
                    System.out.println("ACK not received within timeout period.");

                } catch (IOException e){
                    System.out.println("There was an error when receiving the packet: " + e);
                }
            
            
            // decode the received data
            try {
                // generate TX data
                rstruct = new RxAutonomousStruct();
                if (this.isReceiverConnected){
                    JavaStruct.unpack(rstruct, rxbfr);
                } else {
                    rstruct = null;
                }
                
            } catch (StructException ex) {
                System.out.println("Darn, struct error!!" + ex);
                System.exit(2);
    //                    Logger.getLogger(Udp_beacon_control.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (rstruct != null){
                String AutoSysStateString = new String(rstruct.currentAutoSysStateCharArray);
                this.alertListenersOnAutonomousInfoReceive(
                        rstruct.xPositionInCentimeters, 
                        rstruct.yPositionInCentimeters, 
                        rstruct.headingAngleInRadians, 
                        AutoSysStateString);
            }
            try {
                Thread.sleep(RxThreadSleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(UDPAutonomousReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
}
