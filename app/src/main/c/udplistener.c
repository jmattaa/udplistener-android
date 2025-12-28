#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_jmattaa_udplistener_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    const char *str = "Hello from C";
    return (*env)->NewStringUTF(env, str);
}

