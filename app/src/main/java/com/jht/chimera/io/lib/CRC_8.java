package com.jht.chimera.io.lib;

public class CRC_8 {

    private static int[] crc8_Table = {0x00,0x5A,0xB4,0xEE,0x32,0x68,0x86,0xDC,0x64,0x3E,0xD0,0x8A,0x56,0x0C,0xE2,0xB8};

    static byte getCrc8_U(byte[] data, int length) {
        int ptrIndex = 0;
        byte highNibble_U, dataTemp_U, newCrc_U = 0;

        while(length-- != 0) {
            dataTemp_U = data[ptrIndex];
            highNibble_U = (byte)(ByteToUnsignedInt(newCrc_U) / 16);
            newCrc_U = (byte)(ByteToUnsignedInt(newCrc_U) << 4);
            newCrc_U = (byte)(ByteToUnsignedInt(newCrc_U) ^ crc8_Table[ByteToUnsignedInt(highNibble_U) ^ ByteToUnsignedInt(dataTemp_U) / 16]);

            highNibble_U = (byte)(ByteToUnsignedInt(newCrc_U) / 16);
            newCrc_U = (byte)(ByteToUnsignedInt(newCrc_U) << 4);
            newCrc_U = (byte)(ByteToUnsignedInt(newCrc_U) ^ crc8_Table[ByteToUnsignedInt(highNibble_U) ^ ByteToUnsignedInt(dataTemp_U) & 0x0F]);
            ptrIndex++;
        }

//            Log.d("McuPacketManager","CRC Code: " + String.format("%X",newCrc_U));
        return newCrc_U;
    }

    private static int ByteToUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }
}
