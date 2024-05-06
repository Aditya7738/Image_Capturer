package com.example.image_capturer

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!hasRequiredPermission()){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA_PERMISSION),
                0
            )
        }
    }

    fun hasRequiredPermission(): Boolean{
        return ContextCompat.checkSelfPermission(
            applicationContext,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        private val CAMERA_PERMISSION = Manifest.permission.CAMERA;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(CAMERA_PERMISSION),
                    0
                )
            }
        }
    }
}