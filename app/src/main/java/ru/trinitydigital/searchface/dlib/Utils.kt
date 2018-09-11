package ru.trinitydigital.searchface.dlib

import android.content.Context
import android.os.Environment
import ru.trinitydigital.searchface.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Author: Andrey Khitryy
 * Email: andrey.khitryy@gmail.com
 */
fun init(context: Context, onComplete: (FaceRecognition) -> Unit){
    val folder = File(getDLibDirectoryPath())
    if (!folder.exists()) {
        folder.mkdirs()
    }
    if (!File(getFaceShapeModelPath()).exists()) {
        copyFileFromRawToOthers(context, R.raw.shape_predictor_5_face_landmarks, getFaceShapeModelPath())
    }
    if (!File(getFaceDescriptorModelPath()).exists()) {
        copyFileFromRawToOthers(context, R.raw.dlib_face_recognition_resnet_model_v1, getFaceDescriptorModelPath())
    }
    onComplete.invoke(FaceRecognition(getDLibDirectoryPath()))
}

public fun getRandomImageFile(): File {
    val file = "/sdcard/DCIM/Camera/.searchface.jpg"
    return File(file)
}

public fun getImagesDirectoryPath(): String {
    val sdcard = Environment.getExternalStorageDirectory()

    val folder =  sdcard.absolutePath + File.separator + ".searchface" + File.separator + "photos"
    File(folder).let {
        if(!it.exists()) it.mkdirs()
    }
    return folder
}

private fun getDLibDirectoryPath(): String {
    val sdcard = Environment.getExternalStorageDirectory()
    return sdcard.absolutePath + File.separator + ".searchface"
}

private fun getFaceShapeModelPath(): String {
    return getDLibDirectoryPath() + File.separator + "shape_predictor_5_face_landmarks.dat"
}

private fun getFaceDescriptorModelPath(): String {
    return getDLibDirectoryPath() + File.separator + "dlib_face_recognition_resnet_model_v1.dat"
}

private fun copyFileFromRawToOthers(context: Context, id: Int, targetPath: String) {
    val inp = context.resources.openRawResource(id)
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(targetPath)
        val buff = ByteArray(1024)
        while (true){
            val read = inp.read(buff)
            if(read > 0) {
                out.write(buff, 0, read)
            }
            else {
                break
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            inp?.close()
            out?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}