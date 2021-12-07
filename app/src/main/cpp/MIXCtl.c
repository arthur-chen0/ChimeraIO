#include "MIXCtl.h"
#include "config_wm8988.h"
#define DEBUG 1
#define VOL_DEBUG 0
#define BUFFER_MAX      3
#define DIRECTION_MAX   100
#define LOW             0
#define HIGH            1

//static void tinymix_set_value(struct mixer *mixer, const char *control,const char *ID,const int *setID,const int *Values)
static void tinymix_set_value(struct mixer *mixer, const char *control, const int card, const int setID, const int Values)
{
        if(DEBUG)LOGI("[%s] Entry", __func__);

    struct mixer_ctl *ctl;
    enum mixer_ctl_type type;
    unsigned int num_ctl_values;
    unsigned int i;
    unsigned int num_values= 1;

    if(DEBUG)LOGD("[%s] mixer get ctl \n", __func__);
    ctl = mixer_get_ctl(mixer, setID);

    if (!ctl) {
        LOGE("[%s] Invalid mixer control\n", __func__);
        return;
    }

    type = mixer_ctl_get_type(ctl);
    num_ctl_values = mixer_ctl_get_num_values(ctl);

        if (num_values == 1)
        {
            /* Set all values the same */
            int value = Values;
            for (i = 0; i < num_ctl_values; i++)
            {
                if (mixer_ctl_set_value(ctl, i, value)) {
                    LOGE(stderr, "Error: invalid value\n");
                    return;
                }
            }
        }
}

static int tinymix_get_value(struct mixer *mixer, const char *control,const int card, const int setID)
{
    if(DEBUG)LOGI("[%s] Entry", __func__);

    struct mixer_ctl *ctl;
    enum mixer_ctl_type type;
    unsigned int num_ctl_values;
    unsigned int i;
    unsigned int num_values= 1;
    int get_value = 0;

    if(DEBUG)LOGD("[%s] mixer get ctl \n", __func__);
        ctl = mixer_get_ctl(mixer, setID);

    if (!ctl) {
        LOGE("[%s] Invalid mixer control\n", __func__);
        return -1;
    }

    type = mixer_ctl_get_type(ctl);
    num_ctl_values = mixer_ctl_get_num_values(ctl);

    if (num_values == 1)
    {

        /* Set all values the same */
        // int value = Values;
        for (i = 0; i < num_ctl_values; i++)
        {
            get_value = mixer_ctl_get_value(ctl, i);
            if(DEBUG)LOGI("[%s]  Get value from tinymix: %d \n", __func__, get_value);
            return get_value;

        }
    }

	return 0;
}

/*
 * Reference from MIXCtl.h Part of AMPER Control
*/
static int audio_dev_pen(int dev_gpio,const int value)
{
    if(DEBUG) LOGI("[audio_dev_pen] Audio device power control \n");

    static const char values_str[] = "01";
    char path[DIRECTION_MAX];
    int fd;

    LOGI("Device : [%s] , Value %d ", audio_device[dev_gpio].dev_name, value);
    snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/value", audio_device[dev_gpio].dev_gpio);
    fd = open(path, O_WRONLY);
    if (fd < 0) {
        LOGE("Failed to open gpio value for writing1!\n");
        return 1;
    }

    if (write(fd, &values_str[value == LOW ?  0 : 1], 1) < 0) {
        LOGE("Failed to write value!\n");
        return 1;
    }

    audio_device[dev_gpio].dev_status = value;
    if(DEBUG) LOGI("Device : [ %s ] Value : %d ", audio_device[dev_gpio].dev_name, audio_device[dev_gpio].dev_status);
    close(fd);
    return 0;
}

static void mute_fun_determine(int muteFunction){
//    if(muteFunction == 0) {
//        mute_fun_support = MUTE_FUN_DISABLE;
//    }
//    else {
        mute_fun_support = MUTE_FUN_ENABLE;
//    }
}

