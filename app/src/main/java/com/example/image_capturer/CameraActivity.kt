package com.example.image_capturer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.ViewModelProvider
import com.example.image_capturer.viewModel.SharedPhotoViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
class CameraActivity : AppCompatActivity() {

    lateinit var cameraManager: CameraManager

    lateinit var textureView : TextureView

    lateinit var cameraCaptureSession: CameraCaptureSession

    var cameraDevice: CameraDevice? = null

    lateinit var captureRequest: CaptureRequest

    lateinit var handler: Handler

    lateinit var handlerThread: HandlerThread

    lateinit var captureRequestBuilder: CaptureRequest.Builder

    lateinit var captureImageView: ImageView

    lateinit var switchCameraImageView: ImageView

    private var currentCameraId = ""

    lateinit var imageReader: ImageReader

    lateinit var cameraIdList: Array<String>

    private lateinit var sharedViewModel: SharedPhotoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        textureView = findViewById(R.id.textureView)

        captureImageView = findViewById(R.id.capture)

        switchCameraImageView = findViewById(R.id.capture_switch)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        cameraIdList = cameraManager.cameraIdList

        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler((handlerThread).looper)

        imageReader = ImageReader.newInstance(480,720, ImageFormat.JPEG, 55)

        sharedViewModel = ViewModelProvider(this).get(SharedPhotoViewModel::class.java)

        imageReader.setOnImageAvailableListener(object: ImageReader.OnImageAvailableListener{
            @SuppressLint("Recycle")
            override fun onImageAvailable(p0: ImageReader?) {
                Toast.makeText(applicationContext, "Image Available", Toast.LENGTH_LONG).show()
                val image = p0?.acquireLatestImage()
                if(image != null){
                    Toast.makeText(applicationContext,"Image not Null", Toast.LENGTH_LONG).show()
                    //Toast.makeText(this@CameraActivity, "Image captured", Toast.LENGTH_LONG).show()

//                    val uiHandler = Handler(Looper.getMainLooper())
//                    val runnable = Runnable {
//
//                        @Override
//                        fun run() {

                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.remaining())
                            buffer.get(bytes)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                            val outputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
                            val newBytes = outputStream.toByteArray()


                            val newBitmap = BitmapFactory.decodeByteArray(newBytes, 0, newBytes.size)

                           // val processedBitmap = processImage(newBitmap)
                            sharedViewModel.setCapturedPhotoBitmap(newBitmap)
//                        }
//                    }
//
//                    uiHandler.post(runnable)

//                    textureView.post {
//                        try {
//
//                        val surface = Surface(textureView.surfaceTexture)
//                        val surfaceWidth = textureView.width
//                        val surfaceHeight = textureView.height
//                            if (surfaceWidth > 0 && surfaceHeight > 0) {
//                                val lockRect = Rect(0, 0, surfaceWidth, surfaceHeight)
//                                val canvas = surface.lockCanvas(lockRect)
//                                if(canvas != null){
//                                    canvas.drawBitmap(processedBitmap, 0f, 0f, null)
//                                    surface.unlockCanvasAndPost(canvas)
//                                }else{
//                                    Log.d("CAMERA ERROR", "canvas != null ${canvas != null}")
//                                }
//
//                            }else{
//                                Log.d("CAMERA ERROR", "surfaceWidth > 0 && surfaceHeight > 0 ${surfaceWidth > 0 && surfaceHeight > 0}")
//                            }
//
//                    }catch(e: Exception){
//                        Log.d("CAMERA ERROR", e.toString())
//                    }
//                    }


//                    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG ${LocalDateTime.now()}")
//
//                    try{
//                        val outputStream = FileOutputStream(file)
//
//                        outputStream.write(bytes)
//
//                        outputStream.close()
//                    }catch(e: IOException){
//                        e.printStackTrace()
//                        Log.d("CAMERA SAVE FILE ERROR", e.toString())
//                    }



//                    val intent = Intent(this@CameraActivity, MainActivity::class.java)
//                    intent.putExtra("captured_photo_bitmap", newbitmap)



//                    Log.d("CAMERA", "${Activity.RESULT_OK}")
//                    setResult(Activity.RESULT_OK)
//
//                    Log.d("CAMERA", "${Activity.RESULT_OK}")

                    val intent = Intent(this@CameraActivity, MainActivity::class.java)
                    startActivity(intent)
                   // finish()


                    image.close()

                }else{
                    Toast.makeText(this@CameraActivity, "Image is not captured", Toast.LENGTH_LONG).show()
                }





            }
        }, handler)


        captureImageView.setOnClickListener{
            if (cameraDevice != null){
                captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureRequestBuilder.addTarget(imageReader.surface)
                cameraCaptureSession.capture(captureRequestBuilder.build(), null, null)
            }

        }


        switchCameraImageView.setOnClickListener{
           // onSwitchCamera()
        }

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                Log.d("Camera", "Step1")
               currentCameraId = cameraIdList[0]
                openCamera()
                Log.d("Camera", "last Step")
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {

            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {

            }

        }

    }

