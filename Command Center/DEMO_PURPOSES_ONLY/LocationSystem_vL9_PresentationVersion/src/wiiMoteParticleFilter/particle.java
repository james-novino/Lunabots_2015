/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiiMoteParticleFilter;

/**
 *
 * @author chalbers2
 */
public class particle implements Comparable {
    
    private int xPosition;
    private int yPosition;
    
    private int pOn;
    private int pOff;
    
    private int pTotal;
    
    private int minDistance;
    
    public particle(){
        this.xPosition = 0;
        this.yPosition = 0;
        this.pOff = 0;
        this.pOn = 0;
        this.pTotal     = 0;
        this.minDistance = -1;
    }
    
    public particle(int x, int y, int p){
        this.xPosition = x;
        this.yPosition = y;
        this.pOff = 0;
        this.pOn = 0;
        this.pTotal = p;
        this.minDistance = -1;
    }
    
    public boolean isMinDistanceSet(){
        if (this.minDistance < 0){
            return false;
        } else {
            return true;
        }
    }
    
    public void resetMinDistance(){
        this.minDistance = -1;
    }
    
    public boolean isLessThanMinDistance(int nextD){
        if (nextD < this.minDistance || this.minDistance < 0){
            return true;
        } else {
            return false;
        }
    }
    
    public int getMinDistance(){
        return this.minDistance;
    }
    
    public void setMinDistance(int minDistance){
        if (minDistance >= 0){
            this.minDistance = minDistance;
        } else {
            this.minDistance = -1;
        }
    }
    
    public void setMinDistance(float minDistance){
        if (minDistance >= 0){
            this.minDistance = (int)Math.round(minDistance);
        } else {
            this.minDistance = -1;
        }
    }
    
    public void setMinDistance(double minDistance){
        if (minDistance >= 0){
            this.minDistance = (int)Math.round(minDistance);
        } else {
            this.minDistance = -1;
        }
    }
    
    public int getXPosition(){
        return this.xPosition;
    }
    
    public int getYPosition(){
        return this.yPosition;
    }
    
    public int getPOn(){
        return this.pOn;
    }
    
    public int getPOff(){
        return this.pOff;
    }
    
    public void setPOn(int pOn){
        this.pOn = pOn;
        this.pTotal = this.pOn - this.pOff;
    }
    
    public void setPOff(int pOff){
        this.pOff = pOff;
        this.pTotal = this.pOn - this.pOff;
    }
    
    public void resetP(){
        this.pOff = 0;
        this.pOn = 0;
        this.pTotal = 0;
    }
    
    public int getPTotal(){
        return this.pTotal;
    }
    
    public void setXPosition(int x){
        this.xPosition = x;
    }
    
    public void setYPosition(int y){
        this.yPosition = y;
    }
    
    public void dividePOn(int divider){
        this.pOn = this.pOn / divider;
        this.pTotal = this.pOn - this.pOff;
    }
    
    public void dividePOff(int divider){
        this.pOff = this.pOff / divider;
        this.pTotal = this.pOn - this.pOff;
    }
    
    public void addToPOn(int pToAdd){
        this.pOn += pToAdd;
        this.pTotal = this.pOn - this.pOff;
    }
    
    public void addToPOff(int pToSub){
        this.pOff += pToSub;
        this.pTotal = this.pOn - this.pOff;
    }
    
    
    
    @Override
    public String toString(){
        return "Particle with X = " + this.xPosition + ", Y = " 
                + this.yPosition + ", P = " + this.pTotal;
    }

    @Override
    public int compareTo(Object t) {
        particle tp = (particle) t;
        if (this.pTotal < tp.getPTotal()){
            return 1;
        } else if (this.pTotal == tp.getPTotal()) {
            return 0;
        } else {
            return -1;
        }
    }
    
}