static int audio_mute(int mode)
{
    if(DEBUG)LOGI("[Audio_Mute] Entry ");
    struct mixer *mixer;
    int j = 0;
    int i = 0;

    /**
        * For Chimera HW VerF headphone plug in/out that mute function to set audio/mute mode
        * GPIO control for audio mute function
        * --------------------------
        * | SEL2 | SEL1 | Mode |
        * | 0    | 0    | Shutdown |
        * | 0    | 1    | Click pop  |
        * | 1    | 0    | Mute         |
        * | 1    | 1    | Audio        |
        * --------------------------
        */
    if( mute_fun_support == MUTE_FUN_ENABLE ){
        LOGI("[Audio_Mute] mode :%d ", mode);
        if( mode == MUTE_FUN_MUTE_MODE ){
            usleep(300 * 1000);
            audio_dev_pen( MUTE_SEL_2, GPIO_ST_DISABLE);
        } else if(mode == MUTE_FUN_AUDIO_MODE){
            usleep(150 * 1000);
            audio_dev_pen( MUTE_SEL_2, GPIO_ST_ENABLE);
        }
        return 0;
    } else if (mute_fun_support == MUTE_FUN_DISABLE){
        LOGI("[Audio_Mute] Unsupport Mute function");
        return -1;
    }
	return 0;
}

/*
 * Class:     JniCodecControl
 * Method:    audioControl
 */
jint
Java_com_jht_chimera_io_audio_AudioSourceCodec_audioControl(JNIEnv* env,jobject thiz, jint ctlMethod, jint muteFunction)
{
    if(DEBUG) LOGI("Set MIX Entry ");

    struct mixer *mixer;
    int j = 0;
    int i = 0;
    int v = 0;
    int ret = 0;
    struct route_setting *route;
    /* card : 0 -> audio_codec wm8988 */
    int card = 2;

    LOGI("[Codec Lib][audioControl] Beginning ------");
    LOGI("Open Card : %d ", card);
    LOGI("[Codec Lib][audioControl] [Mute]:%d, [volSetting]:%d", audioMute_Flag, volumeSettingFlag);

    // When Audio Library init first time, we were check the mute function supported or not
    if(!audioInitFlag){
        LOGI("[audioControl] To determine support mute function or not");
        audioInitFlag = 1;
        mute_fun_determine(muteFunction);
    }
    mixer = mixer_open(card);

    if (!mixer) {
        LOGE("Open Mixer failed \n");
        return -1;
    }
    if(DEBUG)LOGI("[audioControl] Num : %d ", ctlMethod);
    if(ctlMethod == ADUIO_MUTE) {
        LOGI("[audioControl] ADUIO_MUTE");
        /* Mute */
        usleep(500);
        audioMute_Flag = MUTE_FUN_MUTE_MODE;
        audio_mute(MUTE_FUN_MUTE_MODE);
    } else if(ctlMethod == ADUIO_UNMUTE) {
        LOGI("[audioControl] ADUIO_UNMUTE");
        // The out-put 1 and out-put 2 of volume value is same, so we just get out-put 1 to be reference.
        /* Unmute */
        usleep(500);
        audioMute_Flag = MUTE_FUN_AUDIO_MODE;
        audio_mute(MUTE_FUN_AUDIO_MODE);
    } else {
        if(DEBUG)LOGI("[audioControl]  Other API");

        LOGI("[audioControl] Mute, then set [%s] ", audio_control[ctlMethod].setting);
        /* Mute function */
        audio_mute(MUTE_FUN_MUTE_MODE);

        /* Get setting table */
        route = audio_control[ctlMethod].input_source;
        if(!route) {
            LOGI("[audioControl] Get route failed ");
            return -1;
        }

        /* Set audio source */
        while (route[i].ctl_name >=0){
            /* Reset the audio volume setting */
            LOGI("[audioControl] Normal setting ");
            if(DEBUG)LOGI("[audioControl] route[ctlMethod].ctl_name: %d, route[ctlMethod].intval: %d?�", route[i].ctl_name, route[i].intval);

            /* Delay few seconds when the codec switch the source channel */
            if( route[i].ctl_name == LEFT_MIXER_LEFT_PLAYBACK_SWITCH || route[i].ctl_name == LEFT_MIXER_RIGHT_PLAYBACK_SWITCH ||
                route[i].ctl_name == RIGHT_MIXER_LEFT_PLAYBACK_SWITCH || route[i].ctl_name == RIGHT_MIXER_RIGHT_PLAYBACK_SWITCH ||
                route[i].ctl_name == LEFT_MIXER_LEFT_BYPASS_SWITCH || route[i].ctl_name == LEFT_MIXER_RIGHT_BYPASS_SWITCH ||
                route[i].ctl_name == RIGHT_MIXER_LEFT_BYPASS_SWITCH || route[i].ctl_name == RIGHT_MIXER_RIGHT_BYPASS_SWITCH ){
                usleep(6 * 1000);
            }
            tinymix_set_value(mixer,"-D", card,route[i].ctl_name, route[i].intval);
            i++;
        }
        /**
                * Audio Mute Flag :  0 -> MUTE_FUN_MUTE_MODE
                *                 :  1 -> MUTE_FUN_AUDIO_MODE
                */
        if (ctlMethod == ADUIO_INPUT_DEFAULT){
            LOGI("[audioControl]  Default setting done");
            /* Set Default Volume finish */
            /* Default setting -> Mute mode */
            audioMute_Flag = MUTE_FUN_MUTE_MODE;
        } else {
            /* We will set mute function to audio mode that the mute of flag to be audio mode */
            if( audioMute_Flag == MUTE_FUN_AUDIO_MODE){
                audio_mute(MUTE_FUN_AUDIO_MODE);
            }
        }
        mixer_close(mixer);
    }
    LOGI("[Codec Lib] Done ------");
    return 0;
}

