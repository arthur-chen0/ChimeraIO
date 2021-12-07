#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <termios.h>
#include <fcntl.h>              /* low-level i/o */
#include <unistd.h>
#include <errno.h>
#include <malloc.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <asm/types.h>          /* for videodev2.h */
#include <linux/usbdevice_fs.h>
#include "tinyalsa/asoundlib.h"

#include <ctype.h>
#include "config_wm8988.h"

#define  LOG_TAG    "nexcomAudioLib"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

/* Audio Codec Library Version */
#define AUDIO_LIB_VERSION "0.3.2"

/* For GPIO Control of AMPER */
#define AMPER_GPIO_COUNTER  2
#define AMPER_GPIO_1        0
#define AMPER_GPIO_2        1
#define MUTE_SEL_1          2
#define MUTE_SEL_2          3
#define GPIO_ST_DISABLE     0
#define GPIO_ST_ENABLE      1

/* Mute function control */
#define MUTE_FUN_DISABLE    0
#define MUTE_FUN_ENABLE     1
#define MUTE_FUN_MUTE_MODE  0
#define MUTE_FUN_AUDIO_MODE 1

#define VOLUME_SETTING_MUTE 0
#define VOLUME_SETTING_NORMAL 1
#define DATA_LENGTH 30

/* MCU Version */
#define MCU_SUPPORT_VER_E 30
#define MCU_SUPPORT_VER_F 50


/**
 * Structure of terminal input / output system.
 */
struct termios oldtio , newtio;

/* Audio Mute function flag, 0 -> Mute Mode, 1 -> Audio Mode */
int audioMute_Flag = MUTE_FUN_AUDIO_MODE;
int volumeSettingFlag = VOLUME_SETTING_MUTE;
/* Audio codec library initialize first time */
int audioInitFlag = 0;
int mute_fun_support = MUTE_FUN_DISABLE;


static struct __audio_device {
        const char *dev_name;
        unsigned int dev_gpio;
        unsigned int dev_status;

} audio_device[] = {
        [AMPER_GPIO_1] = {
                .dev_name = "AMPER1",         /* For Connector : J20 */
                .dev_gpio = 109,                /* GPIO */
                .dev_status = GPIO_ST_DISABLE, /* Default Status */
        },
        [AMPER_GPIO_2] = {
                .dev_name = "AMPER2",           /* For Connector : J21*/
                .dev_gpio = 64,                   /* GPIO */
                .dev_status = GPIO_ST_DISABLE,   /* Default Status */
        },
        [MUTE_SEL_1] = {
                .dev_name = "MUTE_SEL1",       /* Mute function SEL1*/
                .dev_gpio = 0,                   /* GPIO */
                .dev_status = GPIO_ST_DISABLE,   /* Default Status */
        },
        [MUTE_SEL_2] = {
                .dev_name = "MUTE_SEL2",       /* Mute function SEL2*/
                .dev_gpio = 1,                   /* GPIO */
                .dev_status = GPIO_ST_DISABLE,   /* Default Status */
        },
};

//static void tinymix_set_value(struct mixer *mixer, const char *control,const char *ID,const int *setID,const int *Values);
//static int tinymix_get_value(struct mixer *mixer, const char *control,const char *ID,const int *setID);
static void tinymix_set_value(struct mixer *mixer, const char *control, const int card, const int setID, const int Values);
static int tinymix_get_value(struct mixer *mixer, const char *control,const int card, const int setID);


static int switch_amp_pen(int dev_gpio,const int *value);
static int audio_mute(int mode);
void controlaudio (int set);

/* Function control via Audio Codec */
jint Java_com_jht_chimera_io_audio_AudioSourceCodec_audioControl(JNIEnv* env,jobject thiz, jint ctlMethod, jint muteFunction);
jint Java_com_jht_chimera_io_audio_AudioSourceCodec_volumeControl(JNIEnv* env,jobject thiz, jint ctlMethod, jint level, jint muteFunction);
jstring Java_com_jht_chimera_io_audio_AudioSourceCodec_getVersion(JNIEnv* env,jobject thiz);

/* Capture and play function */
void Java_com_jht_chimera_io_audio_AudioSourceCodec_captureConfigureOpen(JNIEnv* env,jobject thiz, jint rate);
void Java_com_jht_chimera_io_audio_AudioSourceCodec_captureConfigureClose(JNIEnv* env,jobject thiz);
void Java_com_jht_chimera_io_audio_AudioSourceCodec_capturePlaybackStart(JNIEnv* env,jobject thiz);
void Java_com_jht_chimera_io_audio_AudioSourceCodec_capturePlaybackStop(JNIEnv* env,jobject thiz);

/* Function control via GPIO */
jint Java_com_jht_chimera_io_audio_AudioSourceCodec_audioAmerEnable(JNIEnv* env,jobject thiz);
jint Java_com_jht_chimera_io_audio_AudioSourceCodec_audioAmerDisable(JNIEnv* env,jobject thiz);
jint Java_com_jht_chimera_io_audio_AudioSourceCodec_readGpio(JNIEnv* env,jobject thiz, jint gpioName);
jint Java_com_jht_chimera_io_audio_AudioSourceCodec_writeGpio(JNIEnv* env,jobject thiz, jint gpioName, jint value);


