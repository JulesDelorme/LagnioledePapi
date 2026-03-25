package com.supdevinci.lagnioledepapi.testutil

import com.supdevinci.lagnioledepapi.data.local.UserStatsDao
import com.supdevinci.lagnioledepapi.data.local.UserStatsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserStatsDao(
    initialStats: UserStatsEntity? = null
) : UserStatsDao {
    private val stats = MutableStateFlow(initialStats)

    override fun observeById(id: Int): Flow<UserStatsEntity?> = stats

    override fun getById(id: Int): UserStatsEntity? = stats.value

    override fun insert(stats: UserStatsEntity) {
        this.stats.value = stats
    }

    override fun ensureRow(id: Int) {
        if (stats.value == null) {
            stats.value = UserStatsEntity(id = id)
        }
    }

    override fun incrementDetailViews(id: Int) {
        ensureRow(id)
        stats.value = stats.value?.copy(detailViews = stats.value!!.detailViews + 1)
    }

    override fun incrementSurpriseOpens(id: Int) {
        ensureRow(id)
        stats.value = stats.value?.copy(surpriseOpens = stats.value!!.surpriseOpens + 1)
    }

    override fun incrementCopyActions(id: Int) {
        ensureRow(id)
        stats.value = stats.value?.copy(copyActions = stats.value!!.copyActions + 1)
    }

    override fun incrementShareActions(id: Int) {
        ensureRow(id)
        stats.value = stats.value?.copy(shareActions = stats.value!!.shareActions + 1)
    }
}
