/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chalbers2
 */
public class countdownTimer implements Runnable {
    public final static boolean DEBUG = false;
    
    private int numSecondsInCountdown = 70;
    private int currentCountdownSeconds = 70;
    private boolean runCountdown = false;
    private boolean isCountdownCompleted = false;
    private boolean isCountdownReadyToStart = false;
    
    private Thread timerThread;
    private boolean shouldTimerThreadRun = true;
    
    private LinkedList<countdownListener> countdownListeners = new LinkedList();
    
    public countdownTimer(int numSecondsInCountdown){
        this.numSecondsInCountdown = numSecondsInCountdown;
        this.timerThread = new Thread(this);
        this.shouldTimerThreadRun = true;
        this.resetCountdown();
        this.timerThread.start();
    }
    
    public void addCountdownListener(countdownListener l){
        this.countdownListeners.add(l);
    }
    
    public void resetCountdown(){
        this.runCountdown = false;
        this.isCountdownCompleted = false;
        this.currentCountdownSeconds = this.numSecondsInCountdown;
        this.isCountdownReadyToStart = true;
        
    }
    
    public void setNumSecondsInCountdown(int numSeconds){
        this.numSecondsInCountdown = numSeconds;
        this.resetCountdown();
    }
    
    public void stopMainCountdownTimerThread(){
        this.shouldTimerThreadRun = false;
    }
    
    public void startCountdown(){
        this.runCountdown = true;
    }
    
    public void stopCountdown(){
        this.runCountdown = false;
        
    }
    
    
    
    public int getCurrentNumSecondsInCountdown(){
        return this.currentCountdownSeconds;
    }
    
    public String getMinuteColonSecondCountdownString(){
        String retval = "";
        
        int minutes = this.currentCountdownSeconds/60;
        
        retval += "" + minutes + ":";
        
        int seconds = this.currentCountdownSeconds%60;
        
        if (seconds < 10){
            retval += "0" + seconds + "";
        } else {
            retval += "" + seconds + "";
        }
        
        return retval;
    }

    @Override
    public void run() {
        while(this.shouldTimerThreadRun){
            if (this.runCountdown){
                this.currentCountdownSeconds--;
                
                if (DEBUG){
                    System.out.println("Current Countdown: " + this.getMinuteColonSecondCountdownString());
                }
                if (this.currentCountdownSeconds == 0){
                    
                    this.runCountdown = false;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(countdownTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
