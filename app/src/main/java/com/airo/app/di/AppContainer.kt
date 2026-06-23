package com.airo.app.di

import android.content.Context
import com.airo.app.BuildConfig
import com.airo.app.data.auth.AuthRepository
import com.airo.app.data.local.AiroDatabase
import com.airo.app.data.remote.GeminiVisionClient
import com.airo.app.data.repository.WardrobeRepository
import com.google.firebase.auth.FirebaseAuth

/** Minimal hand-rolled DI container; the app is small enough that a framework would add more ceremony than value. */
class AppContainer(context: Context) {
    private val database = AiroDatabase.getInstance(context)
    private val visionClient = GeminiVisionClient(apiKey = BuildConfig.GEMINI_API_KEY)

    val repository = WardrobeRepository(
        spaceDao = database.spaceDao(),
        itemDao = database.inventoryItemDao(),
        visionClient = visionClient,
    )

    val authRepository = AuthRepository(auth = FirebaseAuth.getInstance())
}
