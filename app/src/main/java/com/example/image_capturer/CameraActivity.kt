package com.example.image_capturer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        textureView = findViewById(R.id.textureView)

        captureImageView = findViewById(R.id.capture)

        switchCameraImageView = findViewById(R.id.capture_switch)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler((handlerThread).looper)

        imageReader = ImageReader.newInstance(720,1280, ImageFormat.JPEG, 1)

        imageReader.setOnImageAvailableListener(object: ImageReader.OnImageAvailableListener{
            override fun onImageAvailable(p0: ImageReader?) {
                val image = p0?.acquireLatestImage()
                if(image != null){
                    Toast.makeText(this@CameraActivity, "Image captured", Toast.LENGTH_LONG).show()
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                    val newbytes = outputStream.toByteArray()


                    val newbitmap = BitmapFactory.decodeByteArray(newbytes, 0, newbytes.size)
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



                    val intent = Intent(this@CameraActivity, MainActivity::class.java)
                    intent.putExtra("captured_photo_bitmap", newbitmap)

                    Log.d("CAMERA", "${Activity.RESULT_OK}")
                    setResult(Activity.RESULT_OK, intent)

                    Log.d("CAMERA", "${Activity.RESULT_OK}")
                    finish()


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
                currentCameraId = cameraManager.cameraIdList[0]
                openCamera(currentCameraId)
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

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice!!.close()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }


    fun openCamera(cameraId: String){
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

        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback(){



            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0


                captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

                val surface = Surface(textureView.surfaceTexture)
                captureRequestBuilder.addTarget(surface)

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
}