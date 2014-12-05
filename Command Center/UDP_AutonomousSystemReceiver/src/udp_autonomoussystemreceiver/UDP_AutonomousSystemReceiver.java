/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_autonomoussystemreceiver;

import lunarenagui.swing_2d_lunarena.lunArenaSwingGUI;
import udp_autonomous_receive.UDPAutonomousReceiver;

/**
 *
 * @author chalbers2
 */
public class UDP_AutonomousSystemReceiver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        UDPAutonomousReceiver rx = new UDPAutonomousReceiver("192.168.0.10", 2222);
        lunArenaSwingGUI GUI = new lunArenaSwingGUI();
        GUI.setVisible(true);
        rx.addAutonomousSystemListeners(GUI);
        
    }
}
