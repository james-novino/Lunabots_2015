/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;
import lunarenagui.swing_2d_lunarena.lunArenaSwingGUI;

import networkTransmitToGUI.autonomousSystemListener;
import serialComm.arduinoReadByte;
import udp_autonomous_transmit.UDPAutonomousTransmitter;
import udp_location_receive.locationSystemListener;

/**
 *
 * @author chalbers2
 */
public class autonomousSystem implements locationSystemListener, countdownListener, Runnable {
    
    
    public final float thresholdDistanceForReachingTargetInCentimeters = 9.2f;
    
    public final int arenaXDimensionInCentimeters = 738;
    public final int arenaYDimensionInCentimeters = 388;
    
    private LinkedList autonomousSystemListeners;
    private lunArenaObstacleGrid lunArenaObstacleGrid;
    private lunabot lunabot;
    private boolean startSignalReceived;
    private boolean stopSignalReceived;
    private boolean resetForNextRunReceived;
    
    private boolean shouldAutonomousThreadRun;
    private boolean shouldAutonomousSystemThreadLogicBeComputed;
    private boolean shouldPathBeComputed;
    
    private boolean shouldRobotMove;
    
    private target currentTarget;
    
    private int countdownMinutes;
    private int countdownSeconds;
    private lunArenaSwingGUI localGUI;
    
    private LinkedList RobotPath;
    private int tripToDigNumber;
    
    // If set to 1, this gives full power to the motors
    private float lunabotMainDriveSpeedMultiplier;
    private float lunabotCollectionSystemSpeedMultiplier;
    private float lunabotDumpSystemSpeedMultiplier;
    
    // set this to false if driving forward - set to true to drive in reverse
    private boolean driveInReverse;
    private boolean doWeCareIfRobotDrivesInReverseOrNot;
    
    private float leftMainDriveMotorFloat = 0f;
    private float rightMainDriveMotorFloat = 0f;
    private float collectionSystemSpeedFloat = 0f;
    private float dumpSystemSpeedFloat = 0f;
    
    
    private String autonomousSystemStateString;
    private int autonomousSystemState;
    
    private Thread autonomousSystemThread;
    
    public final long millisPerAutoSystemLoop = 30;
    
    private volatile boolean isAutonomousSystemCurrentlyInWaitingState = false;
    
    private UDPAutonomousTransmitter autonomousSystemUDPTransmitter;
    
    private arduinoReadByte sensorBoard;
    
    private boolean shouldBeaconPoleBeExtended = false;
    
