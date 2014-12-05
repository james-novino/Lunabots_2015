/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lunarenagui;

import java.util.LinkedList;

/**
 *
 * @author chalbers2
 */
public interface autonomousSystemListener {
    /**
     * This function should be called in order to update the drawing of the 
     * @param lunabotPositionInCentimeters_X - Lunabot X Position in Centimeters Measured from west (collection bin) wall
     * @param lunabotPositionInCentimeters_Y - Lunabot Y Position in Centimeters Measured from south (bottom) wall
     * @param arenaObstacleGrid - LinkedList containing length-4 short arrays as follows: {xPositionOfUpperLeftCornerInArenaInCentimeters, yPositionOfUpperLeftCornerInCentimeters, widthInCentimeters, heightInCentimeters}
     * @param RobotPath - LinkedList containing length-2 short arrays representing the X and Y locations of the predicted lunabot path in centimeters in the arena coordinate system
     * @param headingAngleInRadians - heading angle in Radians - 0 radians corresponds to heading directly east (dig wall - just like a Cartesian coordinate system).
     * @param currentAutonomousSystemState - String showing the current state of autonomous system FSM - VARIABLE LENGTH
     * 
     */
    public void updateDataFromAutonomousSystem(short lunabotPositionInCentimeters_X,
                              short lunabotPositionInCentimeters_Y,
                              LinkedList arenaObstacleGrid,
                              LinkedList RobotPath,
                              float headingAngleInRadians,
                              String currentAutonomousSystemState);
}
