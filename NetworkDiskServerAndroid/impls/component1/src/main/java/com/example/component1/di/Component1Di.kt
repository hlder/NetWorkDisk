package com.example.component1.di

import com.example.component1.Component1ControllerImpl
import com.example.component1api.Component1Controller
import org.koin.dsl.module

val component1Di = module {
    factory<Component1Controller> { Component1ControllerImpl() }
}