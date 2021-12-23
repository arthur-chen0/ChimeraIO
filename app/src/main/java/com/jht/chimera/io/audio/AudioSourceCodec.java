package com.jht.chimera.io.audio;

import android.content.Context;
import android.provider.Settings;

import com.jht.androidcommonalgorithms.util.SystemCommands;


/**
 * This works with native code to set the Audio mixer.
 */
public class AudioSourceCodec {
    /**
     * The mixer source setting.
     */

    Context context;

    public enum CodecSource {
        DEFAULT(0),
        AUDIO_SOC(1),
        AUDIO_LINE_IN_1(2),
        AUDIO_LINE_IN_2(3),
        AUDIO_SOC_MIX_LINE_IN_1(4),
        AUDIO_SOC_MIX_LINE_IN_2(5),
        AUDIO_SOC_MIX_LINE_IN_ALL(6),
        AUDIO_MUTE(15),
        AUDIO_UNMUTE(16);

        private int ctlNumber;

        CodecSource(int ctlNumber) {
            this.ctlNumber = ctlNumber;
        }

        public int getCtlNumber() {
            return this.ctlNumber;
        }
    }

    public AudioSourceCodec(Context context){
        this.context = context;
    }

    /**
     * Get the mute setting based on the carrier board.
     *
     * @return  0 for rev D or older.  1 for rev F or later.
     */
    private int getMuteFunction() {
        String carrierBoard = SystemCommands.getSystemProperty("ro.boot.board_ca");
        if (carrierBoard.isEmpty()) {

            try {
                //Fall back to using the IO version to check ca board version.
                String ioVersion = Settings.Secure.getString(context.getContentResolver(), "chimera_io_version");
                String[] parts = ioVersion.split("\\.");
                int major = Integer.parseInt(parts[0]);

                if (major >= 50) {
                    return 1;
                } else {
                    return 0;
                }
            }
            catch(Exception ex) {
                return 0;
            }

        } else {
            return carrierBoard.toLowerCase().compareTo("verf") >= 0 ? 1 : 0;
        }
    }

    /**
     * Set the audio source.
     * @param source    The audio source to activate with the mixer.
     */
    public void setAudioSource(CodecSource source) {
        audioControl(source.getCtlNumber(), this.getMuteFunction());
    }

    // Native functions.
    public static native int audioControl(int var0, int var1);

    static {
        System.loadLibrary("ChimeraMIXCtl");
    }


}
