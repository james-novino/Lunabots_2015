/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package locationsystem_v1;

import LocationSystem.LED_BeaconMasterController;
import java.util.logging.Level;
import java.util.logging.Logger;
import lunarenagui.swing_2d_lunarena.lunArenaSwingGUI;
import udp_location_transmit.UDPLocationTransmitter;

/**
 *
 * @author chalbers2
 */
public class LocationSystem_v1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        boolean displayGUIs = true;
        
        String serverIP = "192.168.0.20"; // IP of beacon server
        int UDPPort = 8888;
        
        String initialHeading = "East";
        
        
        
        
        LED_BeaconMasterController L = new LED_BeaconMasterController(serverIP, UDPPort, displayGUIs, initialHeading);
        // transmit the location data to the ALMV at 192.168.0.10 on port 1111
        UDPLocationTransmitter locationTransmitter = new UDPLocationTransmitter("192.168.0.10", 1111);
        L.addLocationSystemListener(locationTransmitter);
        
        if (displayGUIs){
            lunArenaSwingGUI gui = new lunArenaSwingGUI();
            gui.setVisible(true);
            locationSystemToArenaGUIClass converter = new locationSystemToArenaGUIClass(gui);
            L.addLocationSystemListener(converter);
        }
        
        /*
        while (!displayGUIs){
            try {
                int x = L.getXPositionInArenaInCentimeters();
                int y = L.getYPositionInArenaInCentimeters();
                
                System.out.println("X: " + x + ", Y: " + y + ", T/F: " + L.isBeaconLocated());
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(LocationSystem_v1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        */
        
        while (true){
            // delay 1 second
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LocationSystem_v1.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (L.shouldLocationSystemReset()){
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LocationSystem_v1.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                float headingAngle = L.getHeadingAngleInRadians();
                
                L = null;
                locationTransmitter = null;
                
                L = new LED_BeaconMasterController(serverIP, UDPPort, displayGUIs, "East");
                // transmit the location data to the ALMV at 192.168.0.10 on port 1111
                locationTransmitter = new UDPLocationTransmitter("192.168.0.10", 1111);
                L.addLocationSystemListener(locationTransmitter);
                
                L.addToHeadingOffsetAngleInRadians(headingAngle);

                if (displayGUIs){
                    lunArenaSwingGUI gui = new lunArenaSwingGUI();
                    gui.setVisible(true);
                    locationSystemToArenaGUIClass converter = new locationSystemToArenaGUIClass(gui);
                    L.addLocationSystemListener(converter);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LocationSystem_v1.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
        
        
        
        
    }
}
