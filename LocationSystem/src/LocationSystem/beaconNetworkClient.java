/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationSystem;

public interface beaconNetworkClient {
    public void turnBeaconOn(byte level);
    public void turnBeaconOff();
    public void addBeaconACKListeners(beaconACKListener l);
    public void alertListenersOnACKReceive(boolean BeaconOn, 
            long headingFromGyro);
}
