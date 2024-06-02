package com.example.imagecaptionapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.imagecaptionapp.General
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File

class ImageCaptionModel(context: Context): ImageCaptionDeviceService {
    private val imageCaptionModel = LiteModuleLoader.load(
        General
        .assetFilePath(context,"image_caption.ptl"))

    override suspend fun getCaption(image: File): String {
        val bitmap = BitmapFactory.decodeFile(image.absolutePath)
        val bitmapResized = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
        val imagePreprocessed: Tensor = TensorImageUtils.bitmapToFloat32Tensor(bitmapResized,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB)

        val k:Long = 3
        val modelResult = imageCaptionModel.forward(IValue.from(imagePreprocessed), IValue.from(k)).toTuple()
        val completes = modelResult[0]
        val scores = modelResult[1]
        val encCaptions = completes.toTensor().dataAsLongArray

        val captions = mutableListOf<List<Long>>()
        val tempCap = mutableListOf<Long>()

        for (i in encCaptions.indices) {
            tempCap.add(encCaptions[i])
            if (encCaptions[i] == imageCaptionModel.runMethod("word_to_token",
                    IValue.from("<end>")).toLong()) {
                captions.add(tempCap.toList())
                tempCap.clear()
            }
        }

        val floatScores = scores.toTensor().dataAsFloatArray
        var maxIndex = 0
        var maxValue = floatScores[0]
        for (i in floatScores.indices) {
            if (floatScores[i] > maxValue) {
                maxValue = floatScores[i]
                maxIndex = i
            }
        }

        var caption = ""
        val res = captions[maxIndex]
        for(i in 1 until res.size-1){
            caption += "${imageCaptionModel.runMethod("token_to_word",
                IValue.from(res[i])).toStr()} "
        }
        return caption
    }
}