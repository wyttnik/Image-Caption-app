package com.example.imagecaptionapp.data

import com.example.imagecaptionapp.General.isConnected
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

interface CaptionRepository {
    suspend fun getCaption(file: File): String
}

class NetworkCaptionRepository(
    private val imageCaptionApiService: ImageCaptionApiService,
    private val imageCaptionOnDevice: ImageCaptionDeviceService
) : CaptionRepository {

    override suspend fun getCaption(file: File): String {
        return if (isConnected) {
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                file.name,
                file.asRequestBody()
            )
            val response = imageCaptionApiService.uploadImage(multipartBody)

            if (response.isSuccessful){
                response.body()!!.answer
            } else {
                imageCaptionOnDevice.getCaption(file)
            }
        }
        else {
            imageCaptionOnDevice.getCaption(file)
        }
    }
}