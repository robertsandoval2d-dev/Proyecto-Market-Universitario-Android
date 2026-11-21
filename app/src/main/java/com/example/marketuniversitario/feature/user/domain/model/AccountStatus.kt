package com.example.marketuniversitario.feature.user.domain.model

enum class AccountStatus(val value: String, val displayName: String) {
    ACTIVE("active", "Activa"),
    SUSPENDED("suspended", "Suspendida"),
    BANNED("banned", "Baneada");

    companion object {
        fun fromString(value: String?): AccountStatus {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: ACTIVE
        }
    }
}
