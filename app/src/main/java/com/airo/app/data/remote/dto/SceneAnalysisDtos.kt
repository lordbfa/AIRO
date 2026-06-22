package com.airo.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SceneAnalysisDto(
    val items: List<DetectedItemDto> = emptyList(),
)

@Serializable
data class DetectedItemDto(
    val name: String,
    val category: String,
    val isWaste: Boolean = false,
    val needsClarification: Boolean = false,
    val clarificationQuestion: String? = null,
    val clarificationOptions: List<String> = emptyList(),
    val suggestedLocation: String? = null,
    val reason: String? = null,
)

@Serializable
data class PlacementSuggestionDto(
    val suggestedLocation: String,
    val reason: String,
)
