/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package udpbeaconcontrol;

import struct.StructClass;
import struct.StructField;


@StructClass
public class RxStruct {
    @StructField(order=0)
    public long compassHeadingFromGyro;
}
