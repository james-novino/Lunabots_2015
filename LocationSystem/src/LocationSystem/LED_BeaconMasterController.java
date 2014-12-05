/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationSystem;

import LocationSystem.wiiMoteServoScanner.wiiMoteServoScannerMasterController;
import TCP.TCPClient;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import udpbeaconcontrol.UDPBeaconControl;


public class LED_BeaconMasterController implements Runnable, beaconACKListener {
    
    private final boolean DEBUG = false;
    
    private boolean beaconState;
    private int numIterations;
    private wiiMoteServoScannerMasterController northScanner;
    private wiiMoteServoScannerMasterController southScanner;
    private beaconNetworkClient networkClient;
    private LinkedList locationSystemListeners;
    private Thread masterBeaconThread;
    private final int stepTimeForMainLoopInMillis = 40;
    
    private float northScannerYCoordinateInCentimeters = 119f + 2.54f + 147.955f;
    private float southScannerYCoordinateInCentimeters = 119f + 2.54f;
    
    // EDIT HERE BASED ON JOHN'S REPORT!!!
    private float xPositionOfScannersInArenaInCentimeters = -55.88f;
    
    private final int xPositionInCMForHighPowerBeaconActivation = 200;
    private final int numBeaconCyclesWithBeaconNotSeenForHighPowerFlip = 27;
    private int       numBeaconCyclesWithBeaconNotSeen = 0;
    
    private int xPositionInArenaInCentimeters;
    private int yPositionInArenaInCentimeters;
    private float headingAngleInRadians = 0f;
    private float headingAngleOffsetInRadians = 0f;
    
    private final float beaconPositionOnLunabotInCentimeters_X = 45.5f;
    private final float beaconPositionOnLunabotInCentimeters_Y = 25.25f;
    private final double beaconPositionOnLunabotTheta = Math.atan2(
            (double)beaconPositionOnLunabotInCentimeters_Y, (double)beaconPositionOnLunabotInCentimeters_X) + Math.PI;
    private final double beaconPositionOnLunabotInCentimeters_Radius = 
            Math.sqrt((double)(beaconPositionOnLunabotInCentimeters_X * beaconPositionOnLunabotInCentimeters_X) + 
            (double)(beaconPositionOnLunabotInCentimeters_Y * beaconPositionOnLunabotInCentimeters_Y));
    
    private String initialHeading = "";
    private boolean firstTimeCompassInfoReceive = true;
    
    private boolean shouldWeResetLocationSystem = false;
    
    
    public LED_BeaconMasterController(String serverIP, int UDPPort, boolean displayParticleGUIs, String initialHeading){
        this.xPositionInArenaInCentimeters = 75;
        this.yPositionInArenaInCentimeters = 194;
        this.locationSystemListeners = new LinkedList();
        this.numIterations = 0;
        this.beaconState = false;
        this.firstTimeCompassInfoReceive = true;
        this.initialHeading = initialHeading;
        //this.northScanner = new wiiMoteServoScannerMasterController("/dev/tty.usbserial-AM01SJWN", 115200, displayParticleGUIs);
        //this.southScanner = new wiiMoteServoScannerMasterController("/dev/tty.usbserial-A9K7FPXP", 115200, displayParticleGUIs);
        
        this.northScanner = new wiiMoteServoScannerMasterController("/dev/ttyUSB1", 115200, displayParticleGUIs);
        this.southScanner = new wiiMoteServoScannerMasterController("/dev/ttyUSB0", 115200, displayParticleGUIs);
        
        this.networkClient = new UDPBeaconControl(serverIP, UDPPort);
        
        if (DEBUG){
            System.out.println("UDP Beacon Network Client started.");
        }
        
        
        this.shouldWeResetLocationSystem = false;
        
        this.northScanner.getRocketBrandArduino().getArduinoReadByte().setLED_BeaconMasterController(this);
        this.southScanner.getRocketBrandArduino().getArduinoReadByte().setLED_BeaconMasterController(this);
        
        this.networkClient.addBeaconACKListeners(northScanner);
        this.networkClient.addBeaconACKListeners(this.southScanner);
        this.networkClient.addBeaconACKListeners(this);
        
        this.masterBeaconThread = new Thread(this);
        this.masterBeaconThread.start();
    }
    
    public void resetLocationSystem(){
        this.shouldWeResetLocationSystem = true;
    }
    
    public boolean shouldLocationSystemReset(){
        return this.shouldWeResetLocationSystem;
    }
    
    public int getXPositionInArenaInCentimeters(){
        return this.xPositionInArenaInCentimeters;
    }
    
    public int getYPositionInArenaInCentimeters(){
        return this.yPositionInArenaInCentimeters;
    }
    
