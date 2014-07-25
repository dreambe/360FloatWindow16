/*
 * fbshot.c -- FrameBuffer Screen Capture Utility
 * (C)opyright 2002 sfires@sfires.net
 *
 * Originally Written by: Stephan Beyer <PH-Linex@gmx.net>
 * Further changes by: Paul Mundt <lethal@chaoticdreams.org>
 * Rewriten and maintained by: Dariusz Swiderski <sfires@sfires.net>
 * Modular version by: Karol Kuczmarski <karol.kuczmarski@polidea.pl>
 *
 * 	This is a simple program that generates a
 * screenshot of the specified framebuffer device and
 * terminal and writes it to a specified file using
 * the PNG format.
 *
 * See ChangeLog for modifications, CREDITS for credits.
 *
 * fbshot is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Version 2
 * of the License, or (at your option) any later version.
 *
 * fbshot is distributed in the hope that it will be useful, but
 * WITHOUT ANY  WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License with fbshot; if not, please write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <unistd.h>
#include <getopt.h>
#include <fcntl.h>
#include <byteswap.h>
#include <sys/types.h>
#include <asm/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <mntent.h>
#include <errno.h>
#include <sys/utsname.h>

#include <sys/vt.h>
#include <linux/fb.h>

//android include
#include "screenshot.h"
#include <android/log.h>


#define PACKAGE 	"fbshot"
#define VERSION 	"0.3.1"
#define MAINTAINER_NAME "Dariusz Swiderski"
#define MAINTAINER_ADDR "sfires@sfires.net"

//android define
#define DEFAULT_FB      "/dev/graphics/fb0"
#define DEBUG
#define TAG "Screenshot_JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_WARN,TAG,__VA_ARGS__)
#define SCREENSHOT_CLASS_NAME "pkg/screenshot/Screenshot"
#define METHOD_SET_HEADER_NAME "setHeader"
#define METHOD_SET_HEADER_SIGN "(III)V"
#define METHOD_COPY_BUFFER_NAME "copyBuffer"
#define METHOD_COPY_BUFFER_SIGN "([B)Z"
//add opt
//#define AVOID_SAME_SCREENSHOT


/* some conversion macros */
#define RED565(x)    ((((x) >> (11 )) & 0x1f) << 3)
#define GREEN565(x)  ((((x) >> (5 )) & 0x3f) << 2)
#define BLUE565(x)   ((((x) >> (0)) & 0x1f) << 3)

#define ALPHA1555(x) ((((x) >> (15)) & 0x1 ) << 0)
#define RED1555(x)   ((((x) >> (10)) & 0x1f) << 3)
#define GREEN1555(x) ((((x) >> (5 )) & 0x1f) << 3)
#define BLUE1555(x)  ((((x) >> (0 )) & 0x1f) << 3)

#ifdef AVOID_SAME_SCREENSHOT
char* previousBuffer = NULL;
#endif


static int waitbfg = 0; /* wait before grabbing (for -C )... */

struct picture {
    int xres, yres;
    char *buffer;
    struct fb_cmap *colormap;
    char bps, gray;
};

unsigned int create_bitmask(struct fb_bitfield* bf) {
    return ~(~0u << bf->length) << bf->offset;
}

// Unifies the picture's pixel format to be 32-bit ARGB

int unify(struct picture *pict, struct fb_var_screeninfo *fb_varinfo) {

    __u32 red_mask, green_mask, blue_mask;
    __u32 c;
    __u32 r, g, b;
    __u32* out;
    int i, j = 0, bytes_pp;

    
    // build masks for extracting colour bits
    red_mask = create_bitmask(&fb_varinfo->red);
    green_mask = create_bitmask(&fb_varinfo->green);
    blue_mask = create_bitmask(&fb_varinfo->blue);

    // go through the image and put the bits in place
    out = (__u32*) malloc(pict->xres * pict->yres * sizeof (__u32));
    if (out == NULL) {
        LOGE("out allocate memory , Error:[%s]", strerror(errno));
        free(pict->buffer);
        return pkg_screenshot_Screenshot_SCREENSHOT_ERROR_OUTOFMEMORY;
    }
    bytes_pp = pict->bps >> 3;
    for (i = 0; i < pict->xres * pict->yres * bytes_pp; i += bytes_pp) {

        memcpy(((char*) &c) + (sizeof (__u32) - bytes_pp), pict->buffer + i, bytes_pp);

        // get the colors
        r = ((c & red_mask) >> fb_varinfo->red.offset) & ~(~0u << fb_varinfo->red.length);
        g = ((c & green_mask) >> fb_varinfo->green.offset) & ~(~0u << fb_varinfo->green.length);
        b = ((c & blue_mask) >> fb_varinfo->blue.offset) & ~(~0u << fb_varinfo->blue.length);

        // format the new pixel
        out[j++] = (0xFF << 24) | (b << 16) | (g << 8) | r;
    }

    //idiottiger 
    //WARN need free first
    free(pict->buffer);

    pict->buffer = (char*) out;
    pict->bps = 32;

    return pkg_screenshot_Screenshot_SCREENSHOT_OK;
}

