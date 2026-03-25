package com.supdevinci.lagnioledepapi.repository

import com.supdevinci.lagnioledepapi.data.FakeData

class JokeRepository {
    fun randomJoke(): String = FakeData.jokes.random()
}
