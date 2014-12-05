/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

import struct.*;

/**
 *
 * @author chalbers2
 */

@StructClass
public class mainDriveMotorFloatStructClass {
    @StructField(order=0)
    public float leftMotorSpeed;
    
    @StructField(order=1)
    public float rightMotorSpeed;
}
