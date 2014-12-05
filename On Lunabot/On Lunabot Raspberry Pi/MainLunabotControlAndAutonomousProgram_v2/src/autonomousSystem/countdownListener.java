/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

/**
 *
 * @author chalbers2
 */
public interface countdownListener {
    
    /**
     * This method is called with an integer number of seconds left in the countdown-to-start sequence
     * @param numSecondsLeftInCountdown - number of seconds left in the countdown sequence
     */
    public void updateCountdownInSeconds(int numSecondsLeftInCountdown);
    
    /**
     * This method should be called in order to issue a "start" signal to the autonomous system
     */
    public void issueStartSignal();
    
    
    
    /**
     * This method should be called in order to issue a "stop" signal to the autonomous system.
     * The stop signal should be issued in the event of either an autonomous system abort, or the end of a competition round
     */
    public void issueStopSignal();
    
    
    
    /**
     * This method should be called after a *SIMULATED* competition attempt is competed.
     * Calling this method will drive the robot forward towards the starting area while 
     * sending the collection system back into the "safe to drive" state.
     */
    public void resetForNextRun();
    
    
    
}
