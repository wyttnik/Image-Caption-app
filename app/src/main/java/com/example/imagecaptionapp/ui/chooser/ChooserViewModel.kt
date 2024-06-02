
package com.example.imagecaptionapp.ui.chooser

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.example.imagecaptionapp.model.ImageItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel to retrieve all items in the Room database.
 */
class ChooserViewModel(private val app: Application) : AndroidViewModel(app) {
    private val _imagesUiState = MutableStateFlow(ImagesUiState())
    val imagesUiState = _imagesUiState.asStateFlow()

    init {
        getImages()
    }

    fun getImages() {
        val listToInsert = mutableListOf<ImageItem>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        val sort = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        app.contentResolver.query(
            collection, projection, null, null, sort
        )?.use { cursor->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while(cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                listToInsert += ImageItem(id = id, uri = uri)
            }
        }
        _imagesUiState.update { it.copy(imageList = listToInsert) }
    }
}

data class ImagesUiState(val imageList: List<ImageItem> = listOf())