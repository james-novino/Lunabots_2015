/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

/**
 *
 * @author chalbers2
 */
public class motorSpeedCalculator {
    
    public static final boolean DEBUG = false;
    public static final float thetaLimitToTurnInPlaceInRadians = (float)Math.PI * 25.0f/180.0f;
    public static final float thetaLimitToTurnAtAllInRadians   = (float)Math.PI * 2.0f/180.0f;
    
    
    /**
     * This method will get the next output for a given input X from a line going through two specified points
     * 
     * @param x1 - x coordinate of point 1
     * @param y1 - y coordinate of point 1
     * @param x2 - x coordinate of point 2 
     * @param y2 - y coordinate of point 2
     * @param x  - new X input to calculate the output for
     * @return   - output for given X input
     */
    public static float pointSlopeLineCalc(
            float x1, 
            float y1, 
            float x2,
            float y2, 
            float x){

        return ((y2-y1)/(x2-x1)*(x-x1)) + y1;
    }
    
    /**
     * This method will return the drive motor speeds as an array of bytes
     * 
     * 
     * 
     * @param shouldLunabotMove - set to true if lunabot should move - if false, method will output zeros
     * @param doWeCareIfRobotMovesInForwardOrReverse - set to true if the robot has to move in forward or reverse - if set to false, robot will move in most convenient direction
     * @param driveInReverse - set to true to drive in reverse, set false to drive forward
     * @param motorSpeedMultiplier - motor speed multiplier between 0 and 1
     * @param xPositionOfLunabotInArenaInCentimeters - current x position of lunabot in arena in centimeters
     * @param yPositionOfLunabotInArenaInCentimeters - current y position of lunabot in arena in centimeters
     * @param lunabotCurrentHeadingAngleInRadians    - current heading angle of lunabot in arena in radians
     * @param xPositionOfTargetToMoveToInArenaInCentimeters - x position of target spot in arena in centimeters
     * @param yPositionOfTargetToMoveToInArenaInCentimeters - y position of target spot in arena in centimeters
     * @return  - Array of bytes - return[0] = leftMotorByte, return[1] = rightMotorByte
     */
    public static mainDriveMotorFloatStructClass getLeft_RightDriveMotorBytes(
            boolean shouldLunabotMove,
            boolean doWeCareIfRobotMovesInForwardOrReverse,
            boolean driveInReverse,
            float   motorSpeedMultiplier,
            int     xPositionOfLunabotInArenaInCentimeters,
            int     yPositionOfLunabotInArenaInCentimeters,
            float   lunabotCurrentHeadingAngleInRadians,
            int     xPositionOfTargetToMoveToInArenaInCentimeters,
            int     yPositionOfTargetToMoveToInArenaInCentimeters
             ){
        mainDriveMotorFloatStructClass retval = new mainDriveMotorFloatStructClass();
        
        
        float leftMotorSpeed = 0.0f;
        float rightMotorSpeed = 0.0f;
        
        float motorSpeedMultToUse = Math.abs(motorSpeedMultiplier);
        
        if (motorSpeedMultToUse > 1f){
            motorSpeedMultToUse = 1f;
        }
        if (motorSpeedMultToUse < 0f){
            motorSpeedMultToUse = 0f;
        }
        
        
        if (shouldLunabotMove){
            int xDiff = xPositionOfTargetToMoveToInArenaInCentimeters - 
                    xPositionOfLunabotInArenaInCentimeters;
            int yDiff = yPositionOfTargetToMoveToInArenaInCentimeters - 
                    yPositionOfLunabotInArenaInCentimeters;
            
            // compute the theta diff
            float requiredHeadingAngle = (float)Math.atan2((float)yDiff, (float)xDiff);
            float thetaDiff = requiredHeadingAngle - lunabotCurrentHeadingAngleInRadians;
            while (thetaDiff <= (float)Math.PI*-1.0f){
                thetaDiff = thetaDiff + ((float)Math.PI*2.0f);
            }
            while (thetaDiff > (float)Math.PI){
                thetaDiff = thetaDiff - ((float)Math.PI*2.0f);
            }
            
            boolean driveLunabotInReverse = false;
            
            if (doWeCareIfRobotMovesInForwardOrReverse){
                // do whatever is specified by the argument the function is called with
                driveLunabotInReverse = driveInReverse;
            } else {
                if (thetaDiff > (float)Math.PI/2.0f || thetaDiff < (float)Math.PI/-2.0f){
                    // theta diff is greater than pi/2 or less than -pi/2 - therefore, we should drive in reverse
                    driveLunabotInReverse = true;
                } else {
                    // theta diff is between -pi/2 and + pi/2 - therefore, we should drive forward
                    driveLunabotInReverse = false;
                }
            }
            
            if (driveLunabotInReverse){
                // we are driving in reverse
                
                requiredHeadingAngle = requiredHeadingAngle + (float)Math.PI;
                thetaDiff = requiredHeadingAngle - lunabotCurrentHeadingAngleInRadians;
                while (thetaDiff <= (float)Math.PI*-1.0f){
                    thetaDiff = thetaDiff + ((float)Math.PI*2.0f);
                }
                while (thetaDiff > (float)Math.PI){
                    thetaDiff = thetaDiff - ((float)Math.PI*2.0f);
                }
                if (DEBUG){
                    System.out.println("Driving in reverse.");
                    System.out.println("thetaDiff In Degrees: " + (thetaDiff * 180.0f/(float)Math.PI));
                }
                if (thetaDiff < 0.0f){
                    // we need to turn right
                    rightMotorSpeed = -1.0f;
                    if (thetaDiff >= thetaLimitToTurnAtAllInRadians* -1.0f){
                        // we are close enough, we don't need to turn
                        leftMotorSpeed = -1.0f;
                    } else if (thetaDiff < thetaLimitToTurnAtAllInRadians* -1.0f &&
                            thetaDiff >= thetaLimitToTurnInPlaceInRadians * -1.0f){
                        
                        // we need to turn a variable amount
                        leftMotorSpeed = pointSlopeLineCalc(-1f*thetaLimitToTurnAtAllInRadians, 
                                                            -1.0f, 
                                                            -1f*thetaLimitToTurnInPlaceInRadians,
                                                            1.0f, 
                                                            thetaDiff);
                    } else {
                        // we should be turning in place full steam
                        leftMotorSpeed = 1.0f;
                    }
                } else {
                    // we need to turn left
                    leftMotorSpeed = -1.0f;
                    if (thetaDiff <= thetaLimitToTurnAtAllInRadians){
                        // we don't need to turn at all
                        rightMotorSpeed = -1.0f;
                    } else if (thetaDiff > thetaLimitToTurnAtAllInRadians &&
                            thetaDiff <= thetaLimitToTurnInPlaceInRadians){
                        // we should turn a variable amount
                        rightMotorSpeed = pointSlopeLineCalc(thetaLimitToTurnAtAllInRadians, 
                                                            -1.0f, 
                                                            thetaLimitToTurnInPlaceInRadians,
                                                            1.0f, 
                                                            thetaDiff);
                    } else {
                        // we need to turn in place completely
                        rightMotorSpeed = 1.0f;
                    }
                }
                if (true){
                    System.out.println("leftSpeed: " + leftMotorSpeed);
                    System.out.println("rightSpeed: " + rightMotorSpeed);
                }
                
            } else {
                // we are driving forward - not in reverse
                
                while (thetaDiff <= (float)Math.PI*-1.0f){
                    thetaDiff = thetaDiff + ((float)Math.PI*2.0f);
                }
                while (thetaDiff > (float)Math.PI){
                    thetaDiff = thetaDiff - ((float)Math.PI*2.0f);
                }
                if (DEBUG){
                    System.out.println("Driving forward.");
                    System.out.println("thetaDiff in Degrees: " + (thetaDiff * 180.0f/(float)Math.PI));
                }
                
                if (thetaDiff < 0f){
                    // we need to turn right
                    leftMotorSpeed = 1.0f;
                    if (thetaDiff >= thetaLimitToTurnAtAllInRadians* -1.0f){
                        // close enough to heading angle - we don't need to turn
                        rightMotorSpeed = 1.0f;
                    } else if (thetaDiff < thetaLimitToTurnAtAllInRadians* -1.0f &&
                            thetaDiff >= thetaLimitToTurnInPlaceInRadians * -1.0f){
                        
                        // we need to turn a variable amount
                        rightMotorSpeed = pointSlopeLineCalc(-1f*thetaLimitToTurnAtAllInRadians, 
                                                            1.0f, 
                                                            -1f*thetaLimitToTurnInPlaceInRadians,
                                                            -1.0f, 
                                                            thetaDiff);
                    } else {
                        // we should be turning in place full steam
                        rightMotorSpeed = -1.0f;
                    }
                } else {
                    // we need to turn left
                    rightMotorSpeed = 1.0f;
                    if (thetaDiff <= thetaLimitToTurnAtAllInRadians){
                        // we don't need to turn at all
                        leftMotorSpeed = 1.0f;
                    } else if (thetaDiff > thetaLimitToTurnAtAllInRadians &&
                            thetaDiff <= thetaLimitToTurnInPlaceInRadians){
                        // we should turn a variable amount
                        leftMotorSpeed = pointSlopeLineCalc(thetaLimitToTurnAtAllInRadians, 
                                                            1.0f, 
                                                            thetaLimitToTurnInPlaceInRadians,
                                                            -1.0f, 
                                                            thetaDiff);
                    } else {
                        // we need to turn in place completely
                        leftMotorSpeed = -1.0f;
                    }
                }
                
            }
            
            
            
            
            
        } else {
            leftMotorSpeed = 0.0f;
            rightMotorSpeed = 0.0f;
        }
        
        if (DEBUG){
            System.out.println("leftSpeed: " + leftMotorSpeed);
            System.out.println("rightSpeed: " + rightMotorSpeed);
        }
        
        retval.leftMotorSpeed = leftMotorSpeed * motorSpeedMultToUse;
        retval.rightMotorSpeed = rightMotorSpeed * motorSpeedMultToUse;
        
        if (retval.leftMotorSpeed > 1f){
            retval.leftMotorSpeed = 1f;
        }
        if (retval.leftMotorSpeed < -1f){
            retval.leftMotorSpeed = -1f;
        }
        
        if (retval.rightMotorSpeed > 1f){
            retval.rightMotorSpeed = 1f;
        }
        if (retval.rightMotorSpeed < -1f){
            retval.rightMotorSpeed = -1f;
        }
        
        
        
        return retval;
    }
    
