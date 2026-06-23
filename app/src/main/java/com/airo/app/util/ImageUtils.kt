package com.airo.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

// Phone photos can be 10+ MB; Gemini tiles images into ~768px chunks for tokenization, so
// downscaling first keeps both the upload size and token cost down.
fun ByteArray.toResizedJpegBase64(maxDimension: Int = 1568, quality: Int = 85): String {
    val original = BitmapFactory.decodeByteArray(this, 0, size)
    val longEdge = maxOf(original.width, original.height)
    val scale = if (longEdge > maxDimension) maxDimension.toFloat() / longEdge else 1f
    val resized = if (scale < 1f) {
        Bitmap.createScaledBitmap(
            original,
            (original.width * scale).toInt().coerceAtLeast(1),
            (original.height * scale).toInt().coerceAtLeast(1),
            true,
        )
    } else {
        original
    }

    val outputStream = ByteArrayOutputStream()
    resized.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}