/*
 * Class:     JniCodecControl
 * Method:    volumeControl
 */
jint
Java_com_jht_chimera_service_hal_lib_audioselect_AudioSourceCodec_volumeControl(JNIEnv* env,jobject thiz, jint ctlMethod, jint level, jint muteFunction)
{
    struct mixer *mixer;
    int j = 0;
    int i = 0;
    int record_value = 0;
    int pre_value = 0;
    int target_value = 0;
    struct route_setting *route;
    /* card : 0 -> audio_codec */
    int card = 0;
    LOGI("[Codec Lib][volumeControl] Beginning------");
    if(VOL_DEBUG)LOGI("[Codec Lib][volumeControl] Open Card : %d  \n ", card);
    if(VOL_DEBUG)LOGI("[Codec Lib][volumeControl] [Mute]:%d, [volSetting]:%d", audioMute_Flag, volumeSettingFlag);

    // When Audio Library init first time, we were check the mute function supported or not
    if(!audioInitFlag){
        LOGI("[volumeControl] To determine support mute function or not");
        audioInitFlag = 1;
        mute_fun_determine(muteFunction);
    }
    mixer = mixer_open(card);

    if (!mixer) {
        LOGE("Open Mixer failed \n");
        return -1;
    }

    LOGI("[volumeControl] Volume Control: [%s] , Level: [%d], Value: [%d]", audio_control[ctlMethod].setting, level, volume_control[level].volume_value);
    /* Get target value by the volume level */
    target_value = volume_control[level].volume_value;

    route = audio_control[ctlMethod].input_source;

    if(!route) {
    LOGE("[volumeControl] Get route failed \n");
        return -1;
    }

    // The volume of level is LEVEL#0 that we set to mute mode
    if( level == VOLUME_LEVEL_0 ){
        /* Mute function */
        usleep(50 * 1000);
        audio_mute(MUTE_FUN_MUTE_MODE);
        if(VOL_DEBUG)LOGI("[volumeControl] Set Mute mode success");
		tinymix_set_value(mixer,"-D", card, OUTPUT_1_PLAYBACK_VOLUME, 0);
        tinymix_set_value(mixer,"-D", card, OUTPUT_2_PLAYBACK_VOLUME, 0);

    // The other level of volume will setting been here
    } else {
        /* Audio function */
        if( audioMute_Flag == MUTE_FUN_AUDIO_MODE ){
            while (route[i].ctl_name){
                //LOGI("[Codec Lib] [Volume Control] Get Record Value------");
                record_value = tinymix_get_value(mixer,"-D", card,route[i].ctl_name);
                LOGI("[Volume Control] [%d] Record Value : %d ------", route[i].ctl_name, record_value);
                if( target_value == record_value){
                    if(VOL_DEBUG)LOGI("[Volume Control]   Same Value, we do not need to set");
                }else if(target_value > record_value){
                    for(pre_value = record_value; pre_value <= target_value; pre_value++){
                        if(VOL_DEBUG)LOGI("[Volume Control] [%d] Set Value ++: %d ------", route[i].ctl_name, pre_value);
                        tinymix_set_value(mixer,"-D", card, route[i].ctl_name, pre_value);
                        usleep(500);
                    }
                } else if (target_value < record_value){
                    for(pre_value = record_value; pre_value >= target_value; pre_value--){
                        if(VOL_DEBUG)LOGI("[Volume Control] [%d] Set Value -- : %d ------", route[i].ctl_name, pre_value);
                        tinymix_set_value(mixer,"-D", card,route[i].ctl_name, pre_value);
                        usleep(500);
                    }
                }
                i++;
            }
        }
    }

    // We set volume level to be other from level#0 that we will set mute function to audio mode
    if( audioMute_Flag == MUTE_FUN_AUDIO_MODE && volumeSettingFlag == VOLUME_SETTING_NORMAL){
        if(VOL_DEBUG)LOGI("[volumeControl] Audio mode");
        // Set audio mode after record volume setting
        audio_mute(MUTE_FUN_AUDIO_MODE);
    } else {
        if(VOL_DEBUG)LOGI("[volumeControl] Audio still normal mode");
    }
    mixer_close(mixer);
    if(VOL_DEBUG)LOGI("[Codec Lib][volumeControl] [Mute]:%d, [volSetting]:%d", audioMute_Flag, volumeSettingFlag);
    LOGI("[Codec Lib] Volume Setting Done ------");
    return 0;
}

