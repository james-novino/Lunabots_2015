/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_location_transmit;

import LocationSystem.locationSystemListener;
import java.io.*;
import java.net.*;
import struct.JavaStruct;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
import struct.StructException;


public class UDPLocationTransmitter implements locationSystemListener {
    private final boolean DEBUG = false;
    
    // the address and port
    private int SOCKET_TIMEOUT = 100; // ms
    private int port = 0;
    private InetAddress address = null;
    
    
    // rx and tx buffers
    private byte[] txbfr = new byte[1024];
    
    
    // refs to rx and tx packets
    private DatagramPacket txpacket;
    
    // recv address and recv port
    InetAddress raddr = null;
    int rport = 0;
    
    // the socket with which to listen/send on
    DatagramSocket ssocket;
    
    // data structures representing send and data
    // used to encode/decode data, resp.
    private TxLocationStruct f = new TxLocationStruct();
    
    
    public UDPLocationTransmitter(String serverIPAddress, int port){
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
            this.ssocket = new DatagramSocket();
            this.ssocket.setReuseAddress(true);
            this.ssocket.setSoTimeout(SOCKET_TIMEOUT);   // set the timeout in millisecounds.
        } catch (SocketException e) {
            // print errors on stderr
            System.err.print("Error when creating the socket: ");
            System.err.println(e);
            System.exit(1); // throw error
        }
    }
    
    public void sendLocationData(
            short xPosition, 
            short yPosition, 
            float headingAngle){
        f.xPositionInCentimeters = xPosition;
        f.yPositionInCentimeters = yPosition;
        f.headingAngleInRadians = headingAngle;
        
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
        
        if (DEBUG){
            System.out.println("End of Send Data Method");
        }
        
    }

    @Override
    public void updateXYPositionOfLunabotInCentimeters(int xPositionInCentimeters, int yPositionInCentimeters, float headingAngleInRadians) {
        this.sendLocationData((short)xPositionInCentimeters, (short)yPositionInCentimeters, headingAngleInRadians);
    }
    
    
}
