package com.jht.utilities

import javax.xml.bind.DatatypeConverter


class TunerParser extends LogParser {
    void parseLine(String line, OutputStream stream) {
        if(line.contains("Write serial port") && line .contains("ttymxc3")) {
            String dataBytes = line.substring(line.lastIndexOf(" ") + 1)
            if (dataBytes.length() >= 6) {
                String result = getTimeStamp(line) + " Sent " + new String(DatatypeConverter.parseHexBinary(dataBytes.substring(0, 6))) + " to tuner\n"
                stream.write(result.bytes)
            }

        }
        else if(line.contains("Read serial port") && line .contains("ttymxc3")) {
            String dataBytes = line.substring(line.lastIndexOf(" ") + 1)
            if(dataBytes.toLowerCase().startsWith("f5") && dataBytes.length() >= 6) {
                String result = getTimeStamp(line) + " Received ir code 0x" + dataBytes.substring(4, 6).toUpperCase() + " from tuner: " + dataBytes + "\n"
                stream.write(result.bytes)
            }
            else {
                String result = getTimeStamp(line) + " Received garbage from tuner: " + dataBytes + "\n"
                stream.write(result.bytes)
            }
        }
        else if(line.contains("Parsing Swift packet")) {
            String command = line.substring(line.lastIndexOf(" ") + 1)
            String result = getTimeStamp(line) + " Parsing tuner packet " + command + "\n"
            stream.write(result.bytes)
        } else if(line.contains("Missing response ")) {
            String command = line.substring(line.lastIndexOf(" ") + 1)
            String result = getTimeStamp(line) + " Missing tuner packet " + command + "\n"
            stream.write(result.bytes)
        } else if(line.contains("resetTunerProperties")) {
            String result = getTimeStamp(line) + " resetTunerProperties\n"
            stream.write(result.bytes)
        }

    }

    void reset() {}
}