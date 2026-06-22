package com.airo.app.domain.model

// suggestedLocation/reason are already populated when the model skipped clarification.
data class DetectedObject(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val isWaste: Boolean,
    val needsClarification: Boolean,
    val clarificationQuestion: String? = null,
    val clarificationOptions: List<String> = emptyList(),
    val suggestedLocation: String? = null,
    val reason: String? = null,
    val isResolving: Boolean = false,
)
