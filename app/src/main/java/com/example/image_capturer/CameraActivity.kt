package com.example.image_capturer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat

class CameraActivity : AppCompatActivity() {

    lateinit var cameraManager: CameraManager

    lateinit var textureView : TextureView

    lateinit var cameraCaptureSession: CameraCaptureSession

    lateinit var cameraDevice: CameraDevice

    lateinit var captureRequest: CaptureRequest

    lateinit var handler: Handler

    lateinit var handlerThread: HandlerThread

    lateinit var cameraCaptureRequest: CaptureRequest.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        textureView = findViewById(R.id.textureView)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler((handlerThread).looper)

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                TODO("Not yet implemented")
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
                TODO("Not yet implemented")
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                TODO("Not yet implemented")
            }

        }

    }

    fun openCamera(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        cameraManager.openCamera(cameraManager.cameraIdList[0], object : CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0
                 cameraCaptureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

                val surface = Surface(textureView.surfaceTexture)
                cameraCaptureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object:
                    CameraCaptureSession.StateCallback() {
                    override fun onConfigured(p0: CameraCaptureSession) {
                        cameraCaptureSession = p0
                        cameraCaptureSession.setRepeatingRequest(cameraCaptureRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                        TODO("Not yet implemented")
                    }

                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {
                TODO("Not yet implemented")
            }

            override fun onError(p0: CameraDevice, p1: Int) {
                TODO("Not yet implemented")
            }

        }, handler)
    }
}