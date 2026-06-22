package com.airo.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SpaceEntity::class, InventoryItemEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AiroDatabase : RoomDatabase() {
    abstract fun spaceDao(): SpaceDao
    abstract fun inventoryItemDao(): InventoryItemDao

    companion object {
        @Volatile
        private var instance: AiroDatabase? = null

        fun getInstance(context: Context): AiroDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AiroDatabase::class.java,
                    "airo.db",
                ).build().also { instance = it }
            }
    }
}
