/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

/**
 * This interface contains 2-dimensional int arrays in the following form:
 * 
 * 
 * int [][] target = {{x1, y1, 1_if_reverse_0_if_forward, speed_1_to_127, headingAngleAtTarget}, 
 *                    {x2, y2, reverse_not_forward, speed_1_to_127, headingAngleAtTarget}
 *                     , ... , 
 *                    {xn, yn, reverse_not_forward, speed_1_to_127, headingAngleAtTarget}};
 * 
 *                  xn, yn are in centimeters
 *                  1_if_reverse_0_if_forward - set to 1 if we should drive in reverse to get there, 0 if we should drive forward
 *                  speed_1_to_127 - this is the speed multiplier, 1 is slowest speed, 127 is highest speed
 *                  headingAngleAtTarget - heading angle once we arrive at the target - between 0 and 359 degrees any number outside of this range means that we don't care about the heading angle when we arrive at the target
 * 
 * calling target.length will give the number of optional sequences
 * calling target[i].length will give the number of targets in the sequence
 * 
 * 
 * The length of the second dimension should always be 4
 * 
 * @author chalbers2
 */
public interface targetDefs {
    
    // this represents the speed of the robot when digging on a 1-127 scale
    public final int DIG_SPEED = 120;
    public final int TRAVEL_ACROSS_OBSTACLE_FIELD_SPEED = 120;
    
    // after testing, the best x coordinates were 635, 525, 53.
    
    
    public final int [] DIG_START_TARGET = {635, 210, 0, TRAVEL_ACROSS_OBSTACLE_FIELD_SPEED, -1000};
    public final int [] DIG_FINISH_TARGET = {525, 210, 0, TRAVEL_ACROSS_OBSTACLE_FIELD_SPEED, -1000};
    public final int [] DUMP_TARGET = {53, 210, 0, TRAVEL_ACROSS_OBSTACLE_FIELD_SPEED, 0};
    
    // This 2-D array represents the dig finish targets for digging in the Northern part of the dig area
    public final int [][] DIG_FINISH_TARGETS_POSITIVE_ANGLES = 
       {{638, 194, 0, DIG_SPEED, -1000}, 
        {638, 204, 0, DIG_SPEED, -1000}, 
        {638, 214, 0, DIG_SPEED, -1000},
        {638, 224, 0, DIG_SPEED, -1000},
        {638, 234, 0, DIG_SPEED, -1000}, 
        {638, 244, 0, DIG_SPEED, -1000}, 
        {638, 254, 0, DIG_SPEED, -1000},
        {638, 264, 0, DIG_SPEED, -1000},
        {638, 274, 0, DIG_SPEED, -1000}, 
        {638, 284, 0, DIG_SPEED, -1000}
       };
    
    // This 2-D array represents the dig finish targets for digging in the Southern part of the dig area
    public final int [][] DIG_FINISH_TARGETS_NEGATIVE_ANGLES = 
       {{638, 184, 0, DIG_SPEED, -1000}, 
        {638, 174, 0, DIG_SPEED, -1000}, 
        {638, 164, 0, DIG_SPEED, -1000},
        {638, 154, 0, DIG_SPEED, -1000},
        {638, 144, 0, DIG_SPEED, -1000}, 
        {638, 134, 0, DIG_SPEED, -1000}, 
        {638, 124, 0, DIG_SPEED, -1000},
        {638, 114, 0, DIG_SPEED, -1000},
        {638, 104, 0, DIG_SPEED, -1000}
       };
    
    
    
    
    
                                            
}
