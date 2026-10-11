package com.example.marketuniversitario.feature.user.data.datasources

import com.example.marketuniversitario.feature.user.data.models.UserRoomEntity
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getUserById(userId: String): UserRoomEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun getUnsyncedUsers(): List<UserRoomEntity> {
        return userDao.getUnsyncedUsers()
    }

    suspend fun saveUser(user: UserRoomEntity) {
        userDao.insertUser(user)
    }

    suspend fun markAsSynced(userId: String) {
        userDao.markAsSynced(userId)
    }
    
    suspend fun deleteUser(userId: String) {
        userDao.deleteUser(userId)
    }
}
