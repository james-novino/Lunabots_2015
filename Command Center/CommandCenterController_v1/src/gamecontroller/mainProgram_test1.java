/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontroller;

import manual_control_gui.manualControlGUI;
import udp_manualcontrol_transmit.UDPManualControlTransmitter;

/**
 *
 * @author chalbers2
 */
public class mainProgram_test1 {
    
    public static void main (String args[]){
        GameController g = new GameController();
        UDPManualControlTransmitter udpTx = new UDPManualControlTransmitter(
                "192.168.0.10", 1050);
        g.setUDPTransmitter(udpTx);
        manualControlGUI gui = new manualControlGUI();
        gui.setVisible(true);
        g.setManualControlGUI(gui);
    }
    
}
