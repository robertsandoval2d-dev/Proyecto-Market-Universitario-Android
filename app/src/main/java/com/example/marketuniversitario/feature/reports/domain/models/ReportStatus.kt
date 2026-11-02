package com.example.marketuniversitario.feature.reports.domain.models

enum class ReportStatus(val value: String, val displayName: String) {
    PENDING("PENDING", "Pendiente"),
    RESOLVED("RESOLVED", "Resuelto"),
    REJECTED("REJECTED", "Rechazado");

    companion object {
        fun fromString(value: String): ReportStatus {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}
