/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package udpbeaconcontrol;

//import 

import struct.*;


/**
 *
 * @author eric
 */
@StructClass
public class TxStruct {
    @StructField(order=0)
    public short seq; /// GAHAHAHAHHA! No unsigned types!
    @StructField(order=1)
    public byte type;
    @StructField(order=2)
    public byte brightness;
}
