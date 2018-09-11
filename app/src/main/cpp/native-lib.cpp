#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_ru_trinitydigital_searchface_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "SearchFace";
    return env->NewStringUTF(hello.c_str());
}
