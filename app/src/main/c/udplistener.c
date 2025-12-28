#include <jni.h>

JNIEXPORT jint JNICALL
Java_com_jmattaa_udplistener_MainActivity_stringFromJNI(JNIEnv *env,
                                                        jobject thiz,
                                                        jint port)
{
    return port;
}
