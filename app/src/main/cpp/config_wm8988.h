/*
 * Add by JohnKo@20190722
 * For Chimera Codec WM8988 control library
 * Reference from 'Audio codec reference value-wm8988_20190911JohnHarrisArthur'
 */
#ifndef _CONFIG_WM8988_H_
#define _CONFIG_WM8988_H_
/* For Codec WM8988 of tinymix setting */
#define ZC_TIMEOUT_SWITCH                   20
#define CAPTURE_VOLUME                      22
#define CAPTURE_ZC_SWITCH                   23
#define CAPTURE_SWITCH                      24
//#define DIGITAL_SOFT_MUTE                   29
#define PCM_VOLUME						29
#define LEFT_MIXER_LEFT_BYPASS_VOLUME	    30
#define LEFT_MIXER_RIGHT_BYPASS_VOLUME	    31
#define RIGHT_MIXER_LEFT_BYPASS_VOLUME	    32
#define RIGHT_MIXER_RIGHT_BYPASS_VOLUME	    33
#define OUTPUT_1_PLAYBACK_ZC_SWITCH		    34
#define OUTPUT_1_PLAYBACK_VOLUME		    35
#define OUTPUT_2_PLAYBACK_ZC_SWITCH		    36
#define OUTPUT_2_PLAYBACK_VOLUME		    37

//#define DAC_R                               39
//#define DAC_L                               40

#define DIFFERENTIAL_MUX                    38
#define ROUTE						39
#define LEFT_PGA_MUX					    40
#define RIGHT_PGA_MUX					    41
#define LEFT_LINE_MUX					    42
#define RIGHT_LINE_MUX					    43
#define LEFT_MIXER_LEFT_PLAYBACK_SWITCH	    44
#define LEFT_MIXER_LEFT_BYPASS_SWITCH	    45
#define LEFT_MIXER_RIGHT_PLAYBACK_SWITCH	46
#define LEFT_MIXER_RIGHT_BYPASS_SWITCH	    47
#define RIGHT_MIXER_LEFT_PLAYBACK_SWITCH	48
#define RIGHT_MIXER_LEFT_BYPASS_SWITCH	    49
#define RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH	50
#define RIGHT_MIXER_RIGHT_BYPASS_SWITCH	    51

/* Audio input source control*/
#define ADUIO_INPUT_DEFAULT                 0
#define ADUIO_INPUT_SOC                     1
#define ADUIO_INPUT_LINE_IN_1               2
#define ADUIO_INPUT_LINE_IN_2               3
#define ADUIO_INPUT_SOC_MIX_LINE_IN_1       4
#define ADUIO_INPUT_SOC_MIX_LINE_IN_2       5
#define ADUIO_INPUT_SOC_MIX_LINE_IN_ALL     6

/* Audio volume control*/
#define ADUIO_VOLUME_PCM                    7
#define ADUIO_VOLUME_OUT_ALL                8
#define ADUIO_VOLUME_OUT_1                  9
#define ADUIO_VOLUME_OUT_2                  10
#define ADUIO_VOLUME_BYPASS_ALL             11
#define ADUIO_VOLUME_BYPASS_1               12
#define ADUIO_VOLUME_BYPASS_2               13

/* Volume Level */
#define VOLUME_LEVEL_0                      0
#define VOLUME_LEVEL_1                      1
#define VOLUME_LEVEL_2                      2
#define VOLUME_LEVEL_3                      3
#define VOLUME_LEVEL_4                      4
#define VOLUME_LEVEL_5                      5
#define VOLUME_LEVEL_6                      6
#define VOLUME_LEVEL_7                      7
#define VOLUME_LEVEL_8                      8
#define VOLUME_LEVEL_9                      9
#define VOLUME_LEVEL_10                     10
#define VOLUME_LEVEL_11                     11
#define VOLUME_LEVEL_12                     12
#define VOLUME_LEVEL_13                     13
#define VOLUME_LEVEL_14                     14
#define VOLUME_LEVEL_15                     15
#define VOLUME_LEVEL_16                     16
#define VOLUME_LEVEL_17                     17
#define VOLUME_LEVEL_18                     18
#define VOLUME_LEVEL_19                     19
#define VOLUME_LEVEL_20                     20
#define VOLUME_LEVEL_21                     21
#define VOLUME_LEVEL_22                     22
#define VOLUME_LEVEL_23                     23
#define VOLUME_LEVEL_24                     24
#define VOLUME_LEVEL_25                     25
#define VOLUME_LEVEL_26                     26
#define VOLUME_LEVEL_27                     27
#define VOLUME_LEVEL_28                     28
#define VOLUME_LEVEL_29                     29
#define VOLUME_LEVEL_30                     30

