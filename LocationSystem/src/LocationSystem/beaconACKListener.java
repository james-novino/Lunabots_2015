/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationSystem;

public interface beaconACKListener {
    public void beaconACKReceived(boolean LEDBeaconOn);
    public void updateCompassInformation(long headingFromGyro);
}
