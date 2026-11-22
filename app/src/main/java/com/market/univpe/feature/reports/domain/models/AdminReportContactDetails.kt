package com.market.univpe.feature.reports.domain.models

import com.market.univpe.feature.orders.domain.models.Order
import com.market.univpe.feature.user.domain.model.User

data class AdminReportContactDetails(
    val buyer: User? = null,
    val seller: User? = null,
    val order: Order? = null
)