    public float getHeadingAngleInRadians(){
        return this.headingAngleInRadians;
    }
    
    public void addToHeadingOffsetAngleInRadians(float headingAngleToAdd){
        this.headingAngleOffsetInRadians += headingAngleToAdd;
    }
    
    public void addLocationSystemListener(locationSystemListener l){
        this.locationSystemListeners.add(l);
    }
    
    private void updateXYPositionOnListeners(){
        for (Object l: this.locationSystemListeners){
            locationSystemListener lis = (locationSystemListener)l;
            lis.updateXYPositionOfLunabotInCentimeters(
                    xPositionInArenaInCentimeters, 
                    yPositionInArenaInCentimeters,
                    this.headingAngleInRadians);
        }
    }
    
    private void updateListenersPositionUnknown(){
        for (Object l: this.locationSystemListeners){
            locationSystemListener lis = (locationSystemListener)l;
            lis.updateXYPositionOfLunabotInCentimeters(-1, -1, this.headingAngleInRadians);
        }
    }
    
    
    
    private void checkNorthSouthScannersForSwap(){
        if (this.northScanner.isScannerNorthSouthKnown()){
            if (this.northScanner.isSouthScanner()){
                this.swapScanners();
            }
        } else {
            if (this.southScanner.isScannerNorthSouthKnown()){
                if (this.southScanner.isNorthScanner()){
                    this.swapScanners();
                }
            }
        }
    }
    
    private void swapScanners(){
        wiiMoteServoScannerMasterController temp = this.northScanner;
        this.northScanner = this.southScanner;
        this.southScanner = temp;
    }
    
    
    private void singleLoopStep(){
        this.beaconState = !this.beaconState;
        if (this.beaconState){
            if (DEBUG){
                System.out.println("About to turn beacon on");
            }
            byte byteToSend = (byte)255;
            if (this.isBeaconLocated()){
                this.numBeaconCyclesWithBeaconNotSeen = 0;
                if (this.getXPositionInArenaInCentimeters() < 
                        this.xPositionInCMForHighPowerBeaconActivation){
                    byteToSend = (byte)(128+64);
                } else {
                    byteToSend = (byte)255;
                }
            } else {
                // beacon is not located
                this.numBeaconCyclesWithBeaconNotSeen++;
                byteToSend = (byte)(128+64);
                if (this.numBeaconCyclesWithBeaconNotSeen > 
                        this.numBeaconCyclesWithBeaconNotSeenForHighPowerFlip){
                    byteToSend = (byte)255;
                }
                if (this.numBeaconCyclesWithBeaconNotSeen > 
                        (2*this.numBeaconCyclesWithBeaconNotSeenForHighPowerFlip)){
                    this.numBeaconCyclesWithBeaconNotSeen = 0;
                }
            }
            this.networkClient.turnBeaconOn((byte) byteToSend);
            if (DEBUG){
                System.out.println("Beacon Turned On");
            }
            
        } else {
            this.networkClient.turnBeaconOff();
        }
        if (this.numIterations <49){
            this.checkNorthSouthScannersForSwap();
        }
        if (this.northScanner.isBeaconLocated() && this.southScanner.isBeaconLocated()){
            this.computePosition_XY_LawOfCosines();
            this.updateXYPositionOnListeners();
        } else {
            this.updateListenersPositionUnknown();
        }
    }
    