    private countdownTimer countdownTimer;
    
    
    
    
    /**
     * Constructor
     */
    public autonomousSystem(){
        this.autonomousSystemListeners = new LinkedList();
        this.lunArenaObstacleGrid = new lunArenaObstacleGrid();
        this.lunabot = new lunabot();
        this.lunabot.setAutonomousSystemController(this);
        this.autonomousSystemState = autoStateDefs.INIT_STATE;
        this.autonomousSystemStateString = "STATE UNKNOWN";
        this.startSignalReceived = false;
        this.stopSignalReceived = false;
        this.resetForNextRunReceived = false;
        this.isAutonomousSystemCurrentlyInWaitingState = false;
        
        this.doWeCareIfRobotDrivesInReverseOrNot = false;
        
        this.lunabotCollectionSystemSpeedMultiplier = 1.0f;
        this.lunabotDumpSystemSpeedMultiplier = 1.0f;
        this.lunabotMainDriveSpeedMultiplier = 0.1f;
        this.driveInReverse = false;
        this.shouldPathBeComputed = false;
        this.shouldRobotMove = false;
        this.tripToDigNumber = 0;
        
        this.RobotPath = new LinkedList();
        
        
        
        // set the boolean such that the main loop will run
        this.shouldAutonomousThreadRun = true;
        this.shouldAutonomousSystemThreadLogicBeComputed = true;
        try {
            this.sensorBoard = new arduinoReadByte("/dev/due01", 9600);
        } catch (SerialPortException ex) {
            Logger.getLogger(autonomousSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // start the thread running
        this.autonomousSystemThread = new Thread(this);
        this.autonomousSystemThread.start();
        
    }
    
    public void setUDPAutonomousSystemTransmitterToGUI(UDPAutonomousTransmitter AutoTx){
        this.autonomousSystemUDPTransmitter = AutoTx;
    }
    
    public void stopAutonomousSystemThreadLogicExecution(){
        this.shouldAutonomousSystemThreadLogicBeComputed = false;
    }
    
    public void startAutonomousSystemThreadLogicExecution(){
        this.shouldAutonomousSystemThreadLogicBeComputed = true;
    }
    
    public void setCollectionSystemSpeedMultiplier(float collectionSystemSpeedMult){
        this.lunabotCollectionSystemSpeedMultiplier = collectionSystemSpeedMult;
    }
    
    public void setDumpSystemSpeedMultiplier(float dumpSystemSpeedMult){
        this.lunabotDumpSystemSpeedMultiplier = dumpSystemSpeedMult;
    }
    
    public float getLeftMainDriveMotorFloat(){
        if (this.shouldRobotMove){
            return this.leftMainDriveMotorFloat;
        } else {
            return 0f;
        }
        
    }
    
    public float getRightMainDriveMotorFloat(){
        if (this.shouldRobotMove){
            return this.rightMainDriveMotorFloat;
        } else {
            return 0f;
        }
        
    }
    
    public float getCollectionSystemFloat(){
        return this.collectionSystemSpeedFloat;
    }
    
    public float getDumpSystemFloat(){
        return this.dumpSystemSpeedFloat;
    }
    
    public void setMainDriveSpeedMultiplier(float mainDriveSpeedMultiplier){
        this.lunabotMainDriveSpeedMultiplier = Math.abs(mainDriveSpeedMultiplier);
        if (this.lunabotMainDriveSpeedMultiplier > 1f){
            this.lunabotMainDriveSpeedMultiplier = 1f;
        }
    }
    
    public void setObstacleInArena(int xCM, int yCM, int radiusCM){
        // start delete here
        // obstacle test
        for (int i = 0; i<15; i++){
            this.lunArenaObstacleGrid.recordObstacleAtXYInCMWithRadiusInCM(xCM, yCM, radiusCM);
        }
        
        // stop delete here
    }
    
    /**
     * This method will start running a local GUI displaying information on the autonomous system
     * This method should not be run on the final robot.
     */
    public void turnOnGUI(){
       this.localGUI = new lunArenaSwingGUI();
       this.localGUI.setVisible(true);
       this.addAutonomousSystemListener(localGUI); 
    }
    
    /**
     * This method, when called, represents a positive clock edge update of the state machine.
     * This will run whenever the autonomous system receives new data from the location system
     */
    private void autonomousSystemStateMachinePositiveClockEdge(){
        switch(this.autonomousSystemState){
            case autoStateDefs.INIT_STATE:
                // init state behavior
                this.shouldPathBeComputed = false;
                this.shouldRobotMove = false;
                this.autonomousSystemStateString = "Initialization State";
                this.autonomousSystemState = autoStateDefs.WAIT_FOR_START_SIGNAL;
                break;
                // end of init state
                
            case autoStateDefs.WAIT_FOR_START_SIGNAL:
                // wait for start signal state
                this.shouldPathBeComputed = false;
                this.shouldRobotMove = false;
                this.autonomousSystemStateString = "Waiting for start signal. \n" + 
                        "Countdown Time:" + "\n" + this.countdownMinutes + " Minutes, " 
                        + this.countdownSeconds + " Seconds";
                if (this.startSignalReceived){
                    this.autonomousSystemState = autoStateDefs.START_SIGNAL_RECEIVED;
                    this.tripToDigNumber++;
                }
                break;
                // end of wait for start signal state
            
            case autoStateDefs.START_SIGNAL_RECEIVED:
                // start signal received state
                this.shouldPathBeComputed = false;
                this.shouldRobotMove = false;
                this.autonomousSystemStateString = "Start Signal Received.";
                this.autonomousSystemState = autoStateDefs.DRIVE_TO_DIG_START_TARGET;
                this.shouldBeaconPoleBeExtended = true;
                break;
                // start signal received state
                
            case autoStateDefs.DETERMINE_START_ROUTINE:
                // determine start routine state
                this.shouldPathBeComputed = false;
                this.shouldRobotMove = false;
                this.autonomousSystemStateString = "Determining Route For \n Start Sequence";
                float headingAngle = this.lunabot.getLunabotHeadingAngleInRadians();
                char facing = 'u';
                char position = 'u';
                
                // check to see if the heading angle indicates robot facing
                if (headingAngle < (float)Math.PI/4.0f && headingAngle >= (float)Math.PI/-4.0f){
                    // check to see if heading angle is between pi/4 and -pi/4
                    // robot is facing EAST if true
                    facing = 'E';
                } else if (headingAngle < (float)Math.PI*3.0f/4.0f && headingAngle >= (float)Math.PI/4.0f){
                    // check to see if the heading angle is between 3pi/4 and pi/4
                    // if true, robot is facing NORTH
                    facing = 'N';
                } else if (headingAngle < (float)Math.PI/-4.0f && headingAngle >= (float)Math.PI*-3.0f/4.0f){
                    // check to see if the heading angle is between -pi/4 and -3pi/4
                    // if so, the robot is facing west
                    facing = 'S';
                } else { // if we get to this position, then the heading angle is between 3pi/4 and -3pi/4
                         // robot is facing west
                    facing = 'W';
                }
                
                if (this.lunabot.getLunabotYPositionInArenaInCentimeters() < this.arenaYDimensionInCentimeters/2){
                    // the lunabot is currently in the south starting box
                    position = 'S';
                    if (facing == 'E'){
                        this.autonomousSystemState = autoStateDefs.START_ROUTINE_SOUTH_POSITION_EAST_FACING;
                    } else if (facing == 'N'){
                        this.autonomousSystemState = autoStateDefs.START_ROUTINE_SOUTH_POSITION_NORTH_FACING;
                    } else if (facing == 'S'){
                        this.autonomousSystemState = autoStateDefs.START_ROUTINE_SOUTH_POSITION_SOUTH_FACING;
                    } else {
                        // facing == 'W';
                        this.autonomousSystemState = autoStateDefs.START_ROUTINE_SOUTH_POSITION_WEST_FACING;
                    }
                } else {
                    // the lunabot is currently in the north starting box
                    position = 'N';
                    if (facing == 'E'){
                        this.autonomousSystemState = autoStateDefs.START_ROUTINE_NORTH_POSITION_EAST_FACING;
                    } else if (facing == 'N'){
                        this.autonomousSystemState = autoStateDefs.START_ROUTINE_NORTH_POSITION_NORTH_FACING;
                    } else if (facing == 'S'){
                        this.autonomousSystemState = autoStateDefs.START_ROUTINE_NORTH_POSITION_SOUTH_FACING;
                    } else {
                        // facing == 'W';
                        this.autonomousSystemState = autoStateDefs.START_ROUTINE_NORTH_POSITION_WEST_FACING;
                    }
                }
                
                break;
                // end of determine start routine state
            case autoStateDefs.START_ROUTINE_NORTH_POSITION_EAST_FACING:
                // state definition for START_ROUTINE_NORTH_POSITION_EAST_FACING
                this.driveInReverse = false;
                this.currentTarget = new target();
                
                
                // setup the next target to drive to 
                this.currentTarget.x = this.lunabot.getLunabotXPositionInArenaInCentimeters() + 30;
                this.currentTarget.y = this.lunabot.getLunabotYPositionInArenaInCentimeters() - 7;
                this.currentTarget.shouldWeArriveAtGivenAngle = false;
                
                
                this.shouldPathBeComputed = true;
                this.shouldRobotMove = true;
                
                if (this.hasRobotArrivedAtTarget()){
                    this.autonomousSystemState = autoStateDefs.START_ROUTINE_NORTH_POSITION_EAST_FACING_step2;
                }
                // end of definition for START_ROUTINE_NORTH_POSITION_EAST_FACING
                break;
                
            case autoStateDefs.START_ROUTINE_NORTH_POSITION_EAST_FACING_step2:
                this.driveInReverse = true;
                this.currentTarget = new target();
                
                // setup the next target to drive to 
                this.currentTarget.x = this.lunabot.getLunabotXPositionInArenaInCentimeters() - 25;
                this.currentTarget.y = this.lunabot.getLunabotYPositionInArenaInCentimeters() - 7;
                this.currentTarget.shouldWeArriveAtGivenAngle = false;
                
                this.shouldPathBeComputed = true;
                this.shouldRobotMove = true;
                
                if (this.hasRobotArrivedAtTarget()){
                    this.autonomousSystemState = autoStateDefs.START_ROUTINE_NORTH_POSITION_EAST_FACING_step3;
                }
                
                // end of state START_ROUTINE_NORTH_POSITION_EAST_FACING_step2
                break;
                
            case autoStateDefs.START_ROUTINE_NORTH_POSITION_EAST_FACING_step3:
                // 
                this.driveInReverse = false;
                this.currentTarget = new target();
                
                
                // setup the next target to drive to 
                this.currentTarget.x = this.lunabot.getLunabotXPositionInArenaInCentimeters() + 25;
                this.currentTarget.y = this.lunabot.getLunabotYPositionInArenaInCentimeters() - 7;
                this.currentTarget.shouldWeArriveAtGivenAngle = false;
                
                this.shouldPathBeComputed = true;
                this.shouldRobotMove = true;
                
                if (this.hasRobotArrivedAtTarget()){
                    if (this.lunabot.getLunabotYPositionInArenaInCentimeters() < 330){
                        
                    }
                }
                
                break;
                
            case autoStateDefs.DRIVE_TO_DIG_START_TARGET:
                this.driveInReverse = false;
                this.doWeCareIfRobotDrivesInReverseOrNot = false;
                this.currentTarget = new target();
                this.shouldBeaconPoleBeExtended = true;
                
                this.currentTarget.x = targetDefs.DIG_START_TARGET[0];
                this.currentTarget.y = targetDefs.DIG_START_TARGET[1];
                this.currentTarget.speedMultiplier = (float)targetDefs.DIG_START_TARGET[3]/127.0f;
                this.currentTarget.shouldWeArriveAtGivenAngle = true;
                this.currentTarget.setAngleToArriveAtInRadians(0f);
                
                this.lunabotMainDriveSpeedMultiplier = 1.0f;
                
                this.collectionSystemSpeedFloat = 0f;
                this.dumpSystemSpeedFloat = 0f;
                
                this.shouldPathBeComputed = true;
                this.shouldRobotMove = true;
                
                this.autonomousSystemStateString = "Driving to Dig Start Target" + 
                        "\n" + "Trip to dig number " + this.tripToDigNumber;
                
                if (this.hasRobotArrivedAtTarget()){
                    this.shouldRobotMove = false;
                    System.out.println("Arrived!");
                    //this.autonomousSystemState = autoStateDefs.STOP_ALL;
                    this.autonomousSystemState = autoStateDefs.DRIVE_TO_DIG_FINISH_TARGET_WITH_DIG;
                }
                
                break;
                
            case autoStateDefs.TURN_ROBOT_TO_DIG_HEADING_ANGLE:
                
                break;
                
            case autoStateDefs.DRIVE_TO_DIG_FINISH_TARGET_WITH_DIG:
                this.driveInReverse = true;
                this.doWeCareIfRobotDrivesInReverseOrNot = false;
                this.currentTarget = new target();
                
                this.currentTarget.x = targetDefs.DIG_FINISH_TARGET[0];
                this.currentTarget.y = targetDefs.DIG_FINISH_TARGET[1];
                this.currentTarget.speedMultiplier = (float)targetDefs.DIG_FINISH_TARGET[3]/127.0f;
                this.currentTarget.shouldWeArriveAtGivenAngle = false;
                
                this.lunabotMainDriveSpeedMultiplier = 0.5f;
                
                this.shouldPathBeComputed = true;
                this.shouldRobotMove = true;
                
                this.collectionSystemSpeedFloat = 1.0f;
                this.dumpSystemSpeedFloat = 0.1f;
                
                this.autonomousSystemStateString = "Driving to Dig Finish Target" + 
                        "\n" + "Trip to dig number " + this.tripToDigNumber;
                
                if (this.hasRobotArrivedAtTarget()){
                    this.shouldRobotMove = false;
                    System.out.println("Arrived!");
                    this.autonomousSystemState = autoStateDefs.DRIVE_TO_DUMP_TARGET;
                }
                break;
                
            case autoStateDefs.DRIVE_TO_DUMP_TARGET:
                this.driveInReverse = true;
                this.doWeCareIfRobotDrivesInReverseOrNot = true;
                this.currentTarget = new target();
                
                this.currentTarget.x = targetDefs.DUMP_TARGET[0];
                this.currentTarget.y = targetDefs.DUMP_TARGET[1];
                this.currentTarget.speedMultiplier = (float)targetDefs.DUMP_TARGET[3]/127.0f;
                this.currentTarget.shouldWeArriveAtGivenAngle = true;
                
                this.lunabotMainDriveSpeedMultiplier = 1.0f;
                
                this.collectionSystemSpeedFloat = 0f;
                this.dumpSystemSpeedFloat = 0f;
                
                this.shouldPathBeComputed = true;
                this.shouldRobotMove = true;
                
                this.autonomousSystemStateString = "Driving to Dump Target" + 
                        "\n" + "Trip to dig number " + this.tripToDigNumber;
                
                if (this.hasRobotArrivedAtTarget()){
                    this.shouldRobotMove = false;
                    System.out.println("Arrived!");
                    this.countdownTimer = new countdownTimer(90);
                    this.countdownTimer.startCountdown();
                    this.autonomousSystemState = autoStateDefs.DUMP_MATERIAL;
                }
                break;
                
            case autoStateDefs.DUMP_MATERIAL:
                this.shouldPathBeComputed = false;
                this.shouldRobotMove = false;
                this.dumpSystemSpeedFloat = 1.0f;
                this.autonomousSystemStateString = "Dumping Material";
                
                
                
                if (this.countdownTimer.getCurrentNumSecondsInCountdown() <= 0){
                    this.dumpSystemSpeedFloat = 0f;
                    this.autonomousSystemState = autoStateDefs.STOP_ALL;
                }
                break;
                
            case autoStateDefs.STOP_ALL:
                this.shouldRobotMove = false;
                this.shouldPathBeComputed = false;
                this.autonomousSystemStateString = "Robot Stopped.";
                break;
                
            case autoStateDefs.RESET:
                this.shouldBeaconPoleBeExtended = false;
                this.autonomousSystemState = autoStateDefs.INIT_STATE;
                break;
                
            default: 
                // default behavior
                this.autonomousSystemStateString = "INIT STATE";
                this.autonomousSystemState = autoStateDefs.INIT_STATE;
                // end of default behavior
                break;
        }
        
        if (this.isResetForNextRunSignalReceived()){
            this.autonomousSystemState = autoStateDefs.RESET;
        }
    }
    
    
    public boolean hasRobotArrivedAtAppropriateAngle(){
        boolean retval = false;
        return retval;
    }
    
    
    public boolean hasRobotArrivedAtTarget(){
        boolean retval = false;
        float x = (float)(this.currentTarget.x - 
                this.lunabot.getLunabotXPositionInArenaInCentimeters());
        float y = (float)(this.currentTarget.y - 
                this.lunabot.getLunabotYPositionInArenaInCentimeters());
        float distanceToTargetSquared = (float)((x*x) + (y*y));
        if (distanceToTargetSquared > 
                (this.thresholdDistanceForReachingTargetInCentimeters * 
                this.thresholdDistanceForReachingTargetInCentimeters)){
            retval = false;
        } else {
            retval = true;
        }
        return retval;
    }
    
    /**
     * This method will recalculate all output data after an iteration of the state machine.
     * Data calculated will include motor data as well as autonomous system monitor data.
     */
    private void recalculateOutputDataAfterStateMachineIteration(){
        
        // update the path
        LinkedList nextPath = new LinkedList();
        if (this.shouldPathBeComputed && this.currentTarget != null){
            nextPath = staticPathFindingMethods.getLunabotPathInCentimeters(lunabot, 
                    this.lunArenaObstacleGrid, 
                    this.lunabot.getLunabotXPositionInArenaInCentimeters(), 
                    this.lunabot.getLunabotYPositionInArenaInCentimeters(), 
                    this.currentTarget.x, 
                    this.currentTarget.y);
            if (nextPath != null){
                if (nextPath.size() > 0 ){
                    this.RobotPath = null;
                    this.RobotPath = new LinkedList();
                    this.RobotPath.addAll(nextPath);
                }
            }
        }
        
        // get next point to drive to
        
        Object nextPointObject = this.RobotPath.peekLast();
        
        if (nextPointObject != null){
            
            short [] nextPoint = (short [])nextPointObject;
            
            int xTarget = (int)nextPoint[0];
            int yTarget = (int)nextPoint[1];
            
            System.out.println("xTarget: " + xTarget + ", yTarget " + yTarget);
            
            // set motor drive bytes here
            
            if (this.shouldRobotMove && this.lunabot.isLunabotPositionKnown()){
            
                 mainDriveMotorFloatStructClass floatOut =  motorSpeedCalculator.getLeft_RightDriveMotorBytes(
                        this.shouldRobotMove,
                        this.doWeCareIfRobotDrivesInReverseOrNot,
                        this.driveInReverse,
                        this.lunabotMainDriveSpeedMultiplier,
                        this.lunabot.getLunabotXPositionInArenaInCentimeters(),
                        this.lunabot.getLunabotYPositionInArenaInCentimeters(),
                        this.lunabot.getLunabotHeadingAngleInRadians(),
                        xTarget,
                        yTarget
                        );

                 this.leftMainDriveMotorFloat = floatOut.leftMotorSpeed;
                 this.rightMainDriveMotorFloat = floatOut.rightMotorSpeed;
            } else {
                this.leftMainDriveMotorFloat = 0f;
                this.rightMainDriveMotorFloat = 0f;
            }
            
        } else {
            this.leftMainDriveMotorFloat = 0f;
            this.rightMainDriveMotorFloat = 0f;
        }
        
        
        
    }
    
    private void writeBeaconPoleActuationInstruction(){
        if (this.shouldBeaconPoleBeExtended){
            try {
                this.sensorBoard.write((byte)1);
            } catch (SerialPortException ex) {
                Logger.getLogger(autonomousSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.sensorBoard.write((byte)0);
            } catch (SerialPortException ex) {
                Logger.getLogger(autonomousSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
    
    
    /**
     * Adds an Autonomous system listener to the deque of listeners
     * @param l - autonomousSystemListener to be added
     */
    public void addAutonomousSystemListener(autonomousSystemListener l){
        this.autonomousSystemListeners.add(l);
    }
    
    
    
    // FIX_ME - Can Only take 100 elements at a time!
    private void updateDataOnAutonomousSystemListeners(){
        // read obstacle field grid from autonomous system listeners
        
        LinkedList arenaObstacleGrid = new LinkedList();
            
        if (this.autonomousSystemListeners.size() > 0){
            arenaObstacleGrid = this.lunArenaObstacleGrid.getObstacleGridToSendWithClear();
        } else {
            arenaObstacleGrid = this.lunArenaObstacleGrid.getObstacleGridToSendWithoutClear();
        }
        
        
        for (Object lObj: this.autonomousSystemListeners){
            autonomousSystemListener l = (autonomousSystemListener)lObj;
            /* Must send the following data.
             * 
             * This function should be called in order to update the drawing of the 
             * @param lunabotPositionInCentimeters_X - Lunabot X Position in Centimeters Measured from west (collection bin) wall
             * @param lunabotPositionInCentimeters_Y - Lunabot Y Position in Centimeters Measured from south (bottom) wall
             * @param arenaObstacleGrid - LinkedList containing length-4 short arrays as follows: {xPositionOfUpperLeftCornerInArenaInCentimeters, yPositionOfUpperLeftCornerInCentimeters, widthInCentimeters, heightInCentimeters}
             * @param RobotPath - LinkedList containing length-2 short arrays representing the X and Y locations of the predicted lunabot path in centimeters in the arena coordinate system
             * @param headingAngleInRadians - heading angle in Radians - 0 radians corresponds to heading directly east (dig wall - just like a Cartesian coordinate system).
             * @param currentAutonomousSystemState - String showing the current state of autonomous system FSM - VARIABLE LENGTH
             * 
             */
            
            short lunabotPositionInCentimeters_X = -1;
            short lunabotPositionInCentimeters_Y = -1;
            
            
            if (this.lunabot.isLunabotPositionKnown()){
                lunabotPositionInCentimeters_X = (short)this.lunabot.getLunabotXPositionInArenaInCentimeters();
                lunabotPositionInCentimeters_Y = (short)this.lunabot.getLunabotYPositionInArenaInCentimeters();
            
            }
            
            
            
            // get robot path here
            float headingAngleInRadians = this.lunabot.getLunabotHeadingAngleInRadians();
            String currentAutonomousSystemState = this.autonomousSystemStateString;
            
            if (this.RobotPath == null){
                this.RobotPath = new LinkedList();
            }
            
            LinkedList pathToSend = new LinkedList();
            pathToSend.addAll(RobotPath);
            
            //System.out.println("Size Of Array = " + arenaObstacleGrid.size());
            
            l.updateDataFromAutonomousSystem(lunabotPositionInCentimeters_X, 
                    lunabotPositionInCentimeters_Y, 
                    arenaObstacleGrid, 
                    pathToSend, 
                    headingAngleInRadians, 
                    currentAutonomousSystemState);
            
        }
    }

    /**
     * This method will be called from the location system network receiver. It will update the position and heading angle of the lunabot
     * @param xPositionInCentimeters - x position of lunabot in arena in centimeters
     * @param yPositionInCentimeters - y position of lunabot in arena in centimeters
     * @param headingAngle - heading angle of lunabot in arena in radians
     */
    @Override
    public void updateXYPositionOfLunabotInCentimeters(int xPositionInCentimeters, 
                                                       int yPositionInCentimeters, 
                                                       float headingAngle) {
        
        
        
         this.lunabot.setLunabotXAndYPositionInCentimeters(xPositionInCentimeters, yPositionInCentimeters);
         if (this.lunabot.isLunabotPositionKnown()){
             this.lunabot.setLunabotHeadingAngleInRadians(headingAngle);
         }
         
         
            // update the state machine
            //this.autonomousSystemStateMachinePositiveClockEdge();
            
            // recalculate all output based on last state machine iteration
            //this.recalculateOutputDataAfterStateMachineIteration();
            
            // update the listeners based on recalculated data.
            //this.updateDataOnAutonomousSystemListeners();
         
    }

    /**
     * This method will be called from the manual control system. It will update the countdown to start signal
     * @param numSecondsLeftInCountdown - number of seconds left in the countdown
     */
    @Override
    public void updateCountdownInSeconds(int numSecondsLeftInCountdown) {
        this.countdownMinutes = numSecondsLeftInCountdown / 60;
        this.countdownSeconds = numSecondsLeftInCountdown % 60;
    }

    /**
     * This method will be called from the manual control system. It will issue the start signal.
     */
    @Override
    public void issueStartSignal() {
        this.resetForNextRunReceived = false;
        this.stopSignalReceived = false;
        this.startSignalReceived = true;
    }
    
    
    public boolean isStartSignalReceived(){
        return this.startSignalReceived;
    }

    /**
     * This method will be called from the manual control system. It will issue the stop signal
     */
    @Override
    public void issueStopSignal() {
        this.resetForNextRunReceived = false;
        this.startSignalReceived = false;
        this.stopSignalReceived = true;
    }
    
    /**
     * This method will be used to handshake with the manual control system issuing the stop signal
     * @return 
     */
    
    public boolean isStopSignalReceived(){
        return this.stopSignalReceived;
    }

    /**
     * This method will be called from the manual control system. It will instruct the robot to reset for another run
     */
    @Override
    public void resetForNextRun() {
        this.resetForNextRunReceived = true;
        this.startSignalReceived = false;
        this.stopSignalReceived = false;
    }
    
    /**
     * Handshake signal for the manual control
     * @return 
     */
    
    public boolean isResetForNextRunSignalReceived(){
        return this.resetForNextRunReceived;
    }

    /**
     * Main thread for the autonomous system. Should update state machine and reset all variables.
     */
    @Override
    public void run() {
        while(this.shouldAutonomousThreadRun){
            this.isAutonomousSystemCurrentlyInWaitingState = false;
            
            long loopStartTimeInMillis = System.currentTimeMillis();
            // put loop logic here
            
            if (this.shouldAutonomousSystemThreadLogicBeComputed){
                // update the state machine
                this.autonomousSystemStateMachinePositiveClockEdge();

                // recalculate all output based on last state machine iteration
                this.recalculateOutputDataAfterStateMachineIteration();
                
                // write the instruction for the beacon pole
                writeBeaconPoleActuationInstruction();
                
            } else {
                this.shouldBeaconPoleBeExtended = false;
                writeBeaconPoleActuationInstruction();
            }
            
            // update the listeners based on recalculated data.
            this.updateDataOnAutonomousSystemListeners();
            
            // delay the next loop iteration
            this.isAutonomousSystemCurrentlyInWaitingState = true;
            try {
                Thread.sleep(Math.max(1, this.millisPerAutoSystemLoop - (System.currentTimeMillis() - loopStartTimeInMillis)));
            } catch (InterruptedException ex) {
                Logger.getLogger(autonomousSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    
}