/*
 * Class:     JniCodecControl
 * Method:    audioAmerEnable
 */
jstring
Java_com_jht_chimera_service_hal_lib_audioselect_AudioSourceCodec_getVersion(JNIEnv* env,jobject thiz)
{
    if(DEBUG)LOGI("[Codec Lib][getVersion] Entry ");
    const char *audio_lib_version;
    audio_lib_version = AUDIO_LIB_VERSION;
    LOGI("[Codec Lib][getVersion] Version : %s ", audio_lib_version);
    return (*env)->NewStringUTF(env, audio_lib_version);
}

/*
 * Class:     JniCodecControl
 * Method:    audioAmerDisable
 */
jint
Java_com_jht_chimera_service_hal_lib_audioselect_AudioSourceCodec_audioAmerDisable(JNIEnv* env,jobject thiz)
{
    int ret = 0;
    int j = 0;
    if(DEBUG)LOGI("[ audioAmerDisable ]  Entry ");

    for(j = 0; j < AMPER_GPIO_COUNTER; j++){
        ret = audio_dev_pen(j,GPIO_ST_DISABLE);
        if(ret){
            LOGI("[ audioAmerDisable ]  Set failed ");
        }
    }
    return 0;
}
/*-------------------------------------------For GPIO Control------------------------------------------------*/

jint
Java_com_jht_chimera_service_hal_lib_audioselect_AudioSourceCodec_readGpio(JNIEnv* env,jobject thiz, jint gpioName)
{
    LOGI(" Read GPIO Entry \n");

    char path[DIRECTION_MAX];
    char value_str[3];
    int fd;
    snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/value", gpioName);
    fd = open(path, O_RDONLY);
    if (fd < 0) {
        LOGE("failed to open gpio value for reading!\n");
        return -1;
    }
    if (read(fd, value_str, 3) < 0) {
        LOGE("failed to read value!\n");
        return -1;
    }
    close(fd);
    return (atoi(value_str));
}

jint
Java_com_jht_chimera_service_hal_lib_audioselect_AudioSourceCodec_writeGpio(JNIEnv* env,jobject thiz, jint gpioName, jint value)
{
    LOGI(" Write GPIO Entry \n");

    static const char values_str[] = "01";
        char path[DIRECTION_MAX];
        int fd;
        snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/value", gpioName);
        fd = open(path, O_WRONLY);
        if (fd < 0) {
            LOGE("failed to open gpio value for writing2!\n");
            return 0;
        }
        if (write(fd, &values_str[value == LOW ? 0 : 1], 1) < 0) {
            LOGE("failed to write value!\n");
            return 0;
        }
        close(fd);
        return 1;
}

