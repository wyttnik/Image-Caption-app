package com.example.imagecaptionapp.data

import android.app.Application

class ImageCaptionApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
