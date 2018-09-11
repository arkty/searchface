package ru.trinitydigital.searchface

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import ru.trinitydigital.searchface.dlib.FaceRecognition
import ru.trinitydigital.searchface.dlib.getRandomImageFile
import ru.trinitydigital.searchface.dlib.init
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : Activity() {
    lateinit var recognizer: FaceRecognition
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.sample_text).setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, 1)
        }
        // Example of a call to a native method
        initThis()
    }

    private fun initThis() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            init(this) { f ->
                recognizer = f
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 17)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initThis()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            val selectedImage = data?.data
            Log.v("MainActivity", "onActivityResult")
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)

                launch {
                    val face = async(CommonPool) {
                        val scaledBitmap = scaleDown(bitmap, 500.toFloat(), true)
                        val file = getRandomImageFile()
                        try {
                            val out = FileOutputStream(file)
                            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        bitmap.recycle()
                        Log.v("MainActivity", "filePath = ${file.absoluteFile}")
                        recognizer.getFace(scaledBitmap)
                    }.await()

                    Log.v("FACERECON", "faces = ${face.size}")
                    face.forEach {
                        Log.v("FACERECON", "f = $it")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    fun scaleDown(realImage: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
        val ratio = Math.min(
                maxImageSize / realImage.width,
                maxImageSize / realImage.height)
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)

        return Bitmap.createScaledBitmap(realImage, width,
                height, filter)
    }

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
