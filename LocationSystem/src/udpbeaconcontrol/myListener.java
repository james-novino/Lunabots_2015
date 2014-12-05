/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package udpbeaconcontrol;

import LocationSystem.beaconACKListener;


public class myListener implements beaconACKListener{

    @Override
    public void beaconACKReceived(boolean LEDBeaconOn) {
        if (LEDBeaconOn){
            System.out.println("[myListener: ACK] Beacon on!");
        } else {
            System.out.println("[myListener: ACK] Beacon off!");
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCompassInformation(long headingFromGyro) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
