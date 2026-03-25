package com.supdevinci.lagnioledepapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = :id LIMIT 1")
    fun observeById(id: Int = UserStatsEntity.STATS_ROW_ID): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = :id LIMIT 1")
    fun getById(id: Int = UserStatsEntity.STATS_ROW_ID): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stats: UserStatsEntity)

    @Query(
        "INSERT OR IGNORE INTO user_stats (id, detailViews, surpriseOpens, copyActions, shareActions) " +
            "VALUES (:id, 0, 0, 0, 0)"
    )
    fun ensureRow(id: Int = UserStatsEntity.STATS_ROW_ID)

    @Query("UPDATE user_stats SET detailViews = detailViews + 1 WHERE id = :id")
    fun incrementDetailViews(id: Int = UserStatsEntity.STATS_ROW_ID)

    @Query("UPDATE user_stats SET surpriseOpens = surpriseOpens + 1 WHERE id = :id")
    fun incrementSurpriseOpens(id: Int = UserStatsEntity.STATS_ROW_ID)

    @Query("UPDATE user_stats SET copyActions = copyActions + 1 WHERE id = :id")
    fun incrementCopyActions(id: Int = UserStatsEntity.STATS_ROW_ID)

    @Query("UPDATE user_stats SET shareActions = shareActions + 1 WHERE id = :id")
    fun incrementShareActions(id: Int = UserStatsEntity.STATS_ROW_ID)
}
