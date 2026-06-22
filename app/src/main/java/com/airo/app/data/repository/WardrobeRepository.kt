package com.airo.app.data.repository

import com.airo.app.data.local.InventoryItemDao
import com.airo.app.data.local.InventoryItemEntity
import com.airo.app.data.local.SpaceDao
import com.airo.app.data.local.SpaceEntity
import com.airo.app.data.remote.AnthropicVisionClient
import com.airo.app.domain.model.DetectedObject
import com.airo.app.domain.model.ItemCategory
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class WardrobeRepository(
    private val spaceDao: SpaceDao,
    private val itemDao: InventoryItemDao,
    private val visionClient: AnthropicVisionClient,
) {
    fun observeSpaces(userId: String): Flow<List<SpaceEntity>> = spaceDao.observeAllForUser(userId)

    fun observeItemsForSpace(spaceId: String): Flow<List<InventoryItemEntity>> =
        itemDao.observeForSpace(spaceId)

    suspend fun addSpace(name: String, userId: String): SpaceEntity {
        val space = SpaceEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            name = name,
            createdAt = System.currentTimeMillis(),
        )
        spaceDao.insert(space)
        return space
    }

    suspend fun renameSpace(spaceId: String, newName: String) {
        spaceDao.rename(spaceId, newName)
    }

    suspend fun deleteSpace(spaceId: String) {
        itemDao.deleteForSpace(spaceId)
        spaceDao.delete(spaceId)
    }

    suspend fun analyzeScene(imageBase64: String, spaceName: String): List<DetectedObject> {
        val analysis = visionClient.analyzeScene(imageBase64, spaceName)
        return analysis.items.map { dto ->
            DetectedObject(
                id = UUID.randomUUID().toString(),
                name = dto.name,
                category = ItemCategory.fromLabel(dto.category),
                isWaste = dto.isWaste,
                needsClarification = dto.needsClarification,
                clarificationQuestion = dto.clarificationQuestion,
                clarificationOptions = dto.clarificationOptions,
                suggestedLocation = dto.suggestedLocation,
                reason = dto.reason,
            )
        }
    }

    suspend fun resolvePlacement(
        item: DetectedObject,
        spaceName: String,
        answer: String,
    ): DetectedObject {
        val suggestion = visionClient.suggestPlacement(
            itemName = item.name,
            category = item.category.name,
            spaceName = spaceName,
            clarificationAnswer = answer,
        )
        return item.copy(
            suggestedLocation = suggestion.suggestedLocation,
            reason = suggestion.reason,
        )
    }

    suspend fun saveItems(spaceId: String, items: List<DetectedObject>) {
        val entities = items.filter { it.suggestedLocation != null }.map { item ->
            InventoryItemEntity(
                id = item.id,
                spaceId = spaceId,
                name = item.name,
                category = item.category.name,
                isWaste = item.isWaste,
                suggestedLocation = item.suggestedLocation,
                reason = item.reason,
                createdAt = System.currentTimeMillis(),
            )
        }
        if (entities.isNotEmpty()) {
            itemDao.insertAll(entities)
        }
    }
}
