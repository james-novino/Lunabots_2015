/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_manualcontrol_transmit;

import struct.*;

/**
 *
 * @author chalbers2
 */
@StructClass
public class TxManualControlStruct {
    @StructField(order=0)
    public boolean runManualControl;
    
    @StructField(order=1)
    public boolean issueAutonomousStartSignal;
    
    @StructField(order=2)
    public boolean runAutonomousSystem;
    
    @StructField(order=3)
    public boolean issueAutonomousStopSignal;
    
    @StructField(order=4)
    public boolean issueAutonomousResetSignal;
    
    @StructField(order=5)
    public int countdownTimeInSeconds;
    
    @StructField(order=6)
    public float leftMotorSpeed;
    
    @StructField(order=7)
    public float rightMotorSpeed;
    
    @StructField(order=8)
    public float collectionSystemSpeed;
    
    @StructField(order=9)
    public float dumpSystemSpeed;
    
    @StructField(order=10)
    public float driveMotorMaxSpeed;
    
    @StructField(order=11)
    public float collectionSystemAutonomousSpeed;
    
    @StructField(order=12)
    public float dumpSystemAutonomousSpeed;
}
