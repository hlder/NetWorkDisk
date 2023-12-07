package com.example.component1api

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow
import org.koin.mp.KoinPlatformTools

interface Component1Controller {
    fun getFragment(): Fragment

    fun getCallBack(activity: ComponentActivity): Flow<Bitmap>

    companion object {
        fun create(): Component1Controller {
            return KoinPlatformTools.defaultContext().get().get()
        }
    }
}