package com.example.marketuniversitario.feature.reports.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marketuniversitario.feature.reports.data.models.ReportRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getReportsByStudent(studentId: String): Flow<List<ReportRoomEntity>>

    @Query("SELECT * FROM reports WHERE orderId = :orderId LIMIT 1")
    suspend fun getReportByOrderId(orderId: String): ReportRoomEntity?

    @Query("SELECT * FROM reports WHERE isSynced = 0")
    suspend fun getUnsyncedReports(): List<ReportRoomEntity>

    @Query("UPDATE reports SET isSynced = 1 WHERE id = :reportId")
    suspend fun markAsSynced(reportId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<ReportRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportRoomEntity)
}
