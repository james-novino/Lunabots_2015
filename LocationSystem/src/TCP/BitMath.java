/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

/**
 * David Osinski
 *
 * @author Owner
 */
abstract class BitMath {

    public static int ByteToInteger(byte x) {
        int a = 0;

        for (byte i = 0; i < 8; i++) {
            a |= (x & (1 << i));
        }

        return a;
    }

    public static byte[] ShortToByte(short x) {
        byte tmp[] = new byte[2];

        for (byte i = 0; i < 16; i++) {
            tmp[i / 8] |= (x & (1 << i)) >> (i / 8 * 8);
        }

        return tmp;
    }

    public static byte[] IntToByte(int x) {
        byte tmp[] = new byte[4];

        for (byte i = 0; i < 32; i++) {
            tmp[i / 8] |= (x & (1 << i)) >> (i / 8 * 8);
        }

        return tmp;
    }

    public static byte[] LongToByte(long x) {
        byte tmp[] = new byte[8];

        for (byte i = 0; i < 64; i++) {
            tmp[i / 8] |= (x & (1 << i)) >> (i / 8 * 8);
        }

        return tmp;
    }

    public static byte DoubleToByte(double x) {
        int y = (int) x;
        byte z = 0;
        for (byte i = 0; i < 8; i++) {
            z |= y & (1 << i);
        }
        return z;
    }
}
