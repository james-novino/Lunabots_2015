/*
 * Written By David Osinsksi
 * 
 * Edited by Mark Halberstadt
 * 
 * Pressing forward on the joystick makes the robot go forward
 * Pressing button 5 extends the collection bucket actuators while 3 retracts them
 */
package gamecontroller;


import java.util.Arrays;
import java.util.LinkedList;
import manual_control_gui.countdownListener;
import manual_control_gui.countdownTimer;
import manual_control_gui.manualControlGUI;
import net.java.games.input.*;
import udp_manualcontrol_transmit.UDPManualControlTransmitter;


public class GameController implements ControllerListener, Runnable, 
        ButtonDefinitions, countdownListener {

    
    /*
     * set debug to True to print debug messages.
     */
    private static final boolean DEBUG = false;
    
    private UDPManualControlTransmitter UDPManualControlTransmitter;
    
    /*
     * Controller Definitions
     */
    private static final String NAME = "Logitech Extreme 3D";//name of controller
    private static ControllerEnvironment ce;//where all controllers live
    private static Component controls[];//temporary storage for controllers
    private static Controller controller;//the game controller
    private static ControllerEvent controlevent;
    private EventQueue eq;
    private Event e;
    private boolean isConnected = false;//status of whether or not the controller is plugged in 
    //private boolean LIMITED = true;//limits the rate of change
    /*
     * Used to select inputs from event queue
     */
    private Component xAxisComp = null;//container for X axis component
    private Component yAxisComp = null;//container for Y axis component
    private Component zAxisComp = null;//container for Z axis component
    private Component gainComponent = null;//container for the gain knob component
    private Component collectionSystemEnable = null;
    private Component dumpSystemEnable = null;
    private Component robotEnable = null;
    private Component robotDisable = null;
    
    private boolean runManualControlBoolToSend = false;
    private boolean issueAutonomousStartSignalBoolToSend = false;
    private boolean runAutonomousSystemBoolToSend = false;
    private boolean issueAutonomousStopSignalBoolToSend = false;
    private boolean issueAutonomousResetSignalBoolToSend = false;
    
    private float  zAxisValue = 0f;
    private boolean dumpSystemEnabledBool = false;
    private float  dumpSystemSpeed = 0f;
    private boolean collectionSystemEnabledBool = false;
    private float  collectionSystemSpeed = 0f;
    private boolean robotEnabled = false;
    private float  motorSpeedGain = 0f;
    private float  motorSpeedFromJoystickYPosition = 0f;
    private float  motorSpeedFinal     = 0f;
    private float  steering       = 0f;
    
    private float leftMotorSpeed = 0f;
    private float rightMotorSpeed = 0f;
    
    private float deadzone = 0.05f;
    
    private manualControlGUI gui;
    
    private countdownTimer countdownTimer;
    
    
    Thread t;//controller runs as a separate thread
    
    
    
    
    public GameController() {
        isConnected = findController();
        this.countdownTimer = new countdownTimer(10);
        this.countdownTimer.addCountdownListener(this);
        if (this.isConnected){
            this.mapComponents();
        }
        t = new Thread(this);
        t.start();
    }
    
    private void updateGUI(){
        if (this.gui != null){
            // if the gui is not null, then update it
            if (this.isConnected){
                this.gui.setJoystickConnected();
                this.gui.setJoystickXPosition(this.steering);
                this.gui.setJoystickYPosition(this.motorSpeedFromJoystickYPosition);
                this.gui.setJoystickZPosition(this.zAxisValue);
                this.gui.setCollectionSystemSpeed(collectionSystemSpeed);
                this.gui.setDumpSystemSpeed(dumpSystemSpeed);
                this.gui.setMotorSpeeds(leftMotorSpeed, rightMotorSpeed);
                this.gui.setRobotEnabled(robotEnabled);
                
                this.gui.resetTextLabelsForCheckBoxes();
                
            } else {
                this.gui.setJoystickDisconnected();
            }
            
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (this.isConnected){
                    // reset all data fields with new data from joystick
                    this.poll();
                    
                    // recalculate the motor speeds to send
                    this.recalculateMotorSpeeds();
                    
                    // send the motor speeds
                    if (this.UDPManualControlTransmitter != null){
                        this.sendManualControlData();
                    }
                }
                else {
                    this.isConnected = this.findController();
                }
                // update the gui
                this.updateGUI();
                t.sleep(50);
            }
        } catch (InterruptedException ex) {
        }
    }

    private boolean poll() {
        if (controller.poll()) {
            e = new Event();
            eq = controller.getEventQueue();
            
            
            /*
             * Read all events from event queue
             */
            while (eq.getNextEvent(e)) {

                /*
                 * Print out everything
                 */
                //System.out.println(e.getComponent().getName() + " " + e.getValue());

                
                if (e.getComponent().equals(gainComponent)) {
                    // set the motor speed gain
                    this.motorSpeedGain = ((1.0f - e.getValue())/2.0f);
                    // check to see if the robot is enabled
                    if (this.robotEnabled){
                        this.motorSpeedFinal = this.motorSpeedGain * this.motorSpeedFromJoystickYPosition;
                    } else {
                        // robot not enabled
                        this.motorSpeedFinal = 0f;
                    }
                    
                }else if (e.getComponent().equals(robotEnable)){
                    this.robotEnabled = true;
                    this.motorSpeedFinal = this.motorSpeedGain * this.motorSpeedFromJoystickYPosition;
                } else if (e.getComponent().equals(robotDisable)) {
                    // the robot is disabled - set motor speed to zero.
                    this.robotEnabled = false;
                    this.motorSpeedFinal = 0f;
                } else if (e.getComponent().equals(xAxisComp)) {
                    // set the steering component
                    this.steering =  e.getValue();
                } else if (e.getComponent().equals(yAxisComp)) {
                    if(Math.abs(e.getValue())<deadzone){
                        this.motorSpeedFromJoystickYPosition = 0f;
                    }else{
                        this.motorSpeedFromJoystickYPosition =  -1f * e.getValue();
                    }
                    
                    // check to see if the robot is enabled
                    if (this.robotEnabled){
                        this.motorSpeedFinal = this.motorSpeedGain * this.motorSpeedFromJoystickYPosition;
                    } else {
                        // robot not enabled
                        this.motorSpeedFinal = 0f;
                    }
                    
                    
                        
                } else if (e.getComponent().equals(this.zAxisComp)){
                    // record the value of the Z Axis component
                    this.zAxisValue =  e.getValue();
                    if (this.robotEnabled){
                        if (this.collectionSystemEnabledBool){
                            this.collectionSystemSpeed = this.zAxisValue;
                        } else {
                            this.collectionSystemSpeed = 0f;
                        }
                        
                        if (this.dumpSystemEnabledBool){
                            this.dumpSystemSpeed = this.zAxisValue;
                        } else {
                            this.dumpSystemSpeed = 0f;
                        }
                    } else {
                        this.collectionSystemSpeed = 0f;
                        this.dumpSystemSpeed = 0f;
                    }
                } else if (e.getComponent().equals(collectionSystemEnable)) { 
                    if (e.getValue() != 0f){
                        // button is pressed
                        this.collectionSystemEnabledBool = true;
                        if (this.robotEnabled){
                            this.collectionSystemSpeed = this.zAxisValue;
                        } else {
                            this.collectionSystemSpeed = 0f;
                        }
                    } else {
                        // value is zero - button is not pressed
                        this.collectionSystemEnabledBool = false;
                        this.collectionSystemSpeed = 0f;
                    }
                } else if (e.getComponent().equals(dumpSystemEnable)) { 
                    if (e.getValue() != 0f){
                        // button is pressed
                        this.dumpSystemEnabledBool = true;
                        if (this.robotEnabled){
                            this.dumpSystemSpeed = this.zAxisValue;
                        } else {
                            this.dumpSystemSpeed = 0f;
                        }
                    } else {
                        // button is not pressed
                        this.dumpSystemEnabledBool = false;
                        this.dumpSystemSpeed = 0f;
                    }
                    
                }
            }
            
            

            //System.out.println(Arrays.toString(packet));
            return true;
        }//get information from controller
        return false;
    }
    
    

    private void mapComponents() {
        controls = controller.getComponents();
        if (DEBUG){
            System.out.println(Arrays.toString(controls));
        }
        xAxisComp = controls[X_AXIS];
        yAxisComp = controls[Y_AXIS];
        zAxisComp = controls[Z_ROTATION];
        gainComponent = controls[SLIDER];

        collectionSystemEnable = controls[BUTTON_7];
        
        dumpSystemEnable = controls[BUTTON_8];
        
        robotEnable = controls[BUTTON_11];
        robotDisable = controls[BUTTON_12];
    }

    private boolean findController() {

        ce = ControllerEnvironment.getDefaultEnvironment();
        ce.addControllerListener(this);
        Controller[] ca = ce.getControllers();

        for (int i = 0; i < ca.length; i++) {

            
            if (ca[i].getName().equals(NAME)) {
                for(int j=0;j<ca[i].getComponents().length;j++){
                    if (DEBUG){
                        System.out.println(j+": "+ca[i].getComponents()[j].getName()); //prints all controllers available
                    }
                }
                controller = ca[i];
                controlevent = new ControllerEvent(controller);
                this.controllerAdded(controlevent);
                controls = controller.getComponents();
                eq = controller.getEventQueue();
                return true;
            }
        }
        
        return false;
    }
    
    private void recalculateMotorSpeeds(){
        if (this.robotEnabled){
            if (this.steering < 0f){
                // left turn
                this.rightMotorSpeed = this.motorSpeedFinal;
                this.leftMotorSpeed = ((2f * this.steering) + 1f) * this.motorSpeedFinal;
            } else {
                // right turn
                this.leftMotorSpeed = this.motorSpeedFinal;
                this.rightMotorSpeed = ((-2f * this.steering) + 1f) * this.motorSpeedFinal;
            }
        } else {
            this.leftMotorSpeed = 0f;
            this.rightMotorSpeed = 0f;
        }
        if (DEBUG){
            System.out.println("Dump Speed: " + this.dumpSystemSpeed +
                    "Dump Enabled: " + this.dumpSystemEnabledBool + 
                    "Collection Speed Speed: " + this.collectionSystemSpeed + 
                    "Collection Enabled: " + this.collectionSystemEnabledBool);
        }
    }
    
    private void sendManualControlData(){
        if (!this.robotEnabled){
            this.leftMotorSpeed = 0f;
            this.rightMotorSpeed = 0f;
            this.collectionSystemSpeed = 0f;
            this.dumpSystemSpeed = 0f;
        }
        
        if (this.gui != null){
        
            this.UDPManualControlTransmitter.sendManualControlData(
                    this.runManualControlBoolToSend,   // should manual control run
                    this.issueAutonomousStartSignalBoolToSend,  // issue autonomous start signal
                    this.runAutonomousSystemBoolToSend,  // run autonomous system
                    this.issueAutonomousStopSignalBoolToSend,  // issue autonomous stop signal
                    this.issueAutonomousResetSignalBoolToSend,  // issue autonomous reset signal
                    this.countdownTimer.getCurrentNumSecondsInCountdown(),      // countdown time in seconds
                    this.leftMotorSpeed,   // left motor speed (-1 : 1)
                    this.rightMotorSpeed,  // right motor speed (-1 : 1)
                    collectionSystemSpeed,  // collection system speed (-1 : 1)
                    dumpSystemSpeed,       // dump system speed  ( -1 : 1)
                    this.gui.getMainDriveMotorMaxSpeed(),
                    this.gui.getCollectionSystemSpeed(),
                    this.gui.getDumpSystemSpeed()
                    );
        } else {
            this.UDPManualControlTransmitter.sendManualControlData(
                this.runManualControlBoolToSend,   // should manual control run
                this.issueAutonomousStartSignalBoolToSend,  // issue autonomous start signal
                this.runAutonomousSystemBoolToSend,  // run autonomous system
                this.issueAutonomousStopSignalBoolToSend,  // issue autonomous stop signal
                this.issueAutonomousResetSignalBoolToSend,  // issue autonomous reset signal
                this.countdownTimer.getCurrentNumSecondsInCountdown(),      // countdown time in seconds
                this.leftMotorSpeed,   // left motor speed (-1 : 1)
                this.rightMotorSpeed,  // right motor speed (-1 : 1)
                collectionSystemSpeed,  // collection system speed (-1 : 1)
                dumpSystemSpeed,       // dump system speed  ( -1 : 1)
                0.1f,
                1f,
                1f
                );
        }
    }
    
    public void setUDPTransmitter(UDPManualControlTransmitter Tx){
        this.UDPManualControlTransmitter = Tx;
    }
    
    public void setManualControlGUI(manualControlGUI gui){
        this.gui = gui;
    }


    @Override
    public void controllerRemoved(ControllerEvent ce) {
        System.out.println("Controller Removed");
    }

    @Override
    public void controllerAdded(ControllerEvent ce) {
        System.out.println("Controller Connected");
    }

    public countdownTimer getCountdownTimer(){
        return this.countdownTimer;
    }
    
    public void setIssueAutonomousResetSignalBoolToSend(boolean issueResetSignal){
        this.issueAutonomousResetSignalBoolToSend = issueResetSignal;
    }
    
    public boolean getIssueAutonomousResetSignalBoolToSend(){
        return this.issueAutonomousResetSignalBoolToSend;
    }
    
    public void setIssueAutonomousStopSignalBoolToSend(boolean issueStopSignal){
        this.issueAutonomousStopSignalBoolToSend = issueStopSignal;
    }
    
    public boolean getIssueAutonomousStopSignalBoolToSend(){
        return this.issueAutonomousStopSignalBoolToSend;
    }
    
    public void setRunAutonomousSystemBoolToSend(boolean runAutoSys){
        this.runAutonomousSystemBoolToSend = runAutoSys;
    }
    
    public boolean getRunAutonomousSystemBoolToSend(){
        return this.runAutonomousSystemBoolToSend;
    }
    
    public void setIssueAutonomousStartSignalBoolToSend(boolean issueStartSignal){
        this.issueAutonomousStartSignalBoolToSend = issueStartSignal;
    }
    
    public boolean getIssueAutonomousStartSignalBoolToSend(){
        return this.issueAutonomousStartSignalBoolToSend;
    }
    
    public void setRunManualControlBooleanValue(boolean runManualControl){
        this.runManualControlBoolToSend = runManualControl;
    }
    
    public boolean getRunManualControlBoolToSend(){
        return this.runManualControlBoolToSend;
    }
    
    

    /**
     * Returns the status of the controller. Returns true if the controller is
     * detected.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /*
     * Used for GUI, returns the game controller that is connected
     */
    @Override
    public String toString() {
        return controller.getName();
    }
    
    
    /*
     * Main Program for Testinf of Joystick
     */
    public static void main (String args[]){
        
        GameController g = new GameController();
        UDPManualControlTransmitter udpTx = new UDPManualControlTransmitter(
                "192.168.0.10", 1050);
        g.setUDPTransmitter(udpTx);
        manualControlGUI gui = new manualControlGUI();
        gui.setVisible(true);
        g.setManualControlGUI(gui);
        gui.setCountdownTimer(g.getCountdownTimer());
        gui.setGameController(g);
        
    }

    @Override
    public void updateCountdownInSeconds(int currentCountdownInSeconds, String currentCountdownString) {
        
    }

    @Override
    public void countdownTimerFinished() {
        runManualControlBoolToSend = false;
        issueAutonomousStartSignalBoolToSend = true;
        runAutonomousSystemBoolToSend = true;
        issueAutonomousStopSignalBoolToSend = false;
        issueAutonomousResetSignalBoolToSend = false;
        this.gui.setCheckBoxesToCurrentBooleanValues();
    }

    
    
    

    
}
