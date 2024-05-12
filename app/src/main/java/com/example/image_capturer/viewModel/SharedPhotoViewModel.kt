package com.example.image_capturer.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedPhotoViewModel: ViewModel() {
    private val _capturedPhotoBitmap = MutableLiveData<Bitmap>()

    val capturedPhotoBitmap: LiveData<Bitmap>
        get() = _capturedPhotoBitmap

    fun setCapturedPhotoBitmap(bitmap: Bitmap){
        _capturedPhotoBitmap.postValue(bitmap)
    }
}