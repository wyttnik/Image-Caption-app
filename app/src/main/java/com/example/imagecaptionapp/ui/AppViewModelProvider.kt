package com.example.imagecaptionapp.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.imagecaptionapp.data.ImageCaptionApplication
import com.example.imagecaptionapp.ui.chooser.ChooserViewModel
import com.example.imagecaptionapp.ui.home.HomeViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(imageCaptionApplication().container.captionRepository)
        }
        initializer {
            ChooserViewModel(imageCaptionApplication())
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [MediaApplication].
 */
fun CreationExtras.imageCaptionApplication(): ImageCaptionApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ImageCaptionApplication)