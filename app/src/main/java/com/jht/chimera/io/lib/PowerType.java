package com.jht.chimera.io.lib;

import android.annotation.SuppressLint;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;

public class PowerType {

    public class PowerSource{
        private String name;
        private byte id;
        private Switch powerSwitch;

        PowerSource(String name, byte id, Switch powerSwitch){
            this.name = name;
            this.id = id;
            this.powerSwitch = powerSwitch;
        }

        public String getName(){
            return name;
        }

        public byte getId(){
            return id;
        }

        public Switch getPowerSwitch(){
            return powerSwitch;
        }
    }

    private static class PowerTypeInstance {
        @SuppressLint("StaticFieldLeak")
        private static final PowerType powerType = new PowerType();
    }

    public static PowerType getInstance(){
        return PowerTypeInstance.powerType;
    }

    public ArrayList<PowerSource> powerTypeMap = new ArrayList();

    public ArrayList getPowerTypeMap(){
        return powerTypeMap;
    }

    public void addPower(String name, byte id, Switch powerSwitch){
        powerTypeMap.add(new PowerSource(name,id,powerSwitch));
    }

    public Switch getSwitchByID(byte id){
        for(PowerSource power: powerTypeMap){
            if(power.id == id){
                return power.powerSwitch;
            }
        }
        return null;
    }

    public Switch getSwitchByName(String name){
        for(PowerSource power: powerTypeMap){
            if(power.name.equals(name)){
                return power.powerSwitch;
            }
        }
        return null;
    }

    public byte getIDByPowerSwitch(CompoundButton powerSwitch){
        for (PowerSource power: powerTypeMap){
            if(power.powerSwitch.getId() == powerSwitch.getId()){
                return power.id;
            }
        }
        return -1;
    }

    public String getNameBySwitch(CompoundButton powerSwitch){
        for(PowerSource power: powerTypeMap){
            if(power.powerSwitch.getId() == powerSwitch.getId()){
                return power.name;
            }
        }
        return null;
    }

    public String getNameByID(byte id){
        for(PowerSource power: powerTypeMap){
            if(power.id == id){
                return power.name;
            }
        }
        return null;
    }


//    rs485Power((byte)0x01),
//    csafePower((byte)0x03),
//    hdmiPower((byte)0x04),
//    audioSAPower((byte)0x05),
//    voicePower((byte)0x07),
//    rfidPower((byte)0x08),
//    qiPower((byte)0x09),
//    tvPower((byte)0x0A),
//    cabPower((byte)0x0B),
//    ipodPower((byte)0x0C),
//    heartRatePower((byte)0x0D),
//    gymkitPower((byte)0x0E),
//    usb_5V((byte)0x0F),
//    ext_14V((byte)0x10);



}


