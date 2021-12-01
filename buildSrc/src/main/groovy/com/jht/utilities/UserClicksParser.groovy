package com.jht.utilities


class UserClicksParser extends LogParser{
    void parseLine(String line, OutputStream stream) {
        if(line.contains("CLICK:")) {
            String button = line.substring(line.indexOf("CLICK:", + 7))
            String result = getTimeStamp(line) + " User clicked on " + button + "\n"
            stream.write(result.bytes)
        }
    }

    void reset() {}
}