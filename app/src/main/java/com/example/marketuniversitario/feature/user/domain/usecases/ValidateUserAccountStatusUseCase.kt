package com.example.marketuniversitario.feature.user.domain.usecases

import com.example.marketuniversitario.feature.user.domain.model.AccountStatus
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed interface AccountStatusResult {
    object Allowed : AccountStatusResult
    data class Blocked(val message: String) : AccountStatusResult
}

class ValidateUserAccountStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): AccountStatusResult {
        val userResult = userRepository.getUserRemote(userId)
        val user = userResult.getOrNull() ?: return AccountStatusResult.Allowed

        when (user.accountStatus) {
            AccountStatus.BANNED -> {
                val reasonText = if (user.suspensionReason.isNotBlank()) "\nMotivo: ${user.suspensionReason}" else ""
                return AccountStatusResult.Blocked(
                    "Tu cuenta ha sido baneada permanentemente de la plataforma.$reasonText"
                )
            }
            AccountStatus.SUSPENDED -> {
                val until = user.suspendedUntil
                val currentTime = System.currentTimeMillis()

                if (until != null && currentTime > until) {
                    // La suspensión ya expiró: Restaurar la cuenta a activa automáticamente
                    userRepository.restoreUserAccount(userId)
                    return AccountStatusResult.Allowed
                } else {
                    val dateStr = until?.let {
                        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(it))
                    } ?: "tiempo indefinido"
                    val reasonText = if (user.suspensionReason.isNotBlank()) "\nMotivo: ${user.suspensionReason}" else ""

                    return AccountStatusResult.Blocked(
                        "Tu cuenta está suspendida temporalmente hasta el $dateStr.$reasonText"
                    )
                }
            }
            AccountStatus.ACTIVE -> {
                return AccountStatusResult.Allowed
            }
        }
    }
}
