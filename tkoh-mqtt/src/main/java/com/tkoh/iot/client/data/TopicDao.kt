package com.tkoh.iot.client.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Insert(onConflict = REPLACE)
    fun save(topic: Topic)

    @Delete
    fun remove(name: Topic)

    @Query("SELECT * FROM topic WHERE name = :name")
    fun get(name: String): Flow<Topic>

    @Query("SELECT * FROM topic")
    fun all(): Flow<List<Topic>>
}