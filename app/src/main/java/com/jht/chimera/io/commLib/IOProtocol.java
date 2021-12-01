package com.jht.chimera.io.commLib;

public class IOProtocol {

    public enum Commands{
        //Control Commands
        TONE(              (byte)0x00, (byte)0x01),
        FAN(               (byte)0x00, (byte)0x02),
        POWER(             (byte)0x00, (byte)0x03),
        ERP(               (byte)0x00, (byte)0x04),
        DEBUG(             (byte)0x00, (byte)0x05),
        REBOOT(            (byte)0x00, (byte)0x06),
        IWDG(              (byte)0x00, (byte)0x07),
        RS485(             (byte)0x00, (byte)0x08),
        QI(                (byte)0x00, (byte)0x09),
        HW_MONITOR_SWITCH( (byte)0x00, (byte)0x0B),
        KEY_SETTING(       (byte)0x00, (byte)0x0C),
        //Watchdog Commands
        WATCHDOG(          (byte)0x01, (byte)0x00),
        REFRESH(           (byte)0x01, (byte)0x01),
        //Event Commands
        KEY_EVENT(         (byte)0x02, (byte)0x00),
        SAFEKEY_EVENT(     (byte)0x02, (byte)0x01),
        DEBUG_MESSAGE(     (byte)0x02, (byte)0x02),
        LCB_STATUS(        (byte)0x02, (byte)0x03),
        HW_MONITOR_EVENT(  (byte)0x02, (byte)0x04),
        QI_STATUS(         (byte)0x02, (byte)0x05),
        //Information/State Commands
        VERSION(           (byte)0x03, (byte)0x00),
        SAFEKEY(           (byte)0x03, (byte)0x01),
        POWER_STATE(       (byte)0x03, (byte)0x02),
        BOOT(              (byte)0x03, (byte)0x03),
        LCB_TYPE(          (byte)0x03, (byte)0x04),
        RECOVERY_STATE(    (byte)0x03, (byte)0x05),
        FAN_SPEED(         (byte)0x03, (byte)0x06),
        SYSTEM_STATE(      (byte)0x03, (byte)0x07),
        HW_MONITOR_STATE(  (byte)0x03, (byte)0x08),
        KEY_STATE(         (byte)0x03, (byte)0x09),
        HW_MONITOR(        (byte)0x03, (byte)0x0A),
        //Firmware Update Commands
        UPDATE_PARAMETERS( (byte)0x04, (byte)0x00),
        ERASE(             (byte)0x04, (byte)0x01),
        WRITE_PAGE(        (byte)0x04, (byte)0x02),
        READ_PAGE(         (byte)0x04, (byte)0x03),
        START_UPDATE(      (byte)0x04, (byte)0x04),
        END_UPDATE(        (byte)0x04, (byte)0x05),
        OPERATION_MODE(    (byte)0x04, (byte)0x06),
        APP_VERSION(       (byte)0x04, (byte)0x07),
        PROGRAM_STATE(     (byte)0x04, (byte)0x08),
        //CEC
        SET_CEC_CONFIG(    (byte)0x05, (byte)0x00),
        SEND_CEC_COMMAND(  (byte)0x05, (byte)0x01),
        GET_CEC_RESPONSE(  (byte)0x05, (byte)0x02),
        //IR
        SEND_IR_COMMAND(   (byte)0x06, (byte)0x01);


        private byte command_1;
        private byte command_2;

        Commands(byte command_1, byte command_2){
            this.command_1 = command_1;
            this.command_2 = command_2;
        }

        public static Commands fromInt(int i) {
            for (Commands b : Commands.values()) {
                int command;
                command = (b.command_1 << 8) + b.command_2;
                if (command == i)
                    return b;
            }
            return null;
        }

        public byte get_command_1(){
            return command_1;
        }

        public byte get_command_2(){
            return command_2;
        }


    }
}
