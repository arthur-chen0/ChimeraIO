package com.jht

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class STBConverter {
    static void run(File sourceDir, File outputDir) {
        for(File f : sourceDir.listFiles()) {
            JsonSlurper json = new JsonSlurper()
            def object = json.parse(f)
            print(object)
            STBSettings settings = new STBSettings(object, 0)
            File outputFile = new File(outputDir, f.getName())
            outputFile.write(new JsonBuilder(settings).toPrettyString())
        }
    }

    static void updateJson(File rootDir) {
        for (File f : rootDir.listFiles()) {
            if(f.isDirectory()) {
                updateJson(f)
            }
            else if(f.name.equals("settings.json")) {
                JsonSlurper json = new JsonSlurper()
                def object = json.parse(f)
                print(object)
                STBSettings settings = new STBSettings(object, 1)
                f.write(new JsonBuilder(settings).toPrettyString())
            }


        }
    }
}
