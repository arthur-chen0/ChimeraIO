package com.jht.chimera.io.lib;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IOPacket {

    private byte[] txPacket;
    private byte[] data = null;
    private byte[] dataLength;
    private String txMessage;
    private IOProtocol.Commands command;

    private int commandCode = 0;
    private int payloadLength = 0;
    private int payloadIndex = 0;
    private int rxIndex = 0;
    private byte[] rxData = new byte[0];
    private byte[] payload = new byte[0];

    private ByteBuffer lengthBuffer = ByteBuffer.allocate(2);
    private ByteBuffer commandBuffer = ByteBuffer.allocate(2);

    private packetFrame parseStatus = packetFrame.START_1;
    private parseResult result = parseResult.Success;
    private CommandType commandType = CommandType.REPLAY;

    private final byte StartByte_1 = (byte)0x55;
    private final byte StartByte_2 = (byte)0xAA;

    enum packetFrame{
        START_1,
        START_2,
        COMMAND_1,
        COMMAND_2,
        RESULT_1,
        RESULT_2,
        LENGTH_1,
        LENGTH_2,
        PAYLOAD,
        CHECKSUM,
    }

    enum parseState{
        Success,
        NoReply,
        Incomplete,
        InvalidStartByte,
        InvalidCrc,
        Failure,
        InvalidLength
    }

    enum parseResult{
        Success,
        UnknownCommand,
        InvalidPayloadLength,
        InvalidParameter,
        InvalidAddress,
        InvalidPassword,
        Failure,
    }

    enum CommandType{
        REPLAY,
        EVENT,
        UPDATE
    }

    IOPacket(){

    }

    IOPacket(IOProtocol.Commands command, byte... data){
        this.command = command;
        this.txMessage = command.toString();
        if(data != null) {
            this.data = data;
            this.dataLength = ByteBuffer.allocate(4).putInt(data.length).array();
            txPacket = new byte[data.length + 9];
            buildtxPacket();
        }
    }

    public void buildtxPacket(IOProtocol.Commands command, byte... data){
        this.command = command;
        this.txMessage = command.toString();
        if(data != null) {
            this.data = data;
            this.dataLength = ByteBuffer.allocate(4).putInt(data.length).array();
            txPacket = new byte[data.length + 9];
            buildtxPacket();
        }
    }


    private byte[] buildtxPacket(){
        txPacket[packetFrame.START_1.ordinal()] = StartByte_1;
        txPacket[packetFrame.START_2.ordinal()] = StartByte_2;
        txPacket[packetFrame.COMMAND_1.ordinal()] = command.get_command_2();
        txPacket[packetFrame.COMMAND_2.ordinal()] = command.get_command_1();
        txPacket[packetFrame.LENGTH_1.ordinal()] = dataLength[3];
        txPacket[packetFrame.LENGTH_2.ordinal()] = dataLength[2];

        if(data != null) {
            for (int i = 0; i < data.length; i++) {
                txPacket[i + packetFrame.PAYLOAD.ordinal()] = data[i];
            }
        }

        byte[] checkSumData = new byte[data.length + 8];
        for (int i = 0; i < data.length + 8; i++){
            checkSumData[i] = txPacket[i];
        }
        txPacket[data.length + 8] = CRC_8.getCrc8_U(checkSumData, data.length + 8);
        return txPacket;
    }

    public parseState rxPacketParse(byte data){

        if (rxData.length == 0) {
            rxData = new byte[600];
            rxIndex = 0;
        }
        if(rxIndex < 600)
            rxData[rxIndex] = data;
//        Log.d("IOPacketManager","parse status: " + parseStatus);

        switch (parseStatus) {
            case START_1:
                if (data == 0x55)
                    parseStatus = packetFrame.START_2;
                else
                    return parseState.InvalidStartByte;
                break;
            case START_2:
                if(data == (byte)0xAA)
                    parseStatus = packetFrame.COMMAND_1;
                else
                    return parseState.InvalidStartByte;
                break;
            case COMMAND_1:
                commandBuffer.put(data);
//                commandCode = data;
                parseStatus = packetFrame.COMMAND_2;
                break;
            case COMMAND_2:
                if(data == 0x02)
                    commandType = CommandType.EVENT;
                else if(data == 0x04)
                    commandType = CommandType.UPDATE;
                else
                    commandType = CommandType.REPLAY;
//                commandCode = commandCode + ((data & 0xFF) << 8);
                commandBuffer.put(data);
                commandBuffer.flip();
                commandCode = commandBuffer.order(ByteOrder.LITTLE_ENDIAN).getChar();
                Log.d("McuUpdateManager", "txCommand code: " + commandCode);
                commandBuffer.clear();
                parseStatus = packetFrame.RESULT_1;
                break;
            case RESULT_1:
                switch (data){
                    case (byte)0x00:
                        result = parseResult.Success;
                        break;
                    case (byte)0x01:
                        result = parseResult.UnknownCommand;
                        break;
                    case (byte)0x02:
                        result = parseResult.InvalidPayloadLength;
                        break;
                    case (byte)0x03:
                        result = parseResult.InvalidParameter;
                        break;
                    case (byte)0x04:
                        result = parseResult.InvalidAddress;
                        break;
                    case (byte)0x05:
                        result = parseResult.InvalidPassword;
                        break;
                    case (byte)0x06:
                        result = parseResult.Failure;
                        break;
                }
                parseStatus = packetFrame.RESULT_2;
                break;
            case RESULT_2:
                parseStatus = packetFrame.LENGTH_1;
                break;
            case LENGTH_1:
                lengthBuffer.put(data);
//                payloadLength = data;
                parseStatus = packetFrame.LENGTH_2;
                break;
            case LENGTH_2:
                lengthBuffer.put(data);
                lengthBuffer.flip();
                payloadLength = lengthBuffer.order(ByteOrder.LITTLE_ENDIAN).getChar();
//                payloadLength += ((data & 0xFF) << 8);
                Log.d("IOPacketManager","parse Length: " + payloadLength);
                payload = new byte[payloadLength];
                lengthBuffer.clear();
                if(payloadLength == 0)
                    parseStatus = packetFrame.CHECKSUM;
                else if(payloadLength >= 600 - rxIndex - 1) {
                    // Invalid length.  Drop packet.  buffer size - header - checksum = max data length.
                    return parseState.InvalidLength;
                }
                else
                    parseStatus = packetFrame.PAYLOAD;
                break;
            case PAYLOAD:
                payload[payloadIndex] = data;
                if (++payloadIndex == payloadLength) {
                    parseStatus = packetFrame.CHECKSUM;
                    payloadIndex = 0;
                }
                break;
            case CHECKSUM:
                String checkDataString = "";
                for(int i= 0; i <= rxIndex; i++)
                    checkDataString += String.format("%X",rxData[i]) + " ";

                // Log.d("IOPacketManager","checkSum data : " + checkDataString + " index: " + rxIndex);

                byte checkSum = CRC_8.getCrc8_U(rxData,rxIndex);
                if(rxData[rxIndex] != checkSum) {
                    Log.e("IOManager","parse CheckSum: " + String.format("%X", checkSum));
                    return parseState.InvalidCrc;
                }
//                Log.d("McuPacketManager","Command Type: " + commandType.toString());

                if(result == parseResult.Success) {
                    if (commandType == CommandType.UPDATE) {
                        McuUpdateManager.Instance.updateProcess(commandCode, payload, commandType);
                        if(IOProtocol.Commands.fromInt(commandCode) == IOProtocol.Commands.OPERATION_MODE)
                            IOManager.getInstance().rxProcess(commandCode, payload, CommandType.REPLAY);
                    }
                    else
                        IOManager.getInstance().rxProcess(commandCode, payload, commandType);
                    rxData = new byte[0];
                }
//                else
//                {
//                    if(McuProtocol.McuCommands.fromInt(commandCode) == McuProtocol.McuCommands.SEND_IR_COMMAND){
//                        payload[0] = (byte)result.ordinal();
//                        McuPacketManager.getInstance().rxProcess(commandCode, payload, commandType);
//                    }
//                }
                return parseState.Success;
        }

        rxIndex++;

        return parseState.Incomplete;
    }

    public void packetReset(){
//        Log.d(TAG,"reset packet");
        rxData = new byte[0];
        rxIndex = 0;
        payload = new byte[0];
        parseStatus = packetFrame.START_1;
    }

    public parseResult getResult(){
        return result;
    }

    public String getTxMessage() {
        return txMessage;
    }

    public byte[] getTxPacket() {
        return txPacket;
    }
}
