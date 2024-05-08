package com.example.image_capturer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.image_capturer.adapter.ImageAdapter
import com.example.image_capturer.model.ImageData
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val imageList = mutableListOf<ImageData>()
    private lateinit var adapter: ImageAdapter
    val REQUEST_CODE_CAPTURE_PHOTO = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addFabBtn = findViewById<FloatingActionButton>(R.id.addImages)

//        if(!hasRequiredPermission()){
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(CAMERA_PERMISSION),
//                0
//            )
//        }

        val recyclerView: RecyclerView = findViewById(R.id.image_list)


        getPermission()


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(imageList)
        recyclerView.adapter = adapter

        addFabBtn.setOnClickListener{
            val intent = Intent(applicationContext, CameraActivity::class.java)

            startActivityForResult(intent, REQUEST_CODE_CAPTURE_PHOTO)
        }
    }

    fun getPermission(){
        val permissionList = mutableListOf<String>()

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA)
        }


//        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//
//        if (checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//        }

        if(permissionList.size > 0){
            requestPermissions(permissionList.toTypedArray(), 101)
        }
    }

//    fun hasRequiredPermission(): Boolean{
//        return ContextCompat.checkSelfPermission(
//            applicationContext,
//            CAMERA_PERMISSION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    companion object{
//        private val CAMERA_PERMISSION = Manifest.permission.CAMERA;
//        private val READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
//        private val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//        private val MANAGE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.MANAGE_EXTERNAL_STORAGE;
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED){
                getPermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        Log.d("MAINACTIVITY", "resultCode $resultCode")

        Log.d("MAINACTIVITY", "Activity.RESULT_OK ${Activity.RESULT_OK}")

        Log.d("MAINACTIVITY", "requestCode $requestCode")

        Log.d("MAINACTIVITY", "REQUEST_CODE_CAPTURE_PHOTO ${REQUEST_CODE_CAPTURE_PHOTO}")

        Log.d("MAINACTIVITY", "requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK ${requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK}")

        Log.d("MAINACTIVITY", "Activity.RESULT_OK ${resultCode == Activity.RESULT_OK}")

        if (requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK) {
            val capturedBitmap = data?.getParcelableExtra<Bitmap>("captured_photo_bitmap")
            if (capturedBitmap != null) {
                // Add the captured image Bitmap to the list
                val imageName = "Image ${imageList.size + 1}" // Generate a name for the image
                imageList.add(ImageData(imageName, capturedBitmap))
                // Notify the adapter of the new item
                adapter.notifyItemInserted(imageList.size - 1)
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK) {
//            // Handle the result of the photo capture
//            val capturedPhotoPath = data?.getStringExtra("captured_image_path")
//            if (!capturedPhotoPath.isNullOrEmpty()) {
//                // Add the captured photo to your list
//                // For example, if you have a list of photo paths:
//                photoPathsList.add(capturedPhotoPath)
//                // Notify your adapter if you're using one
//                adapter.notifyDataSetChanged()
//            }
//        }
//    }
}