    public boolean isBeaconLocated(){
        if (this.northScanner.isBeaconLocated()){
            if (this.southScanner.isBeaconLocated()){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    
    private void computePosition_XY_LawOfCosines(){
        double angleA = this.northScanner.getAngleInRadians() + (Math.PI/2.0);
        double angleB = (Math.PI/2.0) - this.southScanner.getAngleInRadians();
        double angleC = Math.PI - angleA - angleB;
        
        double cLength = this.northScannerYCoordinateInCentimeters - this.southScannerYCoordinateInCentimeters;
        double aLength = cLength * ((Math.sin(angleA) / Math.sin(angleC)));    // changed from Math.sin(angleB) - check this to see if it works
        
        int xPos = (int) Math.round(aLength * Math.cos(this.southScanner.getAngleInRadians()));
        int yPos = (int) Math.round(aLength * Math.sin(this.southScanner.getAngleInRadians()));
        //this.xPositionInArenaInCentimeters = xPos;
        //this.yPositionInArenaInCentimeters = Math.round(this.southScannerYCoordinateInCentimeters) + yPos;
        
        int xPositionOfBeaconInArena = xPos;
        int yPositionOfBeaconInArena = Math.round(this.southScannerYCoordinateInCentimeters) + yPos;
        
        double thetaBetweenBeaconAndRobotCenter = this.beaconPositionOnLunabotTheta + this.headingAngleInRadians;
        
        this.xPositionInArenaInCentimeters = xPositionOfBeaconInArena + 
                (int)Math.round(this.beaconPositionOnLunabotInCentimeters_Radius * Math.cos(thetaBetweenBeaconAndRobotCenter));
        
        this.yPositionInArenaInCentimeters = yPositionOfBeaconInArena + 
                (int)Math.round(this.beaconPositionOnLunabotInCentimeters_Radius * Math.sin(thetaBetweenBeaconAndRobotCenter));
        
        // add in offset of scanner position in arena - scanners could be on front or back of the collection bin.
        this.xPositionInArenaInCentimeters += (int)Math.round(this.xPositionOfScannersInArenaInCentimeters);
        
        
        System.out.println("X: " + this.xPositionInArenaInCentimeters + " Y: " + this.yPositionInArenaInCentimeters);
        
    }
    
    private void normalizeHeadingAngle(){
        while(this.headingAngleInRadians < (-1f * (float)Math.PI)){
            this.headingAngleInRadians += (2f * (float)Math.PI);
        }
        
        while (this.headingAngleInRadians > (float)Math.PI){
            this.headingAngleInRadians -= (2f * (float)Math.PI);
        }
    }
    

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        while (true){
            long startTime = System.currentTimeMillis();
            
            this.singleLoopStep();
            if (this.numIterations < 50){
                this.numIterations++;
            }
            
            long timeForLoop = System.currentTimeMillis() - startTime;
            long sleepTime = Math.max(1, stepTimeForMainLoopInMillis-timeForLoop);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(LED_BeaconMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void beaconACKReceived(boolean LEDBeaconOn) {
        
    }
    
    private void setHeadingOffsetInRadians(){
        if (this.initialHeading.equals("E") 
                || this.initialHeading.equals("East") 
                || this.initialHeading.equals("e")
                || this.initialHeading.equals("east")
                || this.initialHeading.equals("-E") 
                || this.initialHeading.equals("-East") 
                || this.initialHeading.equals("-e")
                || this.initialHeading.equals("-east")){
            // we start out heading east - set 
            this.headingAngleOffsetInRadians = -1f * this.headingAngleInRadians;
        }
        
        if (this.initialHeading.equals("N") 
                || this.initialHeading.equals("North") 
                || this.initialHeading.equals("n")
                || this.initialHeading.equals("north")
                || this.initialHeading.equals("-N") 
                || this.initialHeading.equals("-North") 
                || this.initialHeading.equals("-n")
                || this.initialHeading.equals("-north")){
            // we start out heading east - set 
            this.headingAngleOffsetInRadians = (-1f * this.headingAngleInRadians) + (float)Math.PI/2f;
        }
        
        if (this.initialHeading.equals("W") 
                || this.initialHeading.equals("West") 
                || this.initialHeading.equals("w")
                || this.initialHeading.equals("west")
                || this.initialHeading.equals("-W") 
                || this.initialHeading.equals("-West") 
                || this.initialHeading.equals("-w")
                || this.initialHeading.equals("-west")){
            // we start out heading east - set 
            this.headingAngleOffsetInRadians = (-1f * this.headingAngleInRadians) + (float)Math.PI/1f;
        }
        
        if (this.initialHeading.equals("S") 
                || this.initialHeading.equals("South") 
                || this.initialHeading.equals("s")
                || this.initialHeading.equals("south")
                || this.initialHeading.equals("-S") 
                || this.initialHeading.equals("-South") 
                || this.initialHeading.equals("-s")
                || this.initialHeading.equals("-south")){
            // we start out heading east - set 
            this.headingAngleOffsetInRadians = (-1f * this.headingAngleInRadians) + (float)Math.PI*3f/2f;
        }
    }

    @Override
    public void updateCompassInformation(long headingFromGyro) {
        // FIX ME!!!!
        
        long gyroBias = 7712000;
        
        this.headingAngleInRadians = ((float)(headingFromGyro - gyroBias)) * ((float)Math.PI / (96400f * 2f));
        if (this.firstTimeCompassInfoReceive){
            this.setHeadingOffsetInRadians();
            this.firstTimeCompassInfoReceive = false;
        }
        
        this.headingAngleInRadians -= 0.02f;
        
        this.headingAngleInRadians += this.headingAngleOffsetInRadians;
        this.normalizeHeadingAngle();
        System.out.println("Compass: " + (headingFromGyro - gyroBias));
        
        //this.headingAngleInRadians += this.headingAngleOffsetInRadians;
    }
}
