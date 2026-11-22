package com.market.univpe.core.database

import android.content.Context
import androidx.room.Room
import com.market.univpe.feature.business.data.datasources.BusinessDao
import com.market.univpe.feature.business.data.datasources.ProductDao
import com.market.univpe.feature.orders.data.datasources.MessageDao
import com.market.univpe.feature.orders.data.datasources.OrderDao
import com.market.univpe.feature.reports.data.datasources.ReportDao
import com.market.univpe.feature.reviews.data.datasources.ReviewDao
import com.market.univpe.feature.user.data.datasources.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "market_universitario.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideBusinessDao(appDatabase: AppDatabase): BusinessDao {
        return appDatabase.businessDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao {
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun provideReviewDao(appDatabase: AppDatabase): ReviewDao {
        return appDatabase.reviewDao()
    }

    @Provides
    @Singleton
    fun provideReportDao(appDatabase: AppDatabase): ReportDao {
        return appDatabase.reportDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(appDatabase: AppDatabase): MessageDao {
        return appDatabase.messageDao()
    }
}