/* Mute function control */
#define ADUIO_MUTE                          15
#define ADUIO_UNMUTE                        16

#define TIMYMIX_DISABLE  0
#define TIMYMIX_ENABLE  1


/* Audio Input setting */
 struct route_setting {
    int ctl_name;
    int intval;
};

/*
 * Route table of audio codec 'wm8988' setting
*/

/* Default of input setting */
static struct route_setting default_input_wm8988[] = {
    {
        .ctl_name = -1,
    },
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 1,
//    },
    {
        .ctl_name = CAPTURE_VOLUME,
        .intval = 26,
    },
    {
        .ctl_name = CAPTURE_ZC_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = CAPTURE_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_VOLUME,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_VOLUME,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_VOLUME,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_VOLUME,
        .intval = 0,
    },
//    {
//        .ctl_name = DAC_R,
//        .intval = 1,
//    },
//    {
//        .ctl_name = DAC_L,
//        .intval = 1,
//    },
    {
        .ctl_name = OUTPUT_1_PLAYBACK_ZC_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_ZC_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = LEFT_PGA_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_PGA_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = PCM_VOLUME,
        .intval = 220,
    },
    {
        .ctl_name = -1,
        .intval = 0,
    },
};

/* SOC of input setting */
static struct route_setting soc_input_wm8988[] = {
//    {
//        .ctl_name = DAC_R,
//        .intval = 1,
//    },
//    {
//        .ctl_name = DAC_L,
//        .intval = 1,
//    },
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = LEFT_PGA_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_PGA_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 0,
    },
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 0,
//    },
    {
        .ctl_name = -1,
    },
};

/* Line IN 1 of input setting */
static struct route_setting line_in1_input_wm8988[] = {
//    {
//        .ctl_name = DAC_R,
//        .intval = 1,
//    },
//    {
//        .ctl_name = DAC_L,
//        .intval = 1,
//    },
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = LEFT_PGA_MUX,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_PGA_MUX,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 1,
    },
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 0,
//    },
    {
        .ctl_name = -1,
    },
};

/* Line IN 2 of input setting */
static struct route_setting line_in2_input_wm8988[] = {
//    {
//        .ctl_name = DAC_R,
//        .intval = 1,
//    },
//    {
//        .ctl_name = DAC_L,
//        .intval = 1,
//    },
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = LEFT_PGA_MUX,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_PGA_MUX,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 1,
    },
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 0,
//    },
    {
        .ctl_name = -1,
    },
};

/* SOC mix Line IN 1 of input setting */
static struct route_setting soc_mix_line_in1_input_wm8988[] = {
    {
        .ctl_name = -1,
    },
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = LEFT_PGA_MUX,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_PGA_MUX,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 1,
    },
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 0,
//    },
    {
        .ctl_name = -1,
    },
};

/* SOC mix Line IN 2 of input setting */
static struct route_setting soc_mix_line_in2_input_wm8988[] = {
    {
        .ctl_name = -1,
    },
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = LEFT_PGA_MUX,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_PGA_MUX,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 1
    },
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 0,
//    },
    {
        .ctl_name = -1,
    },
};

/* SOC mix all Line IN source  of input setting */
static struct route_setting soc_mix_line_all_input_wm8988[] = {
    {
        .ctl_name = -1,
    },
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
        .intval = 100,
    },
    {
        .ctl_name = LEFT_PGA_MUX,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_PGA_MUX,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = RIGHT_LINE_MUX,
        .intval = 2,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_PLAYBACK_SWITCH,
        .intval = 0,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH,
        .intval = 1,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_SWITCH,
        .intval = 1,
    },
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 0,
//    },
    {
        .ctl_name = -1,
    },
};

