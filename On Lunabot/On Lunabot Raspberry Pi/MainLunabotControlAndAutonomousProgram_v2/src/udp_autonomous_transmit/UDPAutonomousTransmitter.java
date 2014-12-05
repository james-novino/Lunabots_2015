/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_autonomous_transmit;

import java.io.*;
import java.net.*;
import struct.JavaStruct;
import java.sql.Time;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import networkTransmitToGUI.autonomousSystemListener;
import struct.StructException;

/**
 *
 * @author chalbers2
 */
public class UDPAutonomousTransmitter implements autonomousSystemListener {
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
    private TxAutonomousStruct f = new TxAutonomousStruct();
    
    
    public UDPAutonomousTransmitter(String serverIPAddress, int port){
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
    
    public void sendAutonomousSystemData(
            short xPosition, 
            short yPosition, 
            float headingAngle,
            LinkedList arenaObstacleGrid,
            LinkedList RobotPath,
            String currentAutonomousSystemState){
        f.xPositionInCentimeters = xPosition;
        f.yPositionInCentimeters = yPosition;
        f.headingAngleInRadians = headingAngle;
        
        if (RobotPath != null){
            int robotPathSize = RobotPath.size();
            if (robotPathSize > 0){
                f.RobotPathArrayX = new short[robotPathSize];
                f.RobotPathArrayY = new short[robotPathSize];
                int i = 0;
                for (Object robotPathObj: RobotPath){
                    short[] thisPathElement = (short []) robotPathObj;
                    f.RobotPathArrayX[i] = thisPathElement[0];
                    f.RobotPathArrayY[i] = thisPathElement[1];
                    i++;
                }
            } else {
                f.RobotPathArrayX = new short[1];
                f.RobotPathArrayX[0] = -10;  // -10 signifies no robotPath
                f.RobotPathArrayY = new short[1];
                f.RobotPathArrayY[0] = -10;  // -10 signifies no robotPath
            }
        } else {
            f.RobotPathArrayX = new short[1];
            f.RobotPathArrayX[0] = -10;  // -10 signifies no robotPath
            f.RobotPathArrayY = new short[1];
            f.RobotPathArrayY[0] = -10;  // -10 signifies no robotPath
        }
        
        if (arenaObstacleGrid != null){
            int obstGridListSize = arenaObstacleGrid.size();
            int j = 0;
            if (obstGridListSize > 100){
                f.arenaObstacleGridArray0 = new short[100];
                f.arenaObstacleGridArray1 = new short[100];
                f.arenaObstacleGridArray2 = new short[100];
                f.arenaObstacleGridArray3 = new short[100];
                for (j = 0; j<100; j++){
                    Object arenaGridObj = arenaObstacleGrid.poll();
                    if (arenaGridObj != null){
                        short[] thisGridElement = (short []) arenaGridObj;
                        f.arenaObstacleGridArray0[j] = thisGridElement[0];
                        f.arenaObstacleGridArray1[j] = thisGridElement[1];
                        f.arenaObstacleGridArray2[j] = thisGridElement[2];
                        f.arenaObstacleGridArray3[j] = thisGridElement[3];
                    }
                }
            } else {
                if (obstGridListSize > 0){
                    f.arenaObstacleGridArray0 = new short[obstGridListSize];
                    f.arenaObstacleGridArray1 = new short[obstGridListSize];
                    f.arenaObstacleGridArray2 = new short[obstGridListSize];
                    f.arenaObstacleGridArray3 = new short[obstGridListSize];
                    j = 0;
                    for (Object arenaGridObj: arenaObstacleGrid){
                        short[] thisGridElement = (short []) arenaGridObj;
                        f.arenaObstacleGridArray0[j] = thisGridElement[0];
                        f.arenaObstacleGridArray1[j] = thisGridElement[1];
                        f.arenaObstacleGridArray2[j] = thisGridElement[2];
                        f.arenaObstacleGridArray3[j] = thisGridElement[3];
                        j++;
                    }
                } else {
                    f.arenaObstacleGridArray0 = new short[1];
                    f.arenaObstacleGridArray0[0] = -10;  // -10 signifies no arenaObstGrid
                    f.arenaObstacleGridArray1 = new short[1];
                    f.arenaObstacleGridArray1[0] = -10;  // -10 signifies no arenaObstGrid
                    f.arenaObstacleGridArray2 = new short[1];
                    f.arenaObstacleGridArray2[0] = -10;  // -10 signifies no arenaObstGrid
                    f.arenaObstacleGridArray3 = new short[1];
                    f.arenaObstacleGridArray3[0] = -10;  // -10 signifies no arenaObstGrid 
                }
            }
        } else {
            f.arenaObstacleGridArray0 = new short[1];
            f.arenaObstacleGridArray0[0] = -10;  // -10 signifies no arenaObstGrid
            f.arenaObstacleGridArray1 = new short[1];
            f.arenaObstacleGridArray1[0] = -10;  // -10 signifies no arenaObstGrid
            f.arenaObstacleGridArray2 = new short[1];
            f.arenaObstacleGridArray2[0] = -10;  // -10 signifies no arenaObstGrid
            f.arenaObstacleGridArray3 = new short[1];
            f.arenaObstacleGridArray3[0] = -10;  // -10 signifies no arenaObstGrid
        }
        
        if (currentAutonomousSystemState != null){
            f.currentAutoSysStateCharArray = currentAutonomousSystemState.toCharArray();
        } else {
            String nullString = "Null String";
            f.currentAutoSysStateCharArray = nullString.toCharArray();
        }
        
        f.obstLength0 = f.arenaObstacleGridArray0.length;
        f.obstLength1 = f.arenaObstacleGridArray1.length;
        f.obstLength2 = f.arenaObstacleGridArray2.length;
        f.obstLength3 = f.arenaObstacleGridArray3.length;
        
        f.robotPathLengthX = f.RobotPathArrayX.length;
        f.robotPathLengthY = f.RobotPathArrayY.length;
        
        f.currentAutonomousSystemStateStringLength = 
                f.currentAutoSysStateCharArray.length;
        
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
    public void updateDataFromAutonomousSystem(
            short lunabotPositionInCentimeters_X, 
            short lunabotPositionInCentimeters_Y, 
            LinkedList arenaObstacleGrid, 
            LinkedList RobotPath, 
            float headingAngleInRadians, 
            String currentAutonomousSystemState) {
        this.sendAutonomousSystemData(
                lunabotPositionInCentimeters_X, 
                lunabotPositionInCentimeters_Y, 
                headingAngleInRadians, 
                arenaObstacleGrid, 
                RobotPath, 
                currentAutonomousSystemState);
    }
    
    
}
