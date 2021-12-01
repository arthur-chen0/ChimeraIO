package com.jht.utilities

import java.util.regex.Pattern

class ProcessLaunchParser extends LogParser {
    void parseLine(String line, OutputStream stream) {
        if(line.contains("START u0")) {
            String process = line.find(Pattern.compile("cmp=.*}")).toString()
            process = process.substring(4, process.length() - 1)
            String uid = line.substring(line.lastIndexOf(" "))
            String result = getTimeStamp(line) + " " + uid + " started " + process + "\n"
            stream.write(result.bytes)
        }
    }

}