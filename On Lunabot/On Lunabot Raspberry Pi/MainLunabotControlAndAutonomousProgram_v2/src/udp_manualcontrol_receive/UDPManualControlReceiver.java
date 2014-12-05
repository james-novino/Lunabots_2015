/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_manualcontrol_receive;

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
public class UDPManualControlReceiver implements Runnable{
    private final boolean DEBUG = false;
    
    // listeners
    private LinkedList<manualControlListener> listeners = new LinkedList();
    
    // the address and port
    private int SOCKET_TIMEOUT = 1000; // ms
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
    private RxManualControlStruct rstruct = new RxManualControlStruct();
    
    // Thread for receiving data
    private Thread rxThread;
    private boolean shouldThreadRun = true;
    private long RxThreadSleepTime = 1; // 10 ms
    private boolean isReceiverConnected = false;
    
    public UDPManualControlReceiver(int port) {
        //SOCKET_TIMEOUT = 100; // ms
        // set the port
        this.port = port;
        
        // set the address
        
        
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
    
    
    @Override
    public void run() {
        
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
                    System.out.println("Command Center packet not received within timeout period.");
                    this.isReceiverConnected = false;
                    this.alertListenersToStopMotors();

                } catch (IOException e){
                    System.out.println("There was an error when receiving the packet: " + e);
                    this.alertListenersToStopMotors();
                }
            
            
            // decode the received data
            try {
                // generate TX data
                rstruct = new RxManualControlStruct();
                JavaStruct.unpack(rstruct, rxbfr);
            } catch (StructException ex) {
                System.out.println("Darn, struct error!!" + ex);
                this.alertListenersToStopMotors();
                System.exit(2);
    //                    Logger.getLogger(Udp_beacon_control.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (this.isReceiverConnected){
                this.alertListenersOnManualControlInfoReceive(
                        rstruct.runManualControl,
                        rstruct.issueAutonomousStartSignal,
                        rstruct.runAutonomousSystem,
                        rstruct.issueAutonomousStopSignal,
                        rstruct.issueAutonomousResetSignal,
                        rstruct.countdownTimeInSeconds,
                        rstruct.leftMotorSpeed,
                        rstruct.rightMotorSpeed,
                        rstruct.collectionSystemSpeed,
                        rstruct.dumpSystemSpeed,
                        rstruct.driveMotorMaxSpeed,
                        rstruct.collectionSystemAutonomousSpeed,
                        rstruct.dumpSystemAutonomousSpeed);
            }
            try {
                Thread.sleep(RxThreadSleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(UDPManualControlReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    public void addManualControlListener(manualControlListener l){
        this.listeners.add(l);
    }
    
    private void alertListenersToStopMotors(){
        for (manualControlListener l: listeners){
            l.stopMotors();
        }
    }
    
    private void alertListenersOnManualControlInfoReceive(
            boolean runManualControl,
            boolean issueAutonomousStartSignal,
            boolean runAutonomousSystem,
            boolean issueAutonomousStopSignal,
            boolean issueAutonomousResetSignal,
            int     countdownTimeInSeconds,
            float   leftMotorSpeed,
            float   rightMotorSpeed,
            float   collectionSystemSpeed,
            float   dumpSystemSpeed, 
            float   driveMotorMaxSpeed,
            float   collectionSystemAutonomousSpeed,
            float   dumpSystemAutonomousSpeed){
        for (manualControlListener l: listeners){
            l.setNewManutalControlReceivedInfo(
                    runManualControl, 
                    issueAutonomousStartSignal, 
                    runAutonomousSystem, 
                    issueAutonomousStopSignal, 
                    issueAutonomousResetSignal, 
                    countdownTimeInSeconds, 
                    leftMotorSpeed, 
                    rightMotorSpeed, 
                    collectionSystemSpeed, 
                    dumpSystemSpeed,
                    Math.abs(driveMotorMaxSpeed),
                    collectionSystemAutonomousSpeed,
                    dumpSystemAutonomousSpeed);
        }
        if (DEBUG){
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("runManualControl: " + runManualControl); 
            System.out.println("issueStartSignal: " + issueAutonomousStartSignal); 
            System.out.println("runAutonomousSystem: " + runAutonomousSystem);
            System.out.println("issueAutonomousStopSignal: " + issueAutonomousStopSignal);
            System.out.println("issueAutonomousResetSignal: " + issueAutonomousResetSignal);
            System.out.println("countdownTimeInSeconds: " + countdownTimeInSeconds);
            System.out.println("Left Motor Speed: " + leftMotorSpeed); 
            System.out.println("Right Motor Speed: " + rightMotorSpeed);
            System.out.println("collectionSystemSpeed: " + collectionSystemSpeed);
            System.out.println("dumpSystemSpeed: " + dumpSystemSpeed);
            System.out.println("driveMotorMaxSpeed: " + driveMotorMaxSpeed);
            System.out.println("collectionSystemAutonomousSpeed: " + collectionSystemAutonomousSpeed);
            System.out.println("dumpSystemAutonomousSpeed: " + dumpSystemAutonomousSpeed);
        }
    }
}
