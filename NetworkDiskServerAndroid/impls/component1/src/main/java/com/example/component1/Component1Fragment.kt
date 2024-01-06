package com.example.component1

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.internal.ChannelFlow

class Component1Fragment : Fragment() {

    private val controllerViewModel: Component1ControllerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setContent {
                FragmentContent()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        ChannelFlow<String>()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("aaaa", "bbbbb")
    }

    @Composable
    fun FragmentContent() {
        Column {
            Button(onClick = {
                controllerViewModel.cropBitmap.value =
                    BitmapFactory.decodeResource(resources, R.drawable.test_icon)
                findNavController().popBackStack()
//                viewModel.showState()
            }) {
                Text("通过controller回调")
            }
            Button(onClick = {
                findNavController().apply {
                    previousBackStackEntry?.savedStateHandle?.set("aaaa", "bbbb1")
                    popBackStack()
                }
            }) {
                Text("返回并传值")
            }
        }
    }
}
