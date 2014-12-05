/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiiMoteParticleFilter;

/**
 *
 * @author chalbers2
 */
public class IRBlob {
    
    private boolean blobDetected;
    private int xPosition;
    private int yPosition;
    private int size;
    private int numBlob;
    
    public IRBlob(int xPosition, int yPosition, int size, int numBlob){
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.size = size;
        this.numBlob = numBlob;
        if (this.size != 0){
            this.blobDetected = true;
        }
    }
    
    public IRBlob(){
        this.xPosition = 0;
        this.yPosition = 0;
        this.size = 0;
        this.numBlob = 0;
        this.blobDetected = false;
    }
    
    public void setUnInitialized(){
        this.xPosition = 0;
        this.yPosition = 0;
        this.size = 0;
        this.numBlob = 0;
        this.blobDetected = false;
    }
    
    public boolean isBlobSeen(){
        return this.blobDetected;
    }
    
    public int getXPosition(){
        return this.xPosition;
    }
    
    public int getYPosition(){
        return this.yPosition;
    }
    
    public int getSize(){
        return this.size;
    }
    
    public int getNumBlob(){
        return this.numBlob;
    }
    
    public void setXPosition(int xPosition){
        this.xPosition = xPosition;
    }
    
    public void setYPosition(int yPosition){
        this.yPosition = yPosition;
    }
    
    public void setSize(int size){
        this.size = size;
        if (this.size == 0){
            this.blobDetected = false;
        } else {
            this.blobDetected = true;
        }
    }
    
    public void setnumBlob(int numBlob){
        this.numBlob = numBlob;
    }
    
    @Override
    public String toString(){
        if (! this.blobDetected){
            return "Inactive IR Blob";
        } else {
            return "IR Blob: X = " + this.xPosition + ", Y = " + this.yPosition 
                + ", Size = " + this.size + ", Blob Number = " + this.numBlob;
        }
    }
    
}
