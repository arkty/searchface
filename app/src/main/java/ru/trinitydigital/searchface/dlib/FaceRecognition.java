package ru.trinitydigital.searchface.dlib;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Author: Andrey Khitryy
 * Email: andrey.khitryy@gmail.com
 */
public class FaceRecognition {
    private static final String TAG = "dlib";

    // accessed by native methods
    @SuppressWarnings("unused")
    private long mNativeFaceRecContext;

    static {
        try {
            System.loadLibrary("android_dlib");
            jniNativeClassInit();
            Log.d(TAG, "jniNativeClassInit success");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "library not found");
        }
    }

    public FaceRecognition(String dir) {
        jniInit(dir);
    }

    public synchronized native float[] getFace(Bitmap path);

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        jniDeInit();
    }

    private native static void jniNativeClassInit();

    private synchronized native int jniInit(String sample_dir_path);

    private synchronized native int jniDeInit();


}
