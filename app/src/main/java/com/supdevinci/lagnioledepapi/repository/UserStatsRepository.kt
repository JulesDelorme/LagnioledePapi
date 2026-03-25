package com.supdevinci.lagnioledepapi.repository

import com.supdevinci.lagnioledepapi.data.local.UserStatsDao
import com.supdevinci.lagnioledepapi.data.local.toModel
import com.supdevinci.lagnioledepapi.model.UserStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserStatsRepository(
    private val dao: UserStatsDao
) {
    fun observeStats(): Flow<UserStats> =
        dao.observeById().map { entity -> entity?.toModel() ?: UserStats() }

    suspend fun recordDetailView() {
        withContext(Dispatchers.IO) {
            dao.ensureRow()
            dao.incrementDetailViews()
        }
    }

    suspend fun recordSurpriseOpen() {
        withContext(Dispatchers.IO) {
            dao.ensureRow()
            dao.incrementSurpriseOpens()
        }
    }

    suspend fun recordShare() {
        withContext(Dispatchers.IO) {
            dao.ensureRow()
            dao.incrementShareActions()
        }
    }

    suspend fun recordCopy() {
        withContext(Dispatchers.IO) {
            dao.ensureRow()
            dao.incrementCopyActions()
        }
    }
}
