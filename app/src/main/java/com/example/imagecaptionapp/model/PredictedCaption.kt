package com.example.imagecaptionapp.model

import kotlinx.serialization.Serializable

@Serializable
data class PredictedCaption(
    val answer: String
)