int read_fb(char *device, int vt_num, struct picture *pict) {
    int fd, vt_old, i, j;
    struct fb_fix_screeninfo fb_fixinfo;
    struct fb_var_screeninfo fb_varinfo;

    if (!(fd = open(device, O_RDONLY))) {
        LOGE("Couldn't open framebuffer device, Error:[%s]", strerror(errno));
        return pkg_screenshot_Screenshot_SCREENSHOT_ERROR_PERMISSION;
    }
    if (ioctl(fd, FBIOGET_FSCREENINFO, &fb_fixinfo)) {
        LOGE("ioctl FBIOGET_FSCREENINFO, Error:[%s]", strerror(errno));
        return pkg_screenshot_Screenshot_SCREENSHOT_ERROR_PERMISSION;
    }
    if (ioctl(fd, FBIOGET_VSCREENINFO, &fb_varinfo)) {
        LOGE("ioctl FBIOGET_VSCREENINFO, Error:[%s]", strerror(errno));
        return pkg_screenshot_Screenshot_SCREENSHOT_ERROR_PERMISSION;
    }

    pict->xres = fb_varinfo.xres;
    pict->yres = fb_varinfo.yres;
    pict->bps = fb_varinfo.bits_per_pixel;
    pict->gray = fb_varinfo.grayscale;

    if (fb_fixinfo.visual == FB_VISUAL_PSEUDOCOLOR) {
        pict->colormap = (struct fb_cmap*) malloc(sizeof (struct fb_cmap));
        pict->colormap->red = (__u16*) malloc(sizeof (__u16)*(1 << pict->bps));
        pict->colormap->green = (__u16*) malloc(sizeof (__u16)*(1 << pict->bps));
        pict->colormap->blue = (__u16*) malloc(sizeof (__u16)*(1 << pict->bps));
        pict->colormap->transp = (__u16*) malloc(sizeof (__u16)*(1 << pict->bps));
        pict->colormap->start = 0;
        pict->colormap->len = 1 << pict->bps;
        if (ioctl(fd, FBIOGETCMAP, pict->colormap)) {
            LOGE("ioctl FBIOGETCMAP, Error:[%s]", strerror(errno));
            return pkg_screenshot_Screenshot_SCREENSHOT_ERROR_UNKNOWN;
        }
    }

    switch (pict->bps) {
        case 15:
            i = 2;
            break;
        default:
            i = pict->bps >> 3;
    }

    if (!(pict->buffer = malloc(pict->xres * pict->yres * i))) {
        LOGE("picture buffer allocate memory , Error:[%s]", strerror(errno));
        return pkg_screenshot_Screenshot_SCREENSHOT_ERROR_OUTOFMEMORY;
    }

    j = (read(fd, pict->buffer, ((pict->xres * pict->yres) * i)) !=
            (pict->xres * pict->yres * i));

    close(fd);

    return unify(pict, &fb_varinfo);
}

int TakeScreenshot(char* device, struct picture* pict) {
    int vt_num = -1;
    return read_fb(device, vt_num, pict);
}

int compareBuffer(char *pre, char *current, int size) {
    char* p1 = pre;
    char* p2 = current;
    //LOGI("loop before size:%d", size);
    while ((size-- > 0) && *(p1++) == *(p2++)) {
    };
    //LOGI("size:%d", size);
    return (size <= 0) ? 0 : 1;
}