//    private fun processImage(bitmap: Bitmap): Bitmap {
//        // Example of image processing: converting to grayscale
//     //   val uiHandler = Handler(Looper.getMainLooper())
//       lateinit var grayscaleBitmap: Bitmap
////        val runnable = Runnable {
////
////            @Override
////            fun run() {
//                val width = bitmap.width
//                val height = bitmap.height
//                grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(grayscaleBitmap)
//                val paint = Paint().apply {
//                    colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
//                }
//                canvas.drawBitmap(bitmap, 0f, 0f, paint)
////            }
////        }
////
////        uiHandler.post(runnable)
//            return grayscaleBitmap
//    }

    override fun onDestroy() {
        super.onDestroy()
        if(cameraDevice != null) {
            cameraDevice!!.close()

            cameraDevice = null
        }
        cameraCaptureSession.close()

        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }


    fun openCamera(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


        if(cameraDevice != null){
            cameraDevice!!.close()
        }

//        var cameraCharacteristics: CameraCharacteristics
//
//        for(cameraId: String in cameraIdList){
//            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
//
//            if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_BACK){
//                currentCameraId = cameraId
//                break
//            }
//        }

        cameraManager.openCamera(currentCameraId, object : CameraDevice.StateCallback(){



            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0


                captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

                val surface = Surface(textureView.surfaceTexture)
                captureRequestBuilder.addTarget(surface)
            //    captureRequestBuilder.addTarget(imageReader.surface)

                cameraDevice!!.createCaptureSession(listOf(surface, imageReader.surface), object:
                    CameraCaptureSession.StateCallback() {
                    override fun onConfigured(p0: CameraCaptureSession) {
                        cameraCaptureSession = p0
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                        Log.d("CAMERA", "onConfigureFailed")
                    }

                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {
                p0.close()
            }

            override fun onError(p0: CameraDevice, p1: Int) {
                p0.close()
            }

        }, handler)
    }

//    fun onSwitchCamera(){
//        val cameraIds = cameraManager.cameraIdList
//        val currentCameraIndex = cameraIds.indexOf(currentCameraId)
//
//        if(cameraIds.size > 1){
//            val nextIndex = (currentCameraIndex + 1) % cameraIds.size
//            currentCameraId = cameraIds[nextIndex]
//
//            openCamera(currentCameraId)
//
//        }else{
//            Toast.makeText(applicationContext, "Front camera is not available", Toast.LENGTH_LONG).show()
//        }
//    }

    fun onSwitchCamera(){
//        var cameraCharacteristics: CameraCharacteristics
//
//        for(cameraId: String in cameraIdList){
//            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
//
//            var LENS_FACING = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
//            if(LENS_FACING == CameraMetadata.LENS_FACING_BACK){
//
//
//            }else if(LENS_FACING == CameraMetadata.LENS_FACING_FRONT)
//        }
    }
}