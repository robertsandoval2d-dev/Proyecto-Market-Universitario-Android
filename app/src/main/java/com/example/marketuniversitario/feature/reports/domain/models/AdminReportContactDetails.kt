package com.example.marketuniversitario.feature.reports.domain.models

import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.user.domain.model.User

data class AdminReportContactDetails(
    val buyer: User? = null,
    val seller: User? = null,
    val order: Order? = null
)
