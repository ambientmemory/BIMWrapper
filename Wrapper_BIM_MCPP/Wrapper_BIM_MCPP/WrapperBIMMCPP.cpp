#include <jni.h>
#include "WrapperBIMJava.h"			//Defines the JNI interfaces

// The Managed C++ header containing the call to the C#
#include "WrapperBIMMCPP.h"

// This is the JNI call to the Managed C++ Class

JNIEXPORT jint JNICALL Java_WrapperBIMJava_callWrapperRead(JNIEnv *jn, jobject jobj, jstring mystring) {
	// Instantiate the MC++ class.
	WrapperClassC* t = new WrapperClassC();
	// The actual call is made. 
	const char *inCStr = jn->GetStringUTFChars(mystring, NULL);
	if (NULL == inCStr) return NULL;
	t->callWrapperRead(mystring);
}

JNIEXPORT jstring JNICALL Java_WrapperBIMJava_callWrapperGetHeaderDescription(JNIEnv *jn, jobject jobj) {
	// Instantiate the MC++ class.
	WrapperClassC* t = new WrapperClassC();
	// The actual call is made. 
	t->callWrapperGetHeaderDescription();
}

JNIEXPORT jstring JNICALL Java_WrapperBIMJava_callWrapperGetHeaderFileName(JNIEnv *jn, jobject jobj) {
	// Instantiate the MC++ class.
	WrapperClassC* t = new WrapperClassC();
	// The actual call is made. 
	t->callWrapperGetHeaderFileName();
}

JNIEXPORT jstring JNICALL Java_WrapperBIMJava_callWrapperGetHeaderSchema(JNIEnv *jn, jobject jobj) {
	// Instantiate the MC++ class.
	WrapperClassC* t = new WrapperClassC();
	// The actual call is made. 
	t->callWrapperGetHeaderSchema();
}


JNIEXPORT void JNICALL Java_WrapperBIMJava_callWrapperExit(JNIEnv *jn, jobject jobj) {
	// Instantiate the MC++ class.
	WrapperClassC* t = new WrapperClassC();
	// The actual call is made. 
	t->callWrapperExit();
}