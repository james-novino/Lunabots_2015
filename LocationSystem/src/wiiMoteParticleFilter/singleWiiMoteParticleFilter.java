/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiiMoteParticleFilter;

import wiiMoteParticleFilter.IRBlob;
import wiiMoteParticleFilter.wiiMoteListener;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author chalbers2
 */
public class singleWiiMoteParticleFilter implements wiiMoteListener {
    
    public static final int NUM_PARTICLES_TO_RESPAWN = 70;
    public static final int NUM_PARTICLES_BY_DEFAULT = 100;
    public static final float CONFIDENCE_THRESHOLD = 0.7f;
    
    private Random randGen;
    
    private int numParticles;
    private IRBlob [] blobs;
    private boolean thisLEDState;
    private int numOn;
    private int numOff;
    
    private int thisAverageOnDistance;
    private int lastAverageOnDistance;
    
    private int firstBlobSeen;
    
    private int nullBlobsCount;
    
    private boolean lastIR_LED_State;
    
    private particle[] particles;
    private float      xVariance;
    private float      xStd;
    private float      yVariance;
    private float      yStd;
    private float      xMean;
    private float      yMean;
    private float      xMeanValid;
    private float      yMeanValid;
    
    private float      lastReportedAngleInDegrees;
    
    private float      meanMinDist;
    private float      confidenceP;
    
    private int        lastServoUpdateTime;
    private int        servoArg;
    private int        lastSeenServoArg;
    
    private boolean    scanningForward;
    private boolean    firstScanTime;
    
    public final int   numParticleFilterUpdatesPerServoMove = 10;
    public       int   numParticleFilterUpdatesSinceLastServoMove;
    public final int   minServoArg = 1;
    public final int   maxServoArg = 175;
    public final int   numMillisPerServoUpdate = 5;
    public final int[] servoScanArgs = {10, 30, 50, 70, 90, 110, 130, 157};
    private      int   servoScanArgCurrentIndex = 4;
    
    private      int   servoScanArgMaxIndex     = 7;
    private      int   servoScanArgMinIndex     = 0;
    
    public final int   minXPositionToMoveServo  = 200;
    public final int   maxXPositionToMoveServo  = 823;
    public final float degreesPerServoArgIncrement = 180f/162f;//1.084337349f; // 180/166
    public final float degreesPerWiiMotePixel      =  42.4f/1023f;
    
    
    public int getNumberOfParticlesToRespawn(){
        return NUM_PARTICLES_TO_RESPAWN;
    }
    
    
    
    public singleWiiMoteParticleFilter(){
        this.numParticles = NUM_PARTICLES_BY_DEFAULT;
        this.particles = new particle[this.numParticles];
        for (int i = 0; i<this.numParticles; i++){
            int x = (int)(Math.random() * 1023);
            int y = (int)(Math.random() * 767);
            int p = 0;
            this.particles[i] = new particle(x,y,p);
        }
        this.numOff = 0;
        this.numOn  = 0;
        this.xMean  = 511;
        this.yMean  = 360;
        this.xMeanValid = 511;
        this.yMeanValid = 360;
        this.lastIR_LED_State = false;
        this.thisLEDState     = false;
        this.nullBlobsCount = 0;
        this.randGen = new Random();
        this.lastServoUpdateTime = 0;
        this.servoArg = 84;
        this.lastSeenServoArg = 84;
        this.numParticleFilterUpdatesSinceLastServoMove = 0;
        this.scanningForward = true;
        this.xStd = 300f;
        this.yStd = 300f;
        this.firstScanTime = false;
        this.lastReportedAngleInDegrees = 0f;
    }
    
    private void reInitializeParticles(){
        if (this.particles != null){
            for (int i = 0; i<this.numParticles; i++){
                int x = (int)(Math.random() * 1023);
                int y = (int)(Math.random() * 767);
                int p = 0;
                this.particles[i] = new particle(x,y,p);
            }
        }
    }
    
    
    private boolean shouldServoMove(){
        if (this.numParticleFilterUpdatesSinceLastServoMove < 
                this.numParticleFilterUpdatesPerServoMove){
            return false;
        }
        if ((System.currentTimeMillis() % 86000000) - this.lastServoUpdateTime
                < this.numMillisPerServoUpdate){
            return false;
        }
        if (this.isBeaconLocated()){
            for (int i = 0; i< this.servoScanArgs.length; i++){
                if (this.servoArg <= this.servoScanArgs[i]){
                    this.servoScanArgCurrentIndex = i;
                    this.servoScanArgMinIndex = Math.max(i-1, 0);
                    this.servoScanArgMaxIndex = Math.min(i+1, this.servoScanArgs.length-1);
                    i = this.servoScanArgs.length + 2;
                }
            }
            if (this.getXMean() > this.minXPositionToMoveServo || 
                    this.servoArg == this.minServoArg){
                if (this.getXMean() < this.maxXPositionToMoveServo ||
                        this.servoArg == this.maxServoArg){
                    return false;
                }
            }
        }
        this.reInitializeParticles();
        this.lastServoUpdateTime = (int)(System.currentTimeMillis() % 86000000);
        this.numParticleFilterUpdatesSinceLastServoMove = 0;
        return true;
    }
    
