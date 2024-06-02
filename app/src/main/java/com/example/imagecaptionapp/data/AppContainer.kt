package com.example.imagecaptionapp.data

import android.content.Context
import com.example.imagecaptionapp.model.PredictedCaption
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.util.concurrent.TimeUnit


interface AppContainer {
    val captionRepository: CaptionRepository
}

interface ImageCaptionApiService {
    @Multipart
    @POST("imageUpload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<PredictedCaption>
}

interface ImageCaptionDeviceService {
    suspend fun getCaption(image: File): String
}

class DefaultAppContainer(context: Context): AppContainer {
    private val baseUrl = "https://expert-smoothly-insect.ngrok-free.app/"

    private var client = OkHttpClient().newBuilder()
        .connectTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(
            Json.asConverterFactory("application/json".toMediaType())
        )
        .baseUrl(baseUrl)
        .client(client)
        .build()

    private val retrofitService: ImageCaptionApiService by lazy {
        retrofit.create(ImageCaptionApiService::class.java)
    }

    private val deviceService: ImageCaptionDeviceService by lazy {
        ImageCaptionModel(context)
    }

    override val captionRepository: CaptionRepository by lazy {
        NetworkCaptionRepository(retrofitService, deviceService)
    }
}