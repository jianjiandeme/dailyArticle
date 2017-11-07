#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <malloc.h>
#include "md5.c"
#include "com_example_dailyarticle_utils_Jstr.h"

char *join3(char *, char *);

#define MD5_KEY  "123456"

JNIEXPORT jstring JNICALL
Java_com_example_dailyarticle_utils_Jstr_getJniString(JNIEnv *env, jobject instance,
                                                             jstring json_) {
    char *szText = (char *) (*env)->GetStringUTFChars(env, json_, 0);
    char *key = MD5_KEY;
    char *str = join3(szText, key);

    MD5_CTX context = {0};
    MD5Init(&context);
    MD5Update(&context, str, strlen(str));
    unsigned char dest[16] = {0};
    MD5Final(dest, &context);
    (*env)->ReleaseStringUTFChars(env, json_, str);
    int i = 0;
    char szMd5[32] = {0};
    for (i = 0; i < 16; i++) {
        sprintf(szMd5, "%s%02x", szMd5, dest[i]);
    }
    return (*env)->NewStringUTF(env, szMd5);
}

char *join3(char *s1, char *s2) {
    char *result = malloc(strlen(s1) + strlen(s2) + 1);//+1 for the zero-terminator
    //in real code you would check for errors in malloc here
    if (result == NULL) exit(1);

    strcpy(result, s1);
    strcat(result, s2);

    return result;
}
