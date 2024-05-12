package com.example.image_capturer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar

class ShowImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

        val intent = intent
        val materialToolbar = findViewById<MaterialToolbar>(R.id.showImageToolbar)

        setSupportActionBar(materialToolbar)

    }
}