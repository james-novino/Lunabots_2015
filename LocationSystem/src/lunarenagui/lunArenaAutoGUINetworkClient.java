/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lunarenagui;

/**
 *
 * @author chalbers2
 */
public interface lunArenaAutoGUINetworkClient {
    public void addLunArenaListener(autonomousSystemListener l);
    //private void alertListenersOnDataReceive();
        // This function must call the updateDataFromAutonomousSystem() method for the autonomousSystemListeners
}
