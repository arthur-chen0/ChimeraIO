package com.jht.utilities

abstract class LogParser {
    abstract void parseLine(String line, OutputStream stream)
    void reset() {}

    String getTimeStamp(String line) {
        return line.substring(0, 34)
    }
}