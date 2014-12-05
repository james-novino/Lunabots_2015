/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

/**
 * This interface contains the state definitions for the autonomous system state machine
 * @author chalbers2
 */
public interface autoStateDefs {
    // autonomous system state machine definitions
    public final int INIT_STATE = 0;
    public final int WAIT_FOR_START_SIGNAL = 1;
    public final int START_SIGNAL_RECEIVED = 2;
    public final int DETERMINE_START_ROUTINE = 3;
    
    // once start signal received, drive out of start position
    public final int START_ROUTINE_NORTH_POSITION_NORTH_FACING = 4;
    public final int START_ROUTINE_NORTH_POSITION_EAST_FACING = 5;
    public final int START_ROUTINE_NORTH_POSITION_SOUTH_FACING = 6;
    public final int START_ROUTINE_NORTH_POSITION_WEST_FACING = 7;
    
    public final int START_ROUTINE_SOUTH_POSITION_NORTH_FACING = 8;
    public final int START_ROUTINE_SOUTH_POSITION_EAST_FACING = 9;
    public final int START_ROUTINE_SOUTH_POSITION_SOUTH_FACING = 10;
    public final int START_ROUTINE_SOUTH_POSITION_WEST_FACING = 11;
    
    
    public final int START_ROUTINE_NORTH_POSITION_EAST_FACING_step2 = 12;
    public final int START_ROUTINE_NORTH_POSITION_EAST_FACING_step3 = 13;
    
    public final int DRIVE_TO_DIG_START_TARGET = 14;
    public final int TURN_ROBOT_TO_DIG_HEADING_ANGLE = 15;
    public final int DRIVE_TO_DIG_FINISH_TARGET_WITH_DIG = 16;
    public final int DRIVE_TO_DIG_FINISH_TARGET_NO_DIG = 17;
    public final int DRIVE_TO_DUMP_TARGET = 18;
    
    public final int DUMP_MATERIAL = 19;
    
    public final int STOP_ALL = -1;
    
    public final int RESET = -2;
    
    
    
}
