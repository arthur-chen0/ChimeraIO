package com.jht

class STBSettings {
    class SerialPortConfiguration {
        int baudRate = 115200
    }
    class RemoteButton {
        int buttonId
        String buttonName
        int[] header = null
        int[] data = null
        int[] end = null
        String command

        RemoteButton(Object json) {

            buttonName = json.buttonName
            if(json.buttonId instanceof Integer) {
                buttonId = json.buttonId.intValue()
            }
            else {
                buttonId = -1
            }
            command = json.command
            if(json.header != null) {
                header = new int[json.header.size()]
                for(int i = 0; i < json.header.size(); i++) {
                    header[i] = json.header[i]
                }
            }

            if(json.data != null) {
                data = new int[json.data.size()]
                for(int i = 0; i < json.data.size(); i++) {
                    data[i] = json.data[i]
                }
            }

            if(json.end != null) {
                end = new int[json.end.size()]
                for(int i = 0; i < json.end.size(); i++) {
                    end[i] = json.end[i]
                }
            }

        }
        RemoteButton(int buttonId, String buttonName, String ccf) {
            this.buttonId = buttonId
            this.buttonName = buttonName
            parseCCF(ccf)

        }

        void fixData(int repeats) {
            int newSize = data.length / repeats
            if(data.length % 2 == 1) {
                newSize++
            }
            int[] originalData = data;
            data = new int[newSize]
            for(int i = 0; i < data.length; i++) {
                data[i] = originalData[i]
            }
        }

        private void parseCCF(String ccf) {
            String[] values = ccf.split(" ")
            if (values.length < 4) {
                return
            }
            //int mode = Integer.parseInt(values[0], 16)
            double frequency = Integer.parseInt(values[1], 16) * 0.241246
            int headerLength = Integer.parseInt(values[2], 16)
            int repeatLength = 36 //Integer.parseInt(values[3], 16)

            header = headerLength == 0 ? null : new int[headerLength]
            for(int i = 0; i < headerLength; i++) {
                header[i] = Math.floor(frequency * Math.abs(Integer.parseInt(values[i + 4], 16)))
            }

            data = new int[repeatLength]
            for(int i = 0; i < repeatLength; i++) {
                data[i] = Math.floor(frequency * Math.abs(Integer.parseInt(values[i + 4 + headerLength], 16)))
            }
            end = null //new int[0]
            repeats--

        }
    }
    String setTopBoxName
    boolean useIR = true
    String serialPortDevice = "/dev/ttyUSB1"
    SerialPortConfiguration serialPortConfiguration = new SerialPortConfiguration()
    RemoteButton[] remoteButtons
    int irFrequencyKhz = 38
    int repeats

    STBSettings(Object json, int version) {
        if (version == 0) {
            setTopBoxName = json.RemoteInfo.DeviceFamily + " " + json.RemoteInfo.Manufacturer + " " + json.RemoteInfo.DeviceModel
            println("\nset top box name " + setTopBoxName)
            int length = json.RemoteFunctions.size()
            remoteButtons = new RemoteButton[length]
            for (int i = 0; i < remoteButtons.length; i++) {
                String name = json.RemoteFunctions[i].ID
                String command = json.RemoteFunctions[i].CCF
                repeats = json.RemoteFunctions[i].MinRepeats
                remoteButtons[i] = new RemoteButton(-1, name.toLowerCase().replace(" ", "_"), command,)
            }
        }
        else {
            setTopBoxName = json.setTopBoxName
            useIR = json.useIR
            serialPortDevice = json.serialPortDevice
            serialPortConfiguration.baudRate = json.serialPortConfiguration.baudRate
            try {
                irFrequencyKhz = json.irFrequencyKhz
            }
            catch(Exception ignored) {
                irFrequencyKhz = 0
            }
            repeats = 1

            remoteButtons = new RemoteButton[json.remoteButtons.size()]
            for(int i = 0; i < remoteButtons.length; i++) {
                remoteButtons[i] = new RemoteButton(json.remoteButtons[i])
                remoteButtons[i].fixData(2)
            }
        }
    }


}