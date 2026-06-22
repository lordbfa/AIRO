package com.airo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {
    @Query("SELECT * FROM inventory_items WHERE spaceId = :spaceId ORDER BY createdAt DESC")
    fun observeForSpace(spaceId: String): Flow<List<InventoryItemEntity>>

    @Insert
    suspend fun insertAll(items: List<InventoryItemEntity>)

    @Query("DELETE FROM inventory_items WHERE spaceId = :spaceId")
    suspend fun deleteForSpace(spaceId: String)
}