/* Volume  setting function of PCM */
static struct route_setting volume_pcm_wm8988[] = {
    {
        .ctl_name = PCM_VOLUME,
    },
    {
        .ctl_name = -1,
    },
};
/* Volume  setting function of OUT 1 */
static struct route_setting volume_out_1_wm8988[] = {
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
    },
    {
        .ctl_name = -1,
    },
};
/* Volume  setting function of OUT 2 */
static struct route_setting volume_out_2_wm8988[] = {
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
    },
    {
        .ctl_name = -1,
    },
};
/* Volume  setting function of OUT all */
static struct route_setting volume_out_all_wm8988[] = {
    {
        .ctl_name = OUTPUT_1_PLAYBACK_VOLUME,
    },
    {
        .ctl_name = OUTPUT_2_PLAYBACK_VOLUME,
    },
    {
        .ctl_name = -1,
    },
};
/* Volume  setting function of OUT all */
static struct route_setting volume_bypass_1_wm8988[] = {
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_VOLUME,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_VOLUME,
    },
    {
        .ctl_name = -1,
    },
};
/* Volume  setting function of OUT all */
static struct route_setting volume_bypass_2_wm8988[] = {
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_VOLUME,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_VOLUME,
    },
    {
        .ctl_name = -1,
    },
};
/* Volume  setting function of OUT all */
static struct route_setting volume_bypass_all_wm8988[] = {
    {
        .ctl_name = LEFT_MIXER_LEFT_BYPASS_VOLUME,
    },
    {
        .ctl_name = LEFT_MIXER_RIGHT_BYPASS_VOLUME,
    },
    {
        .ctl_name = RIGHT_MIXER_LEFT_BYPASS_VOLUME,
    },
    {
        .ctl_name = RIGHT_MIXER_RIGHT_BYPASS_VOLUME,
    },
    {
        .ctl_name = -1,
    },
};
/* Mute function of input setting */
static struct route_setting mute_wm8988[] = {
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 1,
//    },
    {
        .ctl_name = 0,
    },
};

/* Unmute function of input setting */
static struct route_setting unmute_wm8988[] = {
//    {
//        .ctl_name = DIGITAL_SOFT_MUTE,
//        .intval = 0,
//    },
    {
        .ctl_name = -1,
    },
};

