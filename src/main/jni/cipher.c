#include <string.h>
#include <jni.h>
#include "md5.h"

/*
 *  @Author xubin
 */

typedef signed char byte;

static byte FAKE_AES_KEY[] = {
        45, 96, 32, 92, 78, 10, 15, 93, 119, 86, 54, 111, 116, 56, 42, 84
};

static byte TRUE_AES_KEY[] = {
        48, 150, 68, 189, -87, 198, 226, 223, -10, 107, 81, 217, 91, 208, 80, 69
};

static byte TRUE_AES_KEY1[] = {
        61, -1, 237, 209, 1, -15, 84, 60, 167, -59, 255, 124, 62, -115, 41, 122
};

static byte TRUE_AES_KEY2[] = {
        230, 230, 44, 78, 235, 163, 3, 228, 189, 52, -57, 247, 114, 39, -67, 125
};

static byte TRUE_AES_KEY3[] = {
        134, 143, 228, 42, 100, 98, 187, 13, 122, 63, 253, -8, 100, 87, 123, 27
};

static unsigned char DAILY_CAST_SIGNATURE_MD5[16] = {
        0x1f, 0xfa, 0x2a, 0x3d, 0x81, 0x0f, 0xdd, 0x3f, 0x33, 0x8a, 0xc7, 0x07, 0xa5, 0xd1, 0x02,
        0xcf
};

static unsigned char DAILY_CAST_SIGNATURE_MD5_NEW[16] = {
        0x7a, 0xf0, 0xf0, 0x7f, 0x14, 0xe5, 0x1b, 0xb6, 0xc6, 0xc5, 0xb0, 0x79, 0x58, 0x60, 0xcb,
        0xd8
};

static unsigned char *cur_signature_md5 = NULL;

static int isOfficialSignature(unsigned char *cur_signature_md5) {
//    if (memcmp(cur_signature_md5, DAILY_CAST_SIGNATURE_MD5, sizeof(DAILY_CAST_SIGNATURE_MD5)) ==
//        0) {
//        return 1;
//    }
    return 1;
}

static int isOfficialPackageName(const char *packagename) {
    if (strncmp(packagename, "com.suapp", strlen("com.suapp")) == 0) {
        return 1;
    }
    if (strncmp(packagename, "com.jusweet", strlen("com.jusweet")) == 0) {
        return 1;
    }
    return 0;
}

static void get_pkg_signature_md5(JNIEnv *env, jclass this, jobject context, jstring packageName,
                                  unsigned char digest[16]) {
    if (context == NULL || packageName == NULL) {
        return;
    }

    /* get packageManager */
    jclass android_content_Context = (*env)->GetObjectClass(env, context);
    jmethodID midGetPackageManager = (*env)->GetMethodID(env, android_content_Context,
                                                         "getPackageManager",
                                                         "()Landroid/content/pm/PackageManager;");
    jobject packageManager = (*env)->CallObjectMethod(env, context, midGetPackageManager);
    if (packageManager == NULL) {
        return;
    }

    /* get packageInfo */
    jclass android_content_pm_PackageManager = (*env)->FindClass(env,
                                                                 "android/content/pm/PackageManager");
    jmethodID midGetPackageInfo = (*env)->GetMethodID(env, android_content_pm_PackageManager,
                                                      "getPackageInfo",
                                                      "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jfieldID fidSignatureFlag = (*env)->GetStaticFieldID(env, android_content_pm_PackageManager,
                                                         "GET_SIGNATURES", "I");
    jint fignatureFlag = (*env)->GetStaticIntField(env, android_content_pm_PackageManager,
                                                   fidSignatureFlag);
    jobject packageInfo = (jobject) (*env)->CallObjectMethod(env, packageManager, midGetPackageInfo,
                                                             packageName, fignatureFlag);
    if (packageInfo == NULL) {
        return;
    }

    /* get signature */
    jclass android_content_pm_PackageInfo = (*env)->FindClass(env,
                                                              "android/content/pm/PackageInfo");
    jfieldID fidSignatures = (*env)->GetFieldID(env, android_content_pm_PackageInfo, "signatures",
                                                "[Landroid/content/pm/Signature;");
    jobjectArray signatures = (*env)->GetObjectField(env, packageInfo, fidSignatures);
    jobject signature = (*env)->GetObjectArrayElement(env, signatures, 0);

    /* get signature string */
    jclass android_content_pm_Signature = (*env)->FindClass(env, "android/content/pm/Signature");
    jmethodID midToCharString = (*env)->GetMethodID(env, android_content_pm_Signature,
                                                    "toCharsString", "()Ljava/lang/String;");
    jstring signatureStr = (jstring) (*env)->CallObjectMethod(env, signature, midToCharString);

    /* get signature md5 */
    const char *p_sig = (*env)->GetStringUTFChars(env, signatureStr, NULL);
    md5_vector(p_sig, digest);

    (*env)->ReleaseStringUTFChars(env, signatureStr, p_sig);
}

JNIEXPORT jbyteArray JNICALL Java_com_suapp_dcdownloader_utils_CipherUtils_getAESKeyNative
        (JNIEnv *env, jclass this, jobject context) {
    /* check PackageName */
    jclass android_content_Context = (*env)->GetObjectClass(env, context);
    jmethodID midGetPackageName = (*env)->GetMethodID(env, android_content_Context,
                                                      "getPackageName", "()Ljava/lang/String;");
    jstring jpackageName = (jstring) (*env)->CallObjectMethod(env, context, midGetPackageName);
    const char *packagename = (*env)->GetStringUTFChars(env, jpackageName, NULL);

    byte *trueKeyStrore[4] = {TRUE_AES_KEY, TRUE_AES_KEY1, TRUE_AES_KEY2, TRUE_AES_KEY3};
    int i = 0;
    jbyte *keybyte;

    if (!isOfficialPackageName(packagename) == 1) {
        keybyte = (jbyte *) FAKE_AES_KEY;
    } else {
        /* check signature */
        if (cur_signature_md5 == NULL) {
            cur_signature_md5 = (unsigned char *) malloc(sizeof(char) * 16);
            get_pkg_signature_md5(env, this, context, jpackageName, cur_signature_md5);
        }

        /*for (int i = 0; i < 16; ++i) {
            char c = cur_signature_md5[i];
            __android_log_print(ANDROID_LOG_INFO, "JNIMsg", "getAESKeyNative= %02x\n", c);
        }*/

        if (isOfficialSignature(cur_signature_md5) == 1) {
            keybyte = (jbyte *) malloc(sizeof(jbyte) * 16);
            for (i = 0; i < 16; i++) {
                keybyte[i] = (jbyte) trueKeyStrore[i % 4][i];
            }
        } else {
            keybyte = (jbyte *) FAKE_AES_KEY;
        }
    }

    jbyteArray jarray = (*env)->NewByteArray(env, 16);
    (*env)->SetByteArrayRegion(env, jarray, 0, 16, keybyte);

    if (keybyte != (jbyte *) FAKE_AES_KEY) {
        free(keybyte);
    }
    (*env)->ReleaseStringUTFChars(env, jpackageName, packagename);

    return jarray;
}