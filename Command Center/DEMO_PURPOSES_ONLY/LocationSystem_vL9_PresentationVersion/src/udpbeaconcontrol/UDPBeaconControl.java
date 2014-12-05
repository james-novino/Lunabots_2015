/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package udpbeaconcontrol;

import LocationSystem.beaconACKListener;
import LocationSystem.beaconNetworkClient;
import java.io.*;
import java.net.*;
import struct.JavaStruct;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
import struct.StructException;

import java.util.LinkedList;

/**
 *
 * @author Eric Schisselbauer
 */
public class UDPBeaconControl implements beaconNetworkClient{
    
    private long delayTimeInMillisForLEDRiseTime = 20;
    
    // up to 10 listeners
    private LinkedList<beaconACKListener> listeners = new LinkedList();

    // the address and port
    private int SOCKET_TIMEOUT = 100; // ms
    private int port = 0;
    private InetAddress address = null;
    
    // amount of time to wait for beacon to fully respond (UNCOMMENT IN CODE)
    private int BEACON_RESP_TIME = 2; // ms
    
    // rx and tx buffers
    private byte[] txbfr = new byte[1024];
    private byte[] rxbfr = new byte[1024];
    
    // refs to rx and tx packets
    private DatagramPacket rxpacket;
    private DatagramPacket txpacket;

    // send packet data
    private int seq = 0;
    private byte code = 0;
    private byte brightness = 0;
    
    // recv packet data
    int rseq = 0;
    int rcode = 0;
    int rbrightness = 0;
    
    //private int compassX, compassY, compassZ;
    
    // recv address and recv port
    InetAddress raddr = null;
    int rport = 0;
    
    // the socket with which to listen/send on
    DatagramSocket ssocket;
    
    // data structures representing send and data
    // used to encode/decode data, resp.
    private TxStruct f = new TxStruct();
    private RxStruct rstruct = null;
    
    //RxStruct rxs = new RxStruct();
    
    // uses a default address, port if none specified
    public UDPBeaconControl(){
        //SOCKET_TIMEOUT = 100; // ms
        // set the port
        this.port = 8888;
        
        // set the address
        try{
            this.address = InetAddress.getByName("192.168.2.2"); // RPi
        } catch (UnknownHostException e){
            System.out.println("Error: Unknown address" + e);
        }
        
        // actual initialization code
        try {
            this.ssocket = new DatagramSocket(this.port);
            this.ssocket.setSoTimeout(SOCKET_TIMEOUT);   // set the timeout in millisecounds.
        } catch (SocketException e) {
            // print errors on stderr
            System.err.print("Error when creating the socket: ");
            System.err.println(e);
            System.exit(1); // throw error
        }
    }
    
    public UDPBeaconControl(String serverIPAddress, int port){
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
    }
    
    private void sendData(byte brightness){
        
        rxpacket = new DatagramPacket(rxbfr, rxbfr.length);        
        
        seq++;
//        if (brightness != 0){
//            brightness = 0;
//        } else {
//            brightness = 7;
//        }

        f.seq = (short)seq;
        f.type = code;
        f.brightness = brightness;

        // pack TX data
        try {
            txbfr = JavaStruct.pack(f);
        } catch (StructException ex) {
            System.err.println("Darn, struct error!!" + ex);
            System.exit(2);
//                    Logger.getLogger(Udp_beacon_control.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        txpacket = new DatagramPacket(txbfr, txbfr.length, address, port);
        
        // send
        try {
            // assume we don't need to flush the buffer after sending
            ssocket.send(txpacket);
        } catch (IOException e){
            System.err.println("There was an error when sending the packet");
            System.exit(3);
        }
        
        
        
        // recieve the ack.  If ack not received within timeout, keep sending until it is
        while (true){
            try {
                ssocket.receive(rxpacket);
                raddr = rxpacket.getAddress();
                rport = rxpacket.getPort();
                break; // break out of the loop if successful
            } catch (SocketTimeoutException e){
                // resend the packet
                System.out.println("ACK not received within timeout period, resending");
                try {
                    // assume we don't need to flush the buffer after sending
                    ssocket.send(txpacket);
                } catch (IOException e2){
                    System.out.println("There was an error when sending the packet: " + e2);
                    System.exit(3);
                }
            } catch (IOException e){
                System.out.println("There was an error when receiving the packet: " + e);
            }
        }
        
        
        
        // decode the received data
        
            // generate TX data
            rstruct = new RxStruct();
            rstruct.compassHeadingFromGyro = (long)(int)(((0xFF & rxbfr[0])) | 
                    ((0xFF & rxbfr[1])<<8) | 
                    ((0xFF & rxbfr[2])<<16) |
                    ((0xFF & rxbfr[3])<<24));
            //System.out.println("" + rstruct.compassHeadingFromGyro);
            
            //JavaStruct.unpack(rstruct, rxbfr);
            /*
        } catch (StructException ex) {
            System.out.println("Darn, struct error!!" + ex);
            System.exit(2);
//                    Logger.getLogger(Udp_beacon_control.class.getName()).log(Level.SEVERE, null, ex);
        }
        * */
        
            // wait for beacon to be fully on/off
    //        try {
    //            Thread.sleep(BEACON_RESP_TIME);
    //        } catch(InterruptedException ex) {
    //            Thread.currentThread().interrupt();
    //        }
            
            // everything is decoded, etc, alert listeners
        try {    
            Thread.sleep(delayTimeInMillisForLEDRiseTime);
        } catch (InterruptedException ex) {
            
        }
        
        if (brightness == (byte)0){
            alertListenersOnACKReceive(false, rstruct.compassHeadingFromGyro);  // off
        } else {
            alertListenersOnACKReceive(true,  rstruct.compassHeadingFromGyro); // on
        }
        
        

        //System.out.println("[UDP] Received ACK " + rstruct.seq + " with code " + rstruct.type + " and data " + rstruct.brightness + " from " + raddr + ":" + rport);
        //System.out.println("[UDP] DEBUG: SAMPLE (wait here until light is completely on/off, then sample)");
    }
    
    @Override
    public void turnBeaconOn(byte level) {
        sendData(level);
    }

    @Override
    public void turnBeaconOff() {
        // turn the beacon off
        sendData((byte)0);
    }

    

    @Override
    public void addBeaconACKListeners(LocationSystem.beaconACKListener l) {
        listeners.add((beaconACKListener) l);
    }

    @Override
    public void alertListenersOnACKReceive(boolean BeaconOn, long headingFromGyro) {
        
        for (beaconACKListener l: listeners){
            l.beaconACKReceived(BeaconOn);
            l.updateCompassInformation(headingFromGyro);
        }
    }
}