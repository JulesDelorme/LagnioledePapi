package com.supdevinci.lagnioledepapi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.supdevinci.lagnioledepapi.model.UserStats

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = STATS_ROW_ID,
    val detailViews: Int = 0,
    val surpriseOpens: Int = 0,
    val copyActions: Int = 0,
    val shareActions: Int = 0
) {
    companion object {
        const val STATS_ROW_ID = 1
    }
}

fun UserStatsEntity.toModel(): UserStats = UserStats(
    detailViews = detailViews,
    surpriseOpens = surpriseOpens,
    copyActions = copyActions,
    shareActions = shareActions
)
