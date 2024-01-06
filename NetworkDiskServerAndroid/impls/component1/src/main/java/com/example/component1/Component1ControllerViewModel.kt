package com.example.component1

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

internal class Component1ControllerViewModel :ViewModel(){
    val cropBitmap = MutableLiveData<Bitmap>()
}