    private void computeNextServoArgument(){
        if (this.isBeaconLocated()){
            this.moveServoToLocatedBeacon();
            this.firstScanTime = true;
        } else {
            this.scanToFind();
            this.firstScanTime = false;
        }
    }
    
    private void moveServoToLocatedBeacon(){
        float degreesToOffset = (this.getXMean()-511) * 
                this.degreesPerWiiMotePixel;
        float changeOfServoArg = degreesToOffset / 
                this.degreesPerServoArgIncrement;
        this.servoArg = this.servoArg + Math.round(changeOfServoArg);
    }
    
    private void scanToFind(){
        if (this.firstScanTime){
            // center the servo around the last known location of the beacon
            float degreesToOffset = (this.getXMean()-511) * 
                this.degreesPerWiiMotePixel;
            float changeOfServoArg = degreesToOffset / 
                this.degreesPerServoArgIncrement;
            this.servoArg = this.servoArg + Math.round(changeOfServoArg);
            
            
            
            
            this.firstScanTime = false;
            
            for (int i = 0; i< this.servoScanArgs.length; i++){
                if (this.servoArg <= this.servoScanArgs[i]){
                    this.servoScanArgCurrentIndex = i;
                    this.servoScanArgMinIndex = Math.max(i-1, 0);
                    this.servoScanArgMaxIndex = Math.min(i+1, this.servoScanArgs.length-1);
                    i = this.servoScanArgs.length + 2;
                }
            }
            this.scanningForward = true;
            
        } else {
            // scan to find the beacon
            if (this.scanningForward){
                this.servoScanArgCurrentIndex++;
                if (this.servoScanArgCurrentIndex >= this.servoScanArgMaxIndex){
                    this.scanningForward = false;
                    this.servoScanArgMaxIndex = 
                            Math.min(this.servoScanArgs.length-1, 
                            this.servoScanArgMaxIndex + 1);
                }
                this.servoArg = this.servoScanArgs[this.servoScanArgCurrentIndex];
            } else {
                this.servoScanArgCurrentIndex--;
                if (this.servoScanArgCurrentIndex <= this.servoScanArgMinIndex){
                    this.scanningForward = true;
                    this.servoScanArgMinIndex = Math.max(0, 
                            this.servoScanArgMinIndex-1);
                }
                this.servoArg = this.servoScanArgs[this.servoScanArgCurrentIndex];
            }
        }
    }
    
    public int getNextServoArgument(){
        if (this.shouldServoMove()){
            this.computeNextServoArgument();
        }
        if (this.servoArg <= this.minServoArg){
            this.servoArg = this.minServoArg;
        } else {
            if (this.servoArg >= this.maxServoArg){
                this.servoArg = this.maxServoArg;
            }
        }
        return this.servoArg;
    }
    
    /**
     * This method returns the last computed servo argument as an int without updating the argument.
     * @return Returns current servo argument as an int without updating
     */
    public int getCurrentServoArgument_DoNotUpdateServoArgument(){
        return this.servoArg;
    }
    
    
    public float getXMean(){
        return this.xMeanValid;
    }
    
    public float getYMean(){
        return this.yMeanValid;
    }
    
    public float getXVariance(){
        return this.xVariance;
    }
    
    public float getYVariance(){
        return this.yVariance;
    }
    
    public int getNumParticles(){
        return this.numParticles;
    }
    
    public particle[] getParticles(){
        return this.particles;
    }
    
