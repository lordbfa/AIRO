package com.airo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val id: String,
    val spaceId: String,
    val name: String,
    val category: String,
    val isWaste: Boolean,
    val suggestedLocation: String?,
    val reason: String?,
    val createdAt: Long,
)
