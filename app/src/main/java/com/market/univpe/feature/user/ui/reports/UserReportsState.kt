package com.market.univpe.feature.user.ui.reports

import com.market.univpe.feature.reports.domain.models.Report

data class UserReportsState(
    val reports: List<Report> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