    private void computeProb(){
        if (this.firstBlobSeen == -1){
            this.nullBlobsCount++;
        } else {
            this.nullBlobsCount = 0;
            
            for (int b = 0; b<4; b++){
                if (this.blobs[b].isBlobSeen()){
                    // get X and Y position of the current Blob
                    int thisXb = this.blobs[b].getXPosition();
                    int thisYb = this.blobs[b].getYPosition();
                    
                    // step through all particles
                    for (int p = 0; p<this.numParticles; p++){
                        
                        
                        // get X and Y position of the particle
                        int thisXp = this.particles[p].getXPosition();
                        int thisYp = this.particles[p].getYPosition();

                        int xDiff = thisXb - thisXp;
                        int yDiff = thisYb - thisYp;

                        int thisD = (int)Math.sqrt((xDiff*xDiff) + (yDiff*yDiff));
                        
                        if (!this.particles[p].isMinDistanceSet()){
                            if (this.particles[p].isLessThanMinDistance(thisD)){
                                this.particles[p].setMinDistance(thisD);
                            }
                            
                        } else {
                            this.particles[p].setMinDistance(thisD);
                        }
                        
                        if (this.thisLEDState){
                            this.particles[p].addToPOn(thisD);
                            
                        } else {
                            this.particles[p].addToPOff(thisD);
                        }
                    } 
                }
            }
            
        }
        
        if (this.nullBlobsCount >= 50){
            this.reInitializeParticles();
            this.nullBlobsCount = 0;
        }
    }
    
