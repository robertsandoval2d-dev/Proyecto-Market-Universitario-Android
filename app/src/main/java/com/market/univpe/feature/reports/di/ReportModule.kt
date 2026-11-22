package com.market.univpe.feature.reports.di

import com.market.univpe.feature.reports.data.repositories.ReportRepositoryImpl
import com.market.univpe.feature.reports.domain.repositories.ReportRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportModule {

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl
    ): ReportRepository
}
