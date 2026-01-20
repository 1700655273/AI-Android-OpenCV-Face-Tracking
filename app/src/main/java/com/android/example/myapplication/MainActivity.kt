package com.android.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.android.example.myapplication.detector.FaceDetector
import com.android.example.myapplication.detector.FaceTrackingManager
import org.opencv.android.CameraActivity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc


class MainActivity : CameraActivity(), CvCameraViewListener2 {
    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    private var faceDetector: FaceDetector? = null
    private var trackingManager: FaceTrackingManager? = null
    private var btnSwitchCamera: Button? = null
    private var isFrontCamera = false

    private val detectionResolution = Size(640.0, 480.0)
    private var scaleRatioX = 1.0
    private var scaleRatioY = 1.0
    private var frameCount = 0
    private val detectEveryNFrames = 1

    init {
        Log.i(TAG, "Instantiated new " + this.javaClass)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)

        if (OpenCVLoader.initLocal()) {
            Log.i(TAG, "OpenCV loaded successfully")
        } else {
            Log.e(TAG, "OpenCV initialization failed!")
            (Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)).show()
            return
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)

        mOpenCvCameraView =
            findViewById<View>(R.id.tutorial1_activity_java_surface_view) as CameraBridgeViewBase

        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE

        mOpenCvCameraView!!.setCvCameraViewListener(this)

        btnSwitchCamera = findViewById<Button>(R.id.btn_switch_camera)
        btnSwitchCamera?.setOnClickListener {
            mOpenCvCameraView?.disableView()
            isFrontCamera = !isFrontCamera
            val newCameraIndex = if (isFrontCamera) CameraBridgeViewBase.CAMERA_ID_FRONT else CameraBridgeViewBase.CAMERA_ID_BACK
            mOpenCvCameraView?.setCameraIndex(newCameraIndex)
            trackingManager?.clear()
            mOpenCvCameraView?.enableView()
            val cameraName = if (isFrontCamera) "前置" else "后置"
            Toast.makeText(this, "切换到${cameraName}摄像头", Toast.LENGTH_SHORT).show()
        }

        faceDetector = FaceDetector(this)
        trackingManager = FaceTrackingManager()
    }

    public override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    public override fun onResume() {
        super.onResume()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.enableView()
    }

    override fun getCameraViewList(): List<CameraBridgeViewBase> {
        return listOf<CameraBridgeViewBase>(mOpenCvCameraView!!)
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
        faceDetector?.release()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        Log.d(TAG, "Camera started: ${width}x${height}")
        scaleRatioX = width.toDouble() / detectionResolution.width
        scaleRatioY = height.toDouble() / detectionResolution.height
    }

    override fun onCameraViewStopped() {
        Log.d(TAG, "Camera stopped")
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        val rgba = inputFrame.rgba()

        if (faceDetector?.isInitialized() == true) {
            val scaledMat = Mat()
            Imgproc.resize(rgba, scaledMat, detectionResolution)

            val detections = if (frameCount % detectEveryNFrames == 0) {
                faceDetector?.detectFaces(scaledMat, scaleFactor = 1.15, minNeighbors = 4, minSize = Size(24.0, 24.0)) ?: emptyList()
            } else {
                emptyList()
            }

            val scaledDetections = detections.map { rect ->
                Rect(
                    (rect.x * scaleRatioX).toInt(),
                    (rect.y * scaleRatioY).toInt(),
                    (rect.width * scaleRatioX).toInt(),
                    (rect.height * scaleRatioY).toInt()
                )
            }

            val trackedFaces = trackingManager?.update(scaledDetections) ?: emptyList()

            for (face in trackedFaces) {
                val rect = face.smoothedRect ?: face.rect
                val color = when {
                    face.confidence <= 2 -> Scalar(0.0, 255.0, 255.0)
                    face.confidence <= 4 -> Scalar(0.0, 200.0, 255.0)
                    face.confidence <= 6 -> Scalar(0.0, 150.0, 255.0)
                    face.confidence <= 8 -> Scalar(0.0, 100.0, 255.0)
                    else -> Scalar(0.0, 0.0, 255.0)
                }

                Imgproc.rectangle(
                    rgba,
                    Point(rect.x.toDouble(), rect.y.toDouble()),
                    Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                    color,
                    3
                )

                val label = "ID:${face.id} C:${face.confidence}"
                val labelSize = Imgproc.getTextSize(label, Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, 2, intArrayOf(0))
                val labelTopLeft = Point(rect.x.toDouble(), (rect.y - 8.0).coerceAtLeast(labelSize.height.toDouble()))

                Imgproc.rectangle(
                    rgba,
                    labelTopLeft,
                    Point(labelTopLeft.x + labelSize.width + 8, labelTopLeft.y - labelSize.height - 8),
                    color,
                    -1
                )
                Imgproc.putText(
                    rgba,
                    label,
                    Point(rect.x.toDouble(), (rect.y - 10.0).coerceAtLeast(labelSize.height.toDouble())),
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    0.7,
                    Scalar(255.0, 255.0, 255.0),
                    2
                )
            }

            scaledMat.release()
            frameCount++
        }

        return rgba
    }

    companion object {
        private const val TAG = "OCVSample::Activity"
    }
}
