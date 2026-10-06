package com.example.marketuniversitario.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.marketuniversitario.feature.business.data.datasources.ProductDao
import com.example.marketuniversitario.feature.business.data.models.ProductRoomEntity

@Database(entities = [ProductRoomEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
