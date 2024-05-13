package com.example.image_capturer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.image_capturer.adapter.ImageAdapter
import com.example.image_capturer.model.ImageData
import com.example.image_capturer.viewModel.SharedPhotoViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private var imageList = mutableListOf<ImageData>()
    private lateinit var adapter: ImageAdapter
   // private val REQUEST_CODE_CAPTURE_PHOTO = 101
    private lateinit var sharedViewModel: SharedPhotoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val materialToolbar = findViewById<MaterialToolbar>(R.id.mainToolbar)

        setSupportActionBar(materialToolbar)

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

                sharedViewModel = ViewModelProvider(this).get(SharedPhotoViewModel::class.java)





                recyclerView.layoutManager = LinearLayoutManager(this)

                sharedViewModel.capturedPhotoBitmap.observe(this, Observer<Bitmap> { bitmap ->

                    imageList.add(ImageData("IMG ${LocalDateTime.now()}", bitmap))
                }).also {
                    Toast.makeText(applicationContext, "Image added to image List", Toast.LENGTH_LONG).show()
                }

                adapter = ImageAdapter(this@MainActivity, imageList)
                recyclerView.adapter = adapter

                adapter.notifyDataSetChanged()







        addFabBtn.setOnClickListener{
            val intent = Intent(applicationContext, CameraActivity::class.java)
            startActivity(intent)
            //startActivityForResult(intent, REQUEST_CODE_CAPTURE_PHOTO)
        }
    }



    fun getPermission(){
        val permissionList = mutableListOf<String>()

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA)
        }


        if(permissionList.size > 0){
            requestPermissions(permissionList.toTypedArray(), 101)
        }
    }


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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//
//        Log.d("MAINACTIVITY", "resultCode $resultCode")
//
//        Log.d("MAINACTIVITY", "Activity.RESULT_OK ${Activity.RESULT_OK}")
//
//        Log.d("MAINACTIVITY", "requestCode $requestCode")
//
//        Log.d("MAINACTIVITY", "REQUEST_CODE_CAPTURE_PHOTO ${REQUEST_CODE_CAPTURE_PHOTO}")
//
//        Log.d("MAINACTIVITY", "requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK ${requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK}")
//
//        Log.d("MAINACTIVITY", "Activity.RESULT_OK ${resultCode == Activity.RESULT_OK}")
//
//        if (requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK) {
//            val capturedBitmap = data?.getParcelableExtra<Bitmap>("captured_photo_bitmap")
//            if (capturedBitmap != null) {
//                // Add the captured image Bitmap to the list
//                val imageName = "Image ${imageList.size + 1}" // Generate a name for the image
//                imageList.add(ImageData(imageName, capturedBitmap))
//                // Notify the adapter of the new item
//                adapter.notifyItemInserted(imageList.size - 1)
//            }
//        }
//    }

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

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.capturedPhotoBitmap.removeObservers(this)
    }
}