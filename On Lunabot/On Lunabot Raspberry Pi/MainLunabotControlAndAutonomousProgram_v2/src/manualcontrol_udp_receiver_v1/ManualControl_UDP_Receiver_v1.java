/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manualcontrol_udp_receiver_v1;

import autonomousSystem.autonomousSystem;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;
import mainController.mainController;
import serialComm.arduinoReadByte;
import udp_autonomous_transmit.UDPAutonomousTransmitter;
import udp_location_receive.UDPLocationReceiver;
import udp_manualcontrol_receive.UDPManualControlReceiver;

/**
 *
 * @author chalbers2
 */
public class ManualControl_UDP_Receiver_v1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        UDPManualControlReceiver ManualControlRx = new UDPManualControlReceiver(1050);
        UDPLocationReceiver locationSystemRx = new UDPLocationReceiver(1111);
        UDPAutonomousTransmitter UDPAutonomousTransmitter = new UDPAutonomousTransmitter("192.168.0.14", 2222);
        autonomousSystem autonomousSystem = new autonomousSystem();
        autonomousSystem.addAutonomousSystemListener(UDPAutonomousTransmitter);
        locationSystemRx.addLocationSystemListener(autonomousSystem);
        arduinoReadByte serialPort = null;
        
        try {
            serialPort = new arduinoReadByte("/dev/ttyAMA0", 9600);
        } catch (SerialPortException ex) {
            Logger.getLogger(ManualControl_UDP_Receiver_v1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mainController controller = new mainController();
        controller.setAutonomousSystem(autonomousSystem);
        
        controller.setArduinoReadByte(serialPort);
        ManualControlRx.addManualControlListener(controller);
        
    }
}
