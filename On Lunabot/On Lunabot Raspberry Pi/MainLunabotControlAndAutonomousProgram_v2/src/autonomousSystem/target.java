/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

/**
 *
 * @author chalbers2
 */
public class target {
    
    
    
    
    public int x;
    public int y;
    public boolean driveInReverse;
    public float speedMultiplier;
    public boolean shouldWeArriveAtGivenAngle;
    private float angleInRadiansAtFinish;
    
    public target(){
        x = 0;
        y = 0;
        driveInReverse = false;
        speedMultiplier = 1.0f;
        this.shouldWeArriveAtGivenAngle = false;
        this.angleInRadiansAtFinish = 0.0f;
    }
    
    public float getAngleToArriveAtInRadians(){
        return this.angleInRadiansAtFinish;
    }
    
    public void setAngleToArriveAtInRadians(float angleToArriveAtInRadians){
        if (angleToArriveAtInRadians < (float)Math.PI*-1f || angleToArriveAtInRadians > (float)Math.PI){
            this.shouldWeArriveAtGivenAngle = false;
            this.angleInRadiansAtFinish = 0f;
        } else {
            this.shouldWeArriveAtGivenAngle = true;
            this.angleInRadiansAtFinish = angleToArriveAtInRadians;
        }
    }
    
    public void setAngleToArriveAtInDegrees(int angleToArriveInDegrees){
        if (angleToArriveInDegrees < 0 || angleToArriveInDegrees > 359){
            this.shouldWeArriveAtGivenAngle = false;
            this.angleInRadiansAtFinish = 0f;
        } else {
            this.shouldWeArriveAtGivenAngle = true;
            this.angleInRadiansAtFinish = (float)angleToArriveInDegrees * ((float)Math.PI/180.0f);
        }
    }
    
    
}
