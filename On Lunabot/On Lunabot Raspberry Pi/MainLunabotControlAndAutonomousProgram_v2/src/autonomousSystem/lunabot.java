/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

import org.newdawn.slick.util.pathfinding.Mover;

/**
 *
 * @author chalbers2
 */
public class lunabot implements Mover {
    private int xPositionInArenaInCentimeters;
    private int yPositionInArenaInCentimeters;
    private float headingAngleInRadians;
    private boolean isLunabotPositionKnown;
    private autonomousSystem autonomousSystemController;
    
    
    /**
     * Constructor
     */
    public lunabot(){
        this.xPositionInArenaInCentimeters = 75;
        this.yPositionInArenaInCentimeters = 197;
        this.headingAngleInRadians = 0f;
        this.isLunabotPositionKnown = false;
    }
    
    /**
     * This method returns a boolean saying as to whether or not the autonomous system controller has been set
     * @return - true if auto system controller has been set. False if not.
     */
    public boolean isAutonomousSystemControllerSet(){
        if (this.autonomousSystemController != null){
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * This method sets the autonomous system controller such that the lunArenaObstacleGrid can send messages to the controller
     * @param autoSystem - Main autonomousSystemController 
     */
    public void setAutonomousSystemController(autonomousSystem autoSystem){
        this.autonomousSystemController = autoSystem;
    }
    
    /**
     * This method will return the currently set autonomousSystemController
     * @return - autonomous system controller set for the lunArenaObstacleGrid
     */
    public autonomousSystem getAutonomousSystemController(){
        return this.autonomousSystemController;
    }
    
    public boolean isLunabotPositionKnown(){
        return this.isLunabotPositionKnown;
    }
    
    /**
     * This method sets the X and Y position of the lunabot in centimeters in the arena
     * @param xCM - X position in Centimeters of center of lunabot in arena
     * @param yCM - Y position in centimeters of center of lunabot in arena
     */
    public void setLunabotXAndYPositionInCentimeters(int xCM, int yCM){
        
        if (xCM < 0 || 
                yCM < 0){
            this.isLunabotPositionKnown = false;
        } else {
            this.isLunabotPositionKnown = true;
            this.xPositionInArenaInCentimeters = xCM;
            this.yPositionInArenaInCentimeters = yCM;
        }
        
    }
    
    /**
     * This method sets the lunabot heading in radians
     * @param headingAngleRad 
     */
    public void setLunabotHeadingAngleInRadians(float headingAngleRad){
        this.headingAngleInRadians = headingAngleRad;
    }
    
    /**
     * This method returns the lunabot X position in the arena in centimeters
     * @return - X position of lunabot in arena in centimeters
     */
    public int getLunabotXPositionInArenaInCentimeters(){
        return this.xPositionInArenaInCentimeters;
    }
    
    /**
     * This method returns the lunabot Y position in the arena in centimeters
     * @return - Y position of lunabot in arena in centimeters
     */
    public int getLunabotYPositionInArenaInCentimeters(){
        return this.yPositionInArenaInCentimeters;
    }
    
    /**
     * This method returns the lunabot heading angle in radians.
     * @return - Float - heading angle in radians
     */
    public float getLunabotHeadingAngleInRadians(){
        return this.headingAngleInRadians;
    }
}