    /**
     * This method will calculate the motor speed bytes for turning the robot around in place
     * 
     * @param shouldRobotMove - true if the robot should move - if false, method will output zeros
     * @param currentHeadingAngleInRadians - current heading angle of the lunabot in radians
     * @param desiredHeadingAngleInRadians - desired heading angle of the lunabot in radians
     * @param motorSpeedMultiplier - motor speed multiplier between 0 and 1 - will adjust speed motors are set to
     * @return - Array of bytes - return[0] = leftMotorByte, return[1] = rightMotorByte
     */
    public static mainDriveMotorFloatStructClass getLeftRightDriveMotorBytesForTurnAroundInPlace(
            boolean shouldRobotMove,
            float currentHeadingAngleInRadians,
            float desiredHeadingAngleInRadians,
            float motorSpeedMultiplier){
        
        
        
        mainDriveMotorFloatStructClass retval = new mainDriveMotorFloatStructClass();
        
        float motorSpeedMultToUse = Math.abs(motorSpeedMultiplier);
        
        if (motorSpeedMultToUse > 1f){
            motorSpeedMultToUse = 1f;
        }
        if (motorSpeedMultToUse < 0f){
            motorSpeedMultToUse = 0f;
        }
        
        float leftMotorSpeed = 0f;
        float rightMotorSpeed = 0f;
        
        if (shouldRobotMove){
            float thetaDiff = desiredHeadingAngleInRadians - currentHeadingAngleInRadians;
            while (thetaDiff <= (float)Math.PI*-1.0f){
                thetaDiff = thetaDiff + ((float)Math.PI*2.0f);
            }
            while (thetaDiff > (float)Math.PI){
                thetaDiff = thetaDiff - ((float)Math.PI*2.0f);
            }
            if (DEBUG){
                System.out.println("thetaDiff In Degrees: " + (thetaDiff * 180.0f/(float)Math.PI));
            } 
            
            if (thetaDiff < 0f){
                // we need to turn right
                leftMotorSpeed = 1.0f;
                rightMotorSpeed = -1.0f;
            } else {
                // we need to turn left
                leftMotorSpeed = -1.0f;
                rightMotorSpeed = 1.0f;
            }
            
        } else {
            leftMotorSpeed = 0f;
            rightMotorSpeed = 0f;
        }
        
        
        
        
        // at this point, the 
        if (DEBUG){
            System.out.println("leftSpeed: " + leftMotorSpeed);
            System.out.println("rightSpeed: " + rightMotorSpeed);
        }
        
        retval.leftMotorSpeed = leftMotorSpeed * motorSpeedMultToUse;
        retval.rightMotorSpeed = rightMotorSpeed * motorSpeedMultToUse;
        
        if (retval.leftMotorSpeed > 1f){
            retval.leftMotorSpeed = 1f;
        }
        if (retval.leftMotorSpeed < -1f){
            retval.leftMotorSpeed = -1f;
        }
        
        if (retval.rightMotorSpeed > 1f){
            retval.rightMotorSpeed = 1f;
        }
        if (retval.rightMotorSpeed < -1f){
            retval.rightMotorSpeed = -1f;
        }
           
            
            
        return retval;
    }
    
}