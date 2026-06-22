package com.airo.app.domain.model

enum class ItemCategory {
    BAG,
    CLOTHING,
    FOOTWEAR,
    ELECTRONICS,
    DOCUMENT,
    TOILETRY,
    KITCHENWARE,
    WASTE,
    OTHER;

    companion object {
        fun fromLabel(label: String): ItemCategory =
            entries.firstOrNull { it.name.equals(label, ignoreCase = true) } ?: OTHER
    }
}
