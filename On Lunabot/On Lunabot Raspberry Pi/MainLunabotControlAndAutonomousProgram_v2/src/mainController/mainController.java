/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainController;

import autonomousSystem.autonomousSystem;
import java.util.Arrays;

import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;
import serialComm.arduinoReadByte;
import udp_manualcontrol_receive.manualControlListener;

/**
 *
 * @author chalbers2
 */
public class mainController implements manualControlListener {
    
    private final boolean DEBUG = false;
    
    private final byte mainDriveMotorControllerAddress = (byte) 128;
    private final byte collectionAndDumpMotorControllerAddress = (byte) 135;
    
    private autonomousSystem autonomousSystem;
    
    private arduinoReadByte serialPort;
    private float leftMotorSpeed = 0f;
    private float rightMotorSpeed = 0f;
    private float driveMotorMaxSpeed = 0f;
    private float collectionSystemSpeed = 0f;
    private float dumpSystemSpeed = 0f;
    private byte[] byteArrayToWrite;
    
    public mainController(){
        
    }
    
    public void setAutonomousSystem(autonomousSystem autoSys){
        this.autonomousSystem = autoSys;
    }
    
    public autonomousSystem getAutonomousSystem(){
        return this.autonomousSystem;
    }
    
    public void setArduinoReadByte(arduinoReadByte sp){
        this.serialPort = sp;
    }
    
    private void reComputeByteArrayToWriteToMotorControllers(){
        // re initialize the byte array
        this.byteArrayToWrite = new byte[16];
        
        // set the address for the main drive motor controller
        this.byteArrayToWrite[0] = this.mainDriveMotorControllerAddress;
        
        // first command byte
        if (this.leftMotorSpeed < 0f){
            // travel in reverse
            this.byteArrayToWrite[1] = (byte)1;
            // write the data byte
            this.byteArrayToWrite[2] = (byte)Math.round(-127f * this.leftMotorSpeed);
        } else {
            // travel forward
            this.byteArrayToWrite[1] = (byte)0;
            // write the data byte
            this.byteArrayToWrite[2] = (byte)Math.round(127f * this.leftMotorSpeed);
        }
        
        // write the checksum
        this.byteArrayToWrite[3] = 
                (byte) ((byte)(this.byteArrayToWrite[0] + 
                        this.byteArrayToWrite[1] + 
                        this.byteArrayToWrite[2]) & ((byte)127));
        
        this.byteArrayToWrite[4] = this.mainDriveMotorControllerAddress;
        
        if (this.rightMotorSpeed < 0f){
            // drive in reverse
            this.byteArrayToWrite[5] = (byte)5;
            // write the data byte
            this.byteArrayToWrite[6] = (byte)Math.round(-127f * this.rightMotorSpeed);
        } else {
            // drive forward
            this.byteArrayToWrite[5] = (byte)4;
            // write the data byte
            this.byteArrayToWrite[6] = (byte)Math.round(127f * this.rightMotorSpeed);
        }
        
        // write the checksum
        this.byteArrayToWrite[7] = 
                (byte) ((byte)(this.byteArrayToWrite[4] + 
                        this.byteArrayToWrite[5] + 
                        this.byteArrayToWrite[6]) & ((byte)127));
        
        this.byteArrayToWrite[8] = this.collectionAndDumpMotorControllerAddress;
        
        if (this.collectionSystemSpeed < 0f){
            // reverse the collection system
            this.byteArrayToWrite[9] = (byte)1;
            this.byteArrayToWrite[10] = (byte)Math.round(-127f * this.collectionSystemSpeed);
        } else {
            // drive collection system forward
            this.byteArrayToWrite[9] = (byte)0;
            this.byteArrayToWrite[10] = (byte)Math.round(127f * this.collectionSystemSpeed);
        }
        
        // write the checksum
        this.byteArrayToWrite[11] = 
                (byte) ((byte)(this.byteArrayToWrite[8] + 
                        this.byteArrayToWrite[9] + 
                        this.byteArrayToWrite[10]) & ((byte)127));
        
        this.byteArrayToWrite[12] = this.collectionAndDumpMotorControllerAddress;
        
        if (this.dumpSystemSpeed < 0f){
            // drive dump system in reverse
            this.byteArrayToWrite[13] = (byte)5;
            this.byteArrayToWrite[14] = (byte)Math.round(-127f * this.dumpSystemSpeed);
        } else {
            // drive dump system forward
            this.byteArrayToWrite[13] = (byte)4;
            this.byteArrayToWrite[14] = (byte)Math.round(127f * this.dumpSystemSpeed);
        }
        
        // write the checksum
        this.byteArrayToWrite[15] = 
                (byte) ((byte)(this.byteArrayToWrite[12] + 
                        this.byteArrayToWrite[13] + 
                        this.byteArrayToWrite[14]) & ((byte)127));
        
        if (DEBUG){
            System.out.println("Bytes: " + Arrays.toString(byteArrayToWrite));
        }
        
    }
    
