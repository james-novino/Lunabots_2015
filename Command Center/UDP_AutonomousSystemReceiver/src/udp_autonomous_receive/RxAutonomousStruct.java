/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_autonomous_receive;

import java.util.LinkedList;
import struct.*;

/**
 *
 * @author chalbers2
 */
@StructClass
public class RxAutonomousStruct {
    @StructField(order=0)
    public short xPositionInCentimeters;
    
    @StructField(order=1)
    public short yPositionInCentimeters;
    
    @StructField(order=2)
    public float headingAngleInRadians;
    
    @StructField(order=3)
    @ArrayLengthMarker(fieldName = "arenaObstacleGridArray0")
    public int obstLength0;
    
    @StructField(order=4)
    public short[] arenaObstacleGridArray0;
    
    @StructField(order=5)
    @ArrayLengthMarker(fieldName = "arenaObstacleGridArray1")
    public int obstLength1;
    
    @StructField(order=6)
    public short[] arenaObstacleGridArray1;
    
    @StructField(order=7)
    @ArrayLengthMarker(fieldName = "arenaObstacleGridArray2")
    public int obstLength2;
    
    @StructField(order=8)
    public short[] arenaObstacleGridArray2;
    
    @StructField(order=9)
    @ArrayLengthMarker(fieldName = "arenaObstacleGridArray3")
    public int obstLength3;
    
    @StructField(order=10)
    public short[] arenaObstacleGridArray3;
    
    @StructField(order=11)
    @ArrayLengthMarker(fieldName = "RobotPathArrayX")
    public int robotPathLengthX;
    
    @StructField(order=12)
    public short[] RobotPathArrayX;
    
    @StructField(order=13)
    @ArrayLengthMarker(fieldName = "RobotPathArrayY")
    public int robotPathLengthY;
    
    @StructField(order=14)
    public short[] RobotPathArrayY;
    
    @StructField(order=15)
    @ArrayLengthMarker(fieldName = "currentAutoSysStateCharArray")
    public int currentAutonomousSystemStateStringLength;
    
    @StructField(order=16)
    public char[] currentAutoSysStateCharArray;
}
