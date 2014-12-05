/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_location_receive;

import struct.*;

/**
 *
 * @author chalbers2
 */
@StructClass
public class RxLocationStruct {
    @StructField(order=0)
    public short xPositionInCentimeters;
    
    @StructField(order=1)
    public short yPositionInCentimeters;
    
    @StructField(order=2)
    public float headingAngleInRadians;
}
