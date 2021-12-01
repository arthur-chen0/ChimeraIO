package com.jht.utilities

class ParseLogs {
    final List<LogParser> parsers = new ArrayList<>()
    ParseLogs() {}

    void addParser(LogParser parser) {
        parsers.add(parser)
    }

    void parseLogs(File inputPath, File outputPath) {
        for(File f : inputPath.listFiles()) {
            if(f.isFile() && f.name.contains("log") && f.name.contains(".txt")) {
                parseLog(f, new File(outputPath, f.name))
            }
            else if(f.isDirectory()) {
                parseLogs(f, new File(outputPath, f.name))
            }
        }

        StringBuilder processOutput = new StringBuilder();
        Process p = "adb logcat -d".execute()
        Thread outThread = Thread.start {
            processOutput.append(p.getText())
        }
        Thread errorThread = Thread.start {
            p.getErrorStream().getText()
        }
        outThread.join(5000)
        errorThread.join(5000)

        for (LogParser parser : parsers) {
            for (String line : processOutput.toString().split("\n")) {
                parser.parseLine(line, System.out)
            }
        }
    }

    void parseLog(File logFile, File outputFile) {
        outputFile.withOutputStream {output ->
            logFile.withInputStream {input ->
                input.eachLine {line ->
                    for (LogParser parser : parsers) {
                        parser.parseLine(line, output)
                    }
                }
            }
        }

    }
}