struct __audio_control {
        const char *setting;
        struct route_setting *input_source;
} audio_control[] = {
        [ADUIO_INPUT_DEFAULT] = {
                .setting = "Default",
                .input_source = default_input_wm8988,
        },
        [ADUIO_INPUT_SOC] = {
                .setting = "SOC",
                .input_source = soc_input_wm8988,
        },
        [ADUIO_INPUT_LINE_IN_1] = {
                .setting = "LINE_IN_1",
                .input_source = line_in1_input_wm8988,
        },
        [ADUIO_INPUT_LINE_IN_2] = {
                .setting = "LINE_IN_2",
                .input_source = line_in2_input_wm8988,
        },
        [ADUIO_INPUT_SOC_MIX_LINE_IN_1] = {
                .setting = "SOC_MIX_LINE_IN_1",
                .input_source = soc_mix_line_in1_input_wm8988,
        },
        [ADUIO_INPUT_SOC_MIX_LINE_IN_2] = {
                .setting = "SOC_MIX_LINE_IN_2",
                .input_source = soc_mix_line_in2_input_wm8988,
        },
        [ADUIO_INPUT_SOC_MIX_LINE_IN_ALL] = {
                .setting = "SOC_MIX_LINE_IN_ALL",
                .input_source = soc_mix_line_all_input_wm8988,
        },
        [ADUIO_VOLUME_PCM] = {
                .setting = "VOULME_PCM",
                .input_source = volume_pcm_wm8988,
        },
        [ADUIO_VOLUME_OUT_ALL] = {
                .setting = "VOULME_OUT_ALL",
                .input_source = volume_out_all_wm8988,
        },
        [ADUIO_VOLUME_OUT_1] = {
                .setting = "VOULME_OUT_1",
                .input_source = volume_out_1_wm8988,
        },
        [ADUIO_VOLUME_OUT_2] = {
                .setting = "VOULME_OUT_2",
                .input_source = volume_out_2_wm8988,
        },
        [ADUIO_VOLUME_BYPASS_ALL] = {
                .setting = "VOLUME_BYPASS_ALL",
                .input_source = volume_bypass_all_wm8988,
        },
        [ADUIO_VOLUME_BYPASS_1] = {
                .setting = "VOLUME_BYPASS_1",
                .input_source = volume_bypass_1_wm8988,
        },
        [ADUIO_VOLUME_BYPASS_2] = {
                .setting = "VOLUME_BYPASS_2",
                .input_source = volume_bypass_2_wm8988,
        },
        [ADUIO_MUTE] = {
                .setting = "MUTE",
                //.input_source = mute_wm8988,
                .input_source = NULL,
        },
        [ADUIO_UNMUTE] = {
                .setting = "UNMUTE",
                //.input_source = unmute_wm8988,

                .input_source = NULL,
        },
};
struct __volume_control {
        const char *volume_level;
        unsigned int volume_value;
} volume_control[] = {
        [VOLUME_LEVEL_0] = {
                .volume_level = "VOLUME_LEVEL#0",
                .volume_value = 0,
        },
        [VOLUME_LEVEL_1] = {
                .volume_level = "VOLUME_LEVEL#1",
                .volume_value = 65,
        },
        [VOLUME_LEVEL_2] = {
                .volume_level = "VOLUME_LEVEL#2",
                .volume_value = 67,
        },
        [VOLUME_LEVEL_3] = {
                .volume_level = "VOLUME_LEVEL#3",
                .volume_value = 69,
        },
        [VOLUME_LEVEL_4] = {
                .volume_level = "VOLUME_LEVEL#4",
                .volume_value = 71,
        },
        [VOLUME_LEVEL_5] = {
                .volume_level = "VOLUME_LEVEL#5",
                .volume_value = 73,
        },
        [VOLUME_LEVEL_6] = {
                .volume_level = "VOLUME_LEVEL#6",
                .volume_value = 75,
        },
        [VOLUME_LEVEL_7] = {
                .volume_level = "VOLUME_LEVEL#7",
                .volume_value = 77,
        },
        [VOLUME_LEVEL_8] = {
                .volume_level = "VOLUME_LEVEL#8",
                .volume_value = 79,
        },
        [VOLUME_LEVEL_9] = {
                .volume_level = "VOLUME_LEVEL#9",
                .volume_value = 81,
        },
        [VOLUME_LEVEL_10] = {
                .volume_level = "VOLUME_LEVEL#10",
                .volume_value = 83,
        },
        [VOLUME_LEVEL_11] = {
                .volume_level = "VOLUME_LEVEL#11",
                .volume_value = 85,
        },
        [VOLUME_LEVEL_12] = {
                .volume_level = "VOLUME_LEVEL#12",
                .volume_value = 87,
        },
        [VOLUME_LEVEL_13] = {
                .volume_level = "VOLUME_LEVEL#13",
                .volume_value = 89,
        },
        [VOLUME_LEVEL_14] = {
                .volume_level = "VOLUME_LEVEL#14",
                .volume_value = 91,
        },
        [VOLUME_LEVEL_15] = {
                .volume_level = "VOLUME_LEVEL#15",
                .volume_value = 93,
        },
        [VOLUME_LEVEL_16] = {
                .volume_level = "VOLUME_LEVEL#16",
                .volume_value = 95,
        },
        [VOLUME_LEVEL_17] = {
                .volume_level = "VOLUME_LEVEL#17",
                .volume_value = 97,
        },
        [VOLUME_LEVEL_18] = {
                .volume_level = "VOLUME_LEVEL#18",
                .volume_value = 99,
        },
        [VOLUME_LEVEL_19] = {
                .volume_level = "VOLUME_LEVEL#19",
                .volume_value = 101,
        },
        [VOLUME_LEVEL_20] = {
                .volume_level = "VOLUME_LEVEL#20",
                .volume_value = 103,
        },
        [VOLUME_LEVEL_21] = {
                .volume_level = "VOLUME_LEVEL#21",
                .volume_value = 105,
        },
        [VOLUME_LEVEL_22] = {
                .volume_level = "VOLUME_LEVEL#22",
                .volume_value = 107,
        },
        [VOLUME_LEVEL_23] = {
                .volume_level = "VOLUME_LEVEL#23",
                .volume_value = 109,
        },
        [VOLUME_LEVEL_24] = {
                .volume_level = "VOLUME_LEVEL#24",
                .volume_value = 111,
        },
        [VOLUME_LEVEL_25] = {
                .volume_level = "VOLUME_LEVEL#25",
                .volume_value = 113,
        },
        [VOLUME_LEVEL_26] = {
                .volume_level = "VOLUME_LEVEL#26",
                .volume_value = 115,
        },
        [VOLUME_LEVEL_27] = {
                .volume_level = "VOLUME_LEVEL#27",
                .volume_value = 117,
        },
        [VOLUME_LEVEL_28] = {
                .volume_level = "VOLUME_LEVEL#28",
                .volume_value = 119,
        },
        [VOLUME_LEVEL_29] = {
                .volume_level = "VOLUME_LEVEL#29",
                .volume_value = 121,
        },
        [VOLUME_LEVEL_30] = {
                .volume_level = "VOLUME_LEVEL#30",
                .volume_value = 122,
        },
};
#endif
