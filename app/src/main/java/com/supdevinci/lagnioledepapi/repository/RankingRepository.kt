package com.supdevinci.lagnioledepapi.repository

import com.supdevinci.lagnioledepapi.data.FakeData
import com.supdevinci.lagnioledepapi.model.LocalPlayerProfile
import com.supdevinci.lagnioledepapi.model.RankingEntry
import com.supdevinci.lagnioledepapi.model.RankingRegion
import com.supdevinci.lagnioledepapi.model.UserStats

class RankingRepository {
    fun getRankings(region: RankingRegion): List<RankingEntry> =
        FakeData.rankings.filter { it.region == region }

    fun buildLocalProfile(
        userStats: UserStats,
        favoritesCount: Int,
        customCocktailsCount: Int
    ): LocalPlayerProfile {
        val scoreDeBeauf = userStats.detailViews * 3 +
            favoritesCount * 20 +
            customCocktailsCount * 35 +
            userStats.surpriseOpens * 10 +
            userStats.shareActions * 8 +
            userStats.copyActions * 5

        return LocalPlayerProfile(
            scoreDeBeauf = scoreDeBeauf,
            detailViews = userStats.detailViews,
            favorites = favoritesCount,
            customCocktails = customCocktailsCount,
            surpriseOpens = userStats.surpriseOpens,
            copyActions = userStats.copyActions,
            shareActions = userStats.shareActions
        )
    }

    fun getHybridRankings(
        region: RankingRegion,
        userStats: UserStats,
        favoritesCount: Int,
        customCocktailsCount: Int
    ): List<RankingEntry> {
        val rankings = getRankings(region).toMutableList()
        val localEntry = buildLocalProfile(
            userStats = userStats,
            favoritesCount = favoritesCount,
            customCocktailsCount = customCocktailsCount
        ).toRankingEntry(region)
        val insertIndex = rankings.indexOfFirst { entry -> localEntry.score > entry.score }
            .takeIf { it >= 0 }
            ?: rankings.size
        rankings.add(insertIndex, localEntry)
        return rankings
    }
}