    private void writeByteArrayToMotorControllers(){
        try {
            this.serialPort.write(byteArrayToWrite);
        } catch (SerialPortException ex) {
            Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setNewManutalControlReceivedInfo(
            boolean runManualControl, 
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
            float   dumpSystemAutonomousSpeed) {
        this.driveMotorMaxSpeed = driveMotorMaxSpeed;
        
        
        
        if (runManualControl){
            // get motor speed values from the manual control
            if (this.autonomousSystem != null){
                this.autonomousSystem.stopAutonomousSystemThreadLogicExecution();
            }
            this.leftMotorSpeed = leftMotorSpeed;
            this.rightMotorSpeed = rightMotorSpeed;
            this.collectionSystemSpeed = collectionSystemSpeed;
            this.dumpSystemSpeed = dumpSystemSpeed;
            
            
            
        } else {
            if (runAutonomousSystem){
                // get motor info
                if (this.autonomousSystem != null){
                    this.autonomousSystem.startAutonomousSystemThreadLogicExecution();
                    this.autonomousSystem.setMainDriveSpeedMultiplier(driveMotorMaxSpeed);
                    if (issueAutonomousStartSignal){
                        this.autonomousSystem.issueStartSignal();
                    }
                    if (issueAutonomousStopSignal){
                        this.autonomousSystem.issueStopSignal();
                    }
                    if (issueAutonomousResetSignal){
                        this.autonomousSystem.resetForNextRun();
                    }
                    this.autonomousSystem.updateCountdownInSeconds(countdownTimeInSeconds);
                    this.autonomousSystem.setCollectionSystemSpeedMultiplier(collectionSystemAutonomousSpeed);
                    this.autonomousSystem.setDumpSystemSpeedMultiplier(dumpSystemAutonomousSpeed);
                    this.leftMotorSpeed = 
                            this.autonomousSystem.getLeftMainDriveMotorFloat();
                    this.rightMotorSpeed = this.autonomousSystem.getRightMainDriveMotorFloat();
                    this.collectionSystemSpeed = this.autonomousSystem.getCollectionSystemFloat();
                    this.dumpSystemSpeed = this.autonomousSystem.getDumpSystemFloat();
                    
                    
                    
                } else {
                    this.stopMotors();
                }
            } else {
                this.stopMotors();
            }
        }
        
        if (this.leftMotorSpeed < (-1f * this.driveMotorMaxSpeed)){
            this.leftMotorSpeed = -1f * this.driveMotorMaxSpeed;
        }
        
        if (this.leftMotorSpeed > this.driveMotorMaxSpeed){
            this.leftMotorSpeed = this.driveMotorMaxSpeed;
        }

        if (this.rightMotorSpeed < (-1f * this.driveMotorMaxSpeed)){
            this.rightMotorSpeed = -1f * this.driveMotorMaxSpeed;
        }
        
        if (this.rightMotorSpeed > this.driveMotorMaxSpeed){
            this.rightMotorSpeed = this.driveMotorMaxSpeed;
        }
        
        this.reComputeByteArrayToWriteToMotorControllers();
        this.writeByteArrayToMotorControllers();
        
    }

    @Override
    public void stopMotors() {
        this.leftMotorSpeed = 0f;
        this.rightMotorSpeed = 0f;
        this.collectionSystemSpeed = 0f;
        this.dumpSystemSpeed = 0f;
        this.reComputeByteArrayToWriteToMotorControllers();
        this.writeByteArrayToMotorControllers();
    }
}
