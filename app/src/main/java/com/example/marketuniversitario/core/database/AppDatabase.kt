package com.example.marketuniversitario.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.marketuniversitario.feature.business.data.datasources.BusinessDao
import com.example.marketuniversitario.feature.business.data.datasources.ProductDao
import com.example.marketuniversitario.feature.business.data.models.BusinessRoomEntity
import com.example.marketuniversitario.feature.business.data.models.ProductRoomEntity
import com.example.marketuniversitario.feature.user.data.datasources.UserDao
import com.example.marketuniversitario.feature.user.data.models.UserRoomEntity

@Database(
    entities = [
        ProductRoomEntity::class,
        BusinessRoomEntity::class,
        UserRoomEntity::class
    ], 
    version = 3, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun businessDao(): BusinessDao
    abstract fun userDao(): UserDao
}
