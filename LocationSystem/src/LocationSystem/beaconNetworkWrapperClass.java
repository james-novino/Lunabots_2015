/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationSystem;

import TCP.TCPClient;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class beaconNetworkWrapperClass implements beaconNetworkClient {
    
    // must use this
    private LinkedList beaconACKListeners;
    private final int minimumStepTimeInMillis = 20;
    
    // put everything for Eric's Network stuff in here
    
    // End of Eric's Network Stuff
    ///////////////////////////////////////////////////////////////////////////
    // all of Mark's Network Stuff is below
    private TCPClient client;
    private final int timeToDelayForACK = 20;
    private short compassX;
    private short compassY;
    private short compassZ;
    
    
    
    public beaconNetworkWrapperClass(String clientIP, int rxPort, int txPort){
        this.client = new TCPClient(clientIP, rxPort, txPort);
        this.beaconACKListeners = new LinkedList();
        this.compassX = 1;
        this.compassY = 1;
        this.compassZ = 1;
    }
    
    private void updateCompassInfoFromClient(){
        
    }
    
    @Override
    public void turnBeaconOn(byte level){
        if (level < (byte) 1 || level > 7){
            byte [] b = {1};
            this.client.sendObject(b);
        } else {
            byte [] b = {level};
            this.client.sendObject(b);
        }
        this.updateCompassInfoFromClient();
        this.delayMillis(Math.max(this.timeToDelayForACK, this.minimumStepTimeInMillis));
        this.alertListenersOnACKReceive(true, (short) 1);
    }
    
    @Override
    public void turnBeaconOff() {
        byte [] b = {0};
        this.client.sendObject(b);
        this.updateCompassInfoFromClient();
        this.delayMillis(timeToDelayForACK);
        this.alertListenersOnACKReceive(false, (short)1);
    }
    
    @Override
    public void alertListenersOnACKReceive(boolean BeaconOn, long headingFromGyro) {
        if (this.beaconACKListeners.isEmpty()){
            return;
        } else {
            for (Object l : this.beaconACKListeners){
                beaconACKListener bl = (beaconACKListener) l;
                bl.beaconACKReceived(BeaconOn);
                
            }
        }
    }
    
    @Override
    public void addBeaconACKListeners(beaconACKListener l){
        this.beaconACKListeners.add(l);
    }
    
    public void delayMillis(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(beaconNetworkWrapperClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    // end of Mark's Network stuff

    

    
}
