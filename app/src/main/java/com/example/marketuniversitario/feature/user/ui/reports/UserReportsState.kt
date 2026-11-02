package com.example.marketuniversitario.feature.user.ui.reports

import com.example.marketuniversitario.feature.reports.domain.models.Report

data class UserReportsState(
    val reports: List<Report> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
