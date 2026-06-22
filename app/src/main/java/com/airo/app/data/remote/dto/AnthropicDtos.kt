package com.airo.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnthropicResponse(
    val content: List<AnthropicResponseBlock> = emptyList(),
    val error: AnthropicError? = null,
)

@Serializable
data class AnthropicResponseBlock(
    val type: String,
    val text: String? = null,
)

@Serializable
data class AnthropicError(
    val type: String? = null,
    val message: String? = null,
)
