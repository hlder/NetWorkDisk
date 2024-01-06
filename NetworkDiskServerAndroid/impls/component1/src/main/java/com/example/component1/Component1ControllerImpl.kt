package com.example.component1

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import com.example.component1api.Component1Controller
import kotlinx.coroutines.flow.Flow

class Component1ControllerImpl : Component1Controller {
    override fun getFragment(): Fragment {
        return Component1Fragment()
    }

    override fun getCallBack(activity: ComponentActivity): Flow<Bitmap> {
        val viewModel = activity.viewModels<Component1ControllerViewModel>().value
        return viewModel.cropBitmap.asFlow()
    }

}