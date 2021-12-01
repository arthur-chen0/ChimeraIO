package com.jht.rscu

import groovy.json.JsonSlurper

import javax.net.ssl.HttpsURLConnection
import java.util.zip.GZIPInputStream

class RSCU {

    static boolean associate(String softwareType, String softwareConfigurationClass, String version, String url, String md5, List<RSCUAssociation> associations) {

        StringBuilder softwareItem = new StringBuilder()

        softwareItem.append("{")
        softwareItem.append("\"software_type\":\"").append(softwareType).append("\",")
        softwareItem.append("\"version\":\"").append(version).append("\",")
        softwareItem.append("\"url\":\"").append(url).append("\",")
        softwareItem.append("\"md5\":\"").append(md5).append("\"}")

        URL rscuUrl = new URL("https://rscu-api.jfit.co/software/")
        HttpsURLConnection urlConnection = rscuUrl.openConnection()
        urlConnection.setRequestMethod("POST")

        urlConnection.setRequestProperty("Accept", "application/json, text/plain, */*")
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br")
        urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.9")
        urlConnection.setRequestProperty("Connection", "keep-alive")
        urlConnection.setRequestProperty("Content-Length", softwareItem.toString().length() + "")
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.setRequestProperty("x-api-key", System.getenv("RSCU_API_KEY"))

        urlConnection.setDoInput(true)
        urlConnection.setDoOutput(true)
        urlConnection.outputStream.write(softwareItem.toString().bytes)
        urlConnection.outputStream.close()

        int responseCode = urlConnection.getResponseCode()
        println("Response code from RSCU " + responseCode)
        if(responseCode != 200) {
            return false
        }
        InputStream inflaterStream = new GZIPInputStream(urlConnection.inputStream)
        String uncompressedStr = inflaterStream.getText('UTF-8')
        println("Response from RSCU " + uncompressedStr)

        JsonSlurper parser = new JsonSlurper()
        def object = parser.parseText(uncompressedStr)

        println("Software Item id = " + object.software.id)

        for(RSCUAssociation association : associations) {
            boolean result = associate(softwareConfigurationClass, version, object.software.id, association)
            if(!result) {
                return false
            }
        }
        return true
    }

    static boolean associate(String softwareClass, String version, String id, RSCUAssociation association) {

        StringBuilder softwareBundle = new StringBuilder()

        softwareBundle.append("{")
            softwareBundle.append("\"software_configuration_class\":\"").append(softwareClass).append("\",")
            softwareBundle.append("\"version\":\"").append(version).append("\",")
            softwareBundle.append("\"club_id\":").append(association.clubId()).append(",")
            softwareBundle.append("\"env\":{")
                softwareBundle.append("\"dev\":").append(association.dev() ? "1," : "0,")
                softwareBundle.append("\"qa\":").append(association.qa() ? "1," : "0,")
                softwareBundle.append("\"staging\":").append(association.staging() ? "1," : "0,")
                softwareBundle.append("\"prod\":0")
            softwareBundle.append("},")
            softwareBundle.append("\"software_ids\":[\"").append(id).append("\"]")
        softwareBundle.append("}")

        println("Software bundle request " + softwareBundle)

        URL rscuUrl = new URL("https://rscu-api.jfit.co/bundle/")
        HttpsURLConnection urlConnection = rscuUrl.openConnection()
        urlConnection.setRequestMethod("POST")

        urlConnection.setRequestProperty("Accept", "application/json, text/plain, */*")
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br")
        urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.9")
        urlConnection.setRequestProperty("Connection", "keep-alive")
        urlConnection.setRequestProperty("Content-Length", softwareBundle.toString().length() + "")
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.setRequestProperty("x-api-key", System.getenv("RSCU_API_KEY"))

        urlConnection.setDoInput(true)
        urlConnection.setDoOutput(true)
        urlConnection.outputStream.write(softwareBundle.toString().bytes)
        urlConnection.outputStream.close()
        int responseCode = urlConnection.getResponseCode()
        println("Response code from RSCU " + responseCode)
        if(responseCode != 200) {
            return false
        }

        InputStream inflaterStream = new GZIPInputStream(urlConnection.inputStream)
        println("Response from RSCU " + inflaterStream.getText('UTF-8'))

        return true
    }



}