/*
 * Class:     pkg_screenshot_Screenshot
 * Method:    takeScreenshot
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_pkg_screenshot_Screenshot_takeScreenshot
(JNIEnv *env, jobject screenShotObj) {
    LOGI("start take screen shot");

    struct picture pict;

    jint result = TakeScreenshot(DEFAULT_FB, &pict);
    LOGI("take screen shot result:%d", result);
    if (result == pkg_screenshot_Screenshot_SCREENSHOT_OK) {


#ifdef AVOID_SAME_SCREENSHOT
        int buffer_size = pict.xres * pict.yres;
        if (previousBuffer != NULL) {
            int comp = compareBuffer(previousBuffer, pict.buffer, buffer_size);
            //int comp = strncmp(previousBuffer, pict.buffer, buffer_size);
            //if same, free buffer, and dont free previous buffer and return
            //not same, free previous buffer, and pointer to new
            if (comp == 0) {
                free(pict.buffer);
                LOGI("twice screen shot are same");
                return pkg_screenshot_Screenshot_SCREENSHOT_ARE_SAME;
            } else {
                free(previousBuffer);
            }
        }
        previousBuffer = pict.buffer;
#endif

        int size = pict.xres * pict.yres * pict.bps / 8;
        //LOGI("begin copy buffer to java level");


        jbyteArray byte_array = (*env)->NewByteArray(env, size);
        if (byte_array == NULL) {
            free(pict.buffer);
            LOGE("create java byte array to save buffer OOM");
            return pkg_screenshot_Screenshot_SCREENSHOT_ERROR_OUTOFMEMORY;
        }

        //copy pictue buffer to byte array
        jboolean isCopy;
        void *data = (*env)->GetPrimitiveArrayCritical(env, (jarray) byte_array, &isCopy);
        memcpy(data, pict.buffer, size);

        //free the pict buffer and critical array 
#ifndef AVOID_SAME_SCREENSHOT        
        free(pict.buffer);
#endif
        (*env)->ReleasePrimitiveArrayCritical(env, byte_array, data, 0);

        static jclass screenShotClass = NULL;
        static jmethodID method_setHeader = NULL;
        static jmethodID method_copyBuffer = NULL;
        if (screenShotClass == NULL) {
            screenShotClass = (*env)->FindClass(env, SCREENSHOT_CLASS_NAME);
        }
        if (method_setHeader == NULL) {
            method_setHeader = (*env)->GetMethodID(env, screenShotClass, METHOD_SET_HEADER_NAME, METHOD_SET_HEADER_SIGN);
            if (method_setHeader == NULL) {
                LOGE("dont have method [Name:%s,Sign:%s]", METHOD_SET_HEADER_NAME, METHOD_SET_HEADER_SIGN);
                exit(1);
            }
        }
        if (method_copyBuffer == NULL) {
            method_copyBuffer = (*env)->GetMethodID(env, screenShotClass, METHOD_COPY_BUFFER_NAME, METHOD_COPY_BUFFER_SIGN);
            if (method_copyBuffer == NULL) {
                LOGE("dont have method [Name:%s,Sign:%s]", METHOD_COPY_BUFFER_NAME, METHOD_COPY_BUFFER_SIGN);
                exit(1);
            }
        }

        jint w = pict.xres, h = pict.yres, config = pict.bps;
        if (method_setHeader != NULL) {
            LOGI("call method:%s", METHOD_SET_HEADER_NAME);
            (*env)->CallVoidMethod(env, screenShotObj, method_setHeader, w, h, config);
        }
        if (method_copyBuffer != NULL) {
            LOGI("call method:%s", METHOD_COPY_BUFFER_NAME);
            jboolean copy_result = (*env)->CallBooleanMethod(env, screenShotObj, method_copyBuffer, byte_array);
            if (copy_result == JNI_FALSE) {
                (*env)->DeleteLocalRef(env, byte_array);
                return pkg_screenshot_Screenshot_SCREENSHOT_ERROR_OUTOFMEMORY;
            }
        }

        (*env)->DeleteLocalRef(env, byte_array);
        return pkg_screenshot_Screenshot_SCREENSHOT_OK;
    } else {
        return result;
    }
    
}

/*
 * Class:     pkg_screenshot_Screenshot
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_pkg_screenshot_Screenshot_free
(JNIEnv *env, jobject screenShotObj) {
#ifdef AVOID_SAME_SCREENSHOT
    if (previousBuffer != NULL) {
        free(previousBuffer);
        previousBuffer = NULL;
        LOGI("release previous buffer...");
    }
#else
    LOGI("release nothing...");
#endif

}