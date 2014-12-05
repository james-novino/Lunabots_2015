/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manual_control_gui;

/**
 *
 * @author chalbers2
 */
public interface countdownListener {
    public void updateCountdownInSeconds(int currentCountdownInSeconds, String currentCountdownString);
    public void countdownTimerFinished();
}
