package com.example.marketuniversitario.feature.reports.data.datasources

import com.example.marketuniversitario.feature.reports.data.models.ReportEntity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val reportsCollection = firestore.collection("reports")

    suspend fun createReport(reportEntity: ReportEntity) {
        val documentRef = reportsCollection.document()
        val entityToSave = reportEntity.copy(id = documentRef.id)
        documentRef.set(entityToSave).await()
    }

    suspend fun resolveReport(reportId: String, resolutionStatus: String, adminNotes: String) {
        reportsCollection.document(reportId)
            .update(
                mapOf(
                    "status" to resolutionStatus,
                    "adminResolutionNotes" to adminNotes,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }

    suspend fun resolveReportWithBatch(
        reportId: String,
        orderId: String,
        businessId: String,
        resolutionStatus: String,
        adminNotes: String,
        cancelOrder: Boolean,
        isOrderCompleted: Boolean
    ) {
        val batch = firestore.batch()

        // 1. Actualizar el estado del reporte y las notas internas del admin
        val reportRef = reportsCollection.document(reportId)
        batch.update(
            reportRef,
            mapOf(
                "status" to resolutionStatus,
                "adminResolutionNotes" to adminNotes,
                "updatedAt" to System.currentTimeMillis()
            )
        )

        // 2. Si se resolvió a favor del estudiante (RESOLVED) y se tiene el ID del negocio, aumentar +1 en sus strikes
        if (resolutionStatus == "RESOLVED" && businessId.isNotBlank()) {
            val businessRef = firestore.collection("businesses").document(businessId)
            batch.update(businessRef, "strikes", FieldValue.increment(1))
        }

        // 3. Si la orden aún está activa y debe cancelarse, cambiar su estado a REJECTED
        if (cancelOrder && orderId.isNotBlank() && !isOrderCompleted) {
            val orderRef = firestore.collection("orders").document(orderId)
            batch.update(
                orderRef,
                mapOf(
                    "status" to "REJECTED",
                    "updatedAt" to System.currentTimeMillis()
                )
            )
        }

        // Ejecutar todas las operaciones de forma atómica
        batch.commit().await()
    }

    suspend fun getReportByOrderId(orderId: String): ReportEntity? {
        val snapshot = reportsCollection
            .whereEqualTo("orderId", orderId)
            .limit(1)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(ReportEntity::class.java)
    }

    fun getPendingReports(): Flow<List<ReportEntity>> = callbackFlow {
        val subscription = reportsCollection
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reports = snapshot.toObjects(ReportEntity::class.java)
                    trySend(reports.sortedByDescending { it.createdAt }).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getReportsByStudent(studentId: String): Flow<List<ReportEntity>> = callbackFlow {
        val subscription = reportsCollection
            .whereEqualTo("studentId", studentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reports = snapshot.toObjects(ReportEntity::class.java)
                    trySend(reports.sortedByDescending { it.createdAt }).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getAllReports(): Flow<List<ReportEntity>> = callbackFlow {
        val subscription = reportsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reports = snapshot.toObjects(ReportEntity::class.java)
                    trySend(reports.sortedByDescending { it.createdAt }).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }
}
