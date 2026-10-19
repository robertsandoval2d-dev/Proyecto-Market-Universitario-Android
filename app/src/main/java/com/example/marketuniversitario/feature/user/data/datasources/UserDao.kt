package com.example.marketuniversitario.feature.user.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marketuniversitario.feature.user.data.models.UserRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserRoomEntity?
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserStreamById(userId: String): Flow<UserRoomEntity?>

    @Query("SELECT * FROM users WHERE isSynced = 0")
    suspend fun getUnsyncedUsers(): List<UserRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserRoomEntity>)

    @Query("UPDATE users SET isSynced = 1 WHERE id = :userId")
    suspend fun markAsSynced(userId: String)
}
