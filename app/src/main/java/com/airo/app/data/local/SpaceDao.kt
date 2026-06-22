package com.airo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SpaceDao {
    @Query("SELECT * FROM spaces WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeAllForUser(userId: String): Flow<List<SpaceEntity>>

    @Query("SELECT * FROM spaces WHERE id = :id")
    suspend fun getById(id: String): SpaceEntity?

    @Insert
    suspend fun insert(space: SpaceEntity)

    @Query("UPDATE spaces SET name = :name WHERE id = :id")
    suspend fun rename(id: String, name: String)

    @Query("DELETE FROM spaces WHERE id = :id")
    suspend fun delete(id: String)
}
