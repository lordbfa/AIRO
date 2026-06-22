package com.airo.app.di

import android.content.Context
import com.airo.app.BuildConfig
import com.airo.app.data.local.AiroDatabase
import com.airo.app.data.remote.AnthropicVisionClient
import com.airo.app.data.repository.WardrobeRepository

/** Minimal hand-rolled DI container; the app is small enough that a framework would add more ceremony than value. */
class AppContainer(context: Context) {
    private val database = AiroDatabase.getInstance(context)
    private val visionClient = AnthropicVisionClient(apiKey = BuildConfig.ANTHROPIC_API_KEY)

    val repository = WardrobeRepository(
        spaceDao = database.spaceDao(),
        itemDao = database.inventoryItemDao(),
        visionClient = visionClient,
    )
}
