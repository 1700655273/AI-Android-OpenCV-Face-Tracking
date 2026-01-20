package com.android.example.myapplication.detector

import android.content.Context
import com.android.example.myapplication.R
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream

class FaceDetector(private val context: Context) {

    private var faceCascade: CascadeClassifier? = null

    init {
        loadHaarCascade()
    }

    private fun loadHaarCascade() {
        try {
            val inputStream = context.resources.openRawResource(R.raw.haarcascade_frontalface_alt)
            val cascadeDir = context.cacheDir
            val cascadeFile = File(cascadeDir, "haarcascade_frontalface_alt.xml")
            
            val outputStream = FileOutputStream(cascadeFile)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            outputStream.close()

            faceCascade = CascadeClassifier(cascadeFile.absolutePath)
            
            if (faceCascade?.empty() == true) {
                faceCascade = null
            } else {
                cascadeFile.deleteOnExit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isInitialized(): Boolean = faceCascade != null

    fun detectFaces(frame: Mat, scaleFactor: Double = 1.1, minNeighbors: Int = 3, minSize: Size = Size(30.0, 30.0)): List<Rect> {
        val cascade = faceCascade ?: return emptyList()

        val faces = MatOfRect()
        val gray = Mat()
        
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.equalizeHist(gray, gray)

        cascade.detectMultiScale(
            gray,
            faces,
            scaleFactor,
            minNeighbors,
            0,
            minSize,
            Size()
        )

        gray.release()

        return faces.toList()
    }

    fun release() {
        faceCascade = null
    }
}
