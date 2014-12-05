/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationSystem;

/**
 *
 * @author chalbers2
 */
public interface beaconACKListener {
    public void beaconACKReceived(boolean LEDBeaconOn);
    public void updateCompassInformation(long headingFromGyro);
}
