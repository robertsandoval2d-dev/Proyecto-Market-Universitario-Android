package com.market.univpe.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.market.univpe.feature.business.data.datasources.BusinessDao
import com.market.univpe.feature.business.data.datasources.ProductDao
import com.market.univpe.feature.business.data.models.BusinessRoomEntity
import com.market.univpe.feature.business.data.models.ProductRoomEntity
import com.market.univpe.feature.orders.data.datasources.MessageDao
import com.market.univpe.feature.orders.data.datasources.OrderDao
import com.market.univpe.feature.orders.data.models.MessageRoomEntity
import com.market.univpe.feature.orders.data.models.OrderRoomEntity
import com.market.univpe.feature.reports.data.datasources.ReportDao
import com.market.univpe.feature.reports.data.models.ReportRoomEntity
import com.market.univpe.feature.reviews.data.datasources.ReviewDao
import com.market.univpe.feature.reviews.data.models.ReviewRoomEntity
import com.market.univpe.feature.user.data.datasources.UserDao
import com.market.univpe.feature.user.data.models.UserRoomEntity

@Database(
    entities = [
        ProductRoomEntity::class,
        BusinessRoomEntity::class,
        UserRoomEntity::class,
        OrderRoomEntity::class,
        ReviewRoomEntity::class,
        MessageRoomEntity::class,
        ReportRoomEntity::class
    ], 
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun businessDao(): BusinessDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun messageDao(): MessageDao
    abstract fun reportDao(): ReportDao
}
