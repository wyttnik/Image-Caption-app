
package com.example.imagecaptionapp.ui.home

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagecaptionapp.data.CaptionRepository
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(private val captionRepository: CaptionRepository) : ViewModel() {

    var captionState: Caption by mutableStateOf(Caption())
        private set

    private var textToSpeech:TextToSpeech? = null

    fun resetAnswer() {
        captionState = captionState.copy(string = "")
    }

    fun textToSpeech(context: Context) {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        textToSpeech = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.speak(
                        captionState.string,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        ""
                    )
                }
            }
        }
    }

    fun uploadImage(file: File) {
        viewModelScope.launch {
            val caption = captionRepository.getCaption(
                file
            )
            captionState = captionState.copy(string = caption)
        }
    }
}

data class Caption(val string: String = "")