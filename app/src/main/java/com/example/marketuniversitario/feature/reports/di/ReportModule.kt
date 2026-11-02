package com.example.marketuniversitario.feature.reports.di

import com.example.marketuniversitario.feature.reports.data.repositories.ReportRepositoryImpl
import com.example.marketuniversitario.feature.reports.domain.repositories.ReportRepository
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