    public boolean isBeaconLocated(){
        if (this.numParticleFilterUpdatesSinceLastServoMove >= 
                this.numParticleFilterUpdatesPerServoMove && 
                this.isBeaconLocatedBasedOnConfidence()){
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isBeaconLocatedBasedOnConfidence(){
        this.computeConfidenceP();
        if (this.confidenceP > CONFIDENCE_THRESHOLD){
            return true;
        } else {
            return false;
        }
    }
    
    private void computeMeanandVariance(){
        float x = 0;
        float x2 = 0;
        float y = 0;
        float y2 = 0;
        
        float thisX = 0;
        float thisY = 0;
        for (int i = 0; i<this.numParticles; i++){
            thisX = (float)this.particles[i].getXPosition();
            thisY = (float)this.particles[i].getYPosition();
            x += thisX;
            y += thisY;
            x2 += (thisX*thisX / (float)this.numParticles);
            y2 += (thisY*thisY / (float)this.numParticles);
        }
        x = x / (float)this.numParticles;
        y = y / (float)this.numParticles;
        
        
        
        this.xMean = x;
        this.yMean = y;
        
        this.xVariance = x2 - (x*x);
        this.yVariance = y2 - (y*y);
        
        this.xStd = (float)Math.sqrt(this.xVariance);
        this.yStd = (float)Math.sqrt(this.yVariance);
        
        if (this.xStd <= 15.0f || this.xStd != this.xStd){
            this.xStd = 15.01f;
        }
        if (this.yStd <= 15.0f || this.yStd != this.yStd){
            this.yStd = 15.01f;
        }
        
    }
    
    
    private void computeMeanandVarianceOfGoodParticles(){
        float x = 0;
        float x2 = 0;
        float y = 0;
        float y2 = 0;
        
        float thisX = 0;
        float thisY = 0;
        for (int i = NUM_PARTICLES_TO_RESPAWN; i<this.numParticles; i++){
            thisX = (float)this.particles[i].getXPosition();
            thisY = (float)this.particles[i].getYPosition();
            x += thisX;
            y += thisY;
            x2 += (thisX*thisX / (float)(this.numParticles - NUM_PARTICLES_TO_RESPAWN));
            y2 += (thisY*thisY / (float)(this.numParticles - NUM_PARTICLES_TO_RESPAWN));
        }
        x = x / (float)(this.numParticles - NUM_PARTICLES_TO_RESPAWN);
        y = y / (float)(this.numParticles - NUM_PARTICLES_TO_RESPAWN);
        
        
        
        this.xMean = x;
        this.yMean = y;
        
        this.xVariance = x2 - (x*x);
        this.yVariance = y2 - (y*y);
        
        this.xStd = (float)Math.sqrt(this.xVariance);
        this.yStd = (float)Math.sqrt(this.yVariance);
        
        if (this.xStd <= 15.0f || this.xStd != this.xStd){
            this.xStd = 15.01f;
        }
        if (this.yStd <= 15.0f || this.yStd != this.yStd){
            this.yStd = 15.01f;
        }
        
    }
    
    private void setAllPToZero(){
        for (int i = 0; i< this.numParticles; i++){
            this.particles[i].resetP();
        }
    }
    
    private void resetAllMinDistances(){
        for (int i = 0; i< this.numParticles; i++){
            this.particles[i].resetMinDistance();
        }
    }
    
    private void resetAllMinDistancesAndSetAllPToZero(){
        for (int i = 0; i< this.numParticles; i++){
            this.particles[i].resetMinDistance();
            this.particles[i].resetP();
        }
    }
    
    private void computeMeanMinimumDistance(){
        this.meanMinDist = 0;
        for (int i = 0; i< this.numParticles; i++){
            this.meanMinDist += (float)this.particles[i].getMinDistance();
        }
        
        this.meanMinDist = this.meanMinDist / (float)this.numParticles;
    }
    
    
    
    private void updateParticles(){
        this.computeProb();
        
        if (this.lastIR_LED_State && !this.thisLEDState){
            
            
            
            // respawn particles
            //this.computeMeanandVariance();
            java.util.Arrays.sort(this.particles);
            this.computeMeanandVarianceOfGoodParticles();
            this.computeMeanMinimumDistance();
            
            //System.out.println("Array Sorted");
            for (int i = 0;
                    i < NUM_PARTICLES_TO_RESPAWN; i++){
                
                
                
                float xStdMult = (float)Math.sqrt(this.xStd * this.meanMinDist);
                float yStdMult = (float)Math.sqrt(this.yStd * this.meanMinDist);
                
                
                int x = (int)((this.randGen.nextGaussian() * xStdMult) + this.xMean);
                int y = (int)((this.randGen.nextGaussian() * yStdMult) + this.yMean);
                
                
                if (x > 1023 || x < 0 || xStdMult == 0){
                    x = (int)Math.round(Math.random()*1023);
                }
                
                if (y > 1023 || y < 0 || yStdMult == 0){
                    y = (int)Math.round(Math.random()*767);
                }
                
                this.particles[i].setXPosition(x);
                this.particles[i].setYPosition(y);
                
            }
            //this.setAllPToZero();
            //this.resetAllMinDistances();
            this.resetAllMinDistancesAndSetAllPToZero();
            this.numParticleFilterUpdatesSinceLastServoMove++;
            this.numOff = 0;
            this.numOn = 0;
            //this.computeMeanandVariance();
        }
        if (this.numParticleFilterUpdatesSinceLastServoMove >= 
                this.numParticleFilterUpdatesPerServoMove && 
                this.isBeaconLocatedBasedOnConfidence()){
            this.xMeanValid = this.xMean;
            this.yMeanValid = this.yMean;
        }
    }
    
    
    
    public void normalizeParticlesForP(){
        for (int i = 0; i< this.numParticles; i++){
            this.particles[i].dividePOff(this.numOff);
            this.particles[i].dividePOn(this.numOn);
        }
    }
    
    public void updateNumOnNumOff(){
        if (this.thisLEDState){
            this.numOn++;
        } else {
            this.numOff++;
        }
    }
    
    private void computeConfidenceP(){
        this.confidenceP = ((1f - (this.xStd / 400f)) * (1f - (this.yStd / 300f)));
    }
    
    public float getConfidencePercentage(){
        
        //float yRet = 1 - ( (-1 / 300) * this.yStd);
        //return (xRet * yRet) * 100;
        this.computeConfidenceP();
        return this.confidenceP;
    }
    
    public float getAngleInDegrees(){
        if (this.isBeaconLocated()){
            this.lastReportedAngleInDegrees = (180f/162f * (this.servoArg - 82-3)) + ((this.getXMean()-511f) * this.degreesPerWiiMotePixel);
        }
        return this.lastReportedAngleInDegrees;
    }
    
    public float getAngleInRadians(){
        return this.getAngleInDegrees() * (float)Math.PI / 180f;
    }
    
    public boolean isServoCurrentlyMoving(){
        if (this.numParticleFilterUpdatesSinceLastServoMove >= 
                this.numParticleFilterUpdatesPerServoMove){
            return true;
        } else {
            return false;
        }
    }
    
    
    
    public static int numOfFirstBlobSeen(IRBlob [] b){
        if (b == null){
            return -1;
        }
        for (int i = 0; i<4; i++){
            if (b[i].isBlobSeen()){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void blobUpdate(IRBlob[] blobs, boolean LED_on) {
        this.blobs = blobs;
        this.firstBlobSeen = numOfFirstBlobSeen(blobs);
        this.lastIR_LED_State = this.thisLEDState;
        this.thisLEDState = LED_on;
        this.updateNumOnNumOff();
        this.updateParticles();
        //System.out.println("blobsUpdated Called" + this.thisLEDState + this.lastIR_LED_State);
        //System.out.println("XStd: " + this.xStd + ", YStd: " + this.yStd);
        //System.out.println("Xmean: " + this.xMean + ", YMean: " + this.yMean);
        /*
        if (this.isBeaconLocated()){
            System.out.println("ServoArg: " + this.servoArg + " WiiMote X Position: " + this.getXMean());
            float angle = (180f/162f * (this.servoArg - 82-3)) + ((this.getXMean()-511f) * this.degreesPerWiiMotePixel);
            System.out.println("Angle: " + angle);
        }
        * */
        
    }
    
}
