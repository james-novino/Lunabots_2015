/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_manualcontrol_receive;

/**
 *
 * @author chalbers2
 */
public interface manualControlListener {
    public void setNewManutalControlReceivedInfo(boolean runManualControl,
            boolean issueAutonomousStartSignal,
            boolean runAutonomousSystem,
            boolean issueAutonomousStopSignal,
            boolean issueAutonomousResetSignal,
            int     countdownTimeInSeconds,
            float   leftMotorSpeed,
            float   rightMotorSpeed,
            float   collectionSystemSpeed,
            float   dumpSystemSpeed,
            float   driveMotorMaxSpeed,
            float   collectionSystemAutonomousSpeed,
            float   dumpSystemAutonomousSpeed);
    
    public void stopMotors();
}
