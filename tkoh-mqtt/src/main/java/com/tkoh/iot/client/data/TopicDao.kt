package com.tkoh.iot.client.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Insert(onConflict = REPLACE)
    fun save(topic: Topic)

    @Query("delete from topic where name = :name")
    fun remove(name: String)

    @Query("select * from topic where name = :name")
    fun get(name: String): Flow<Topic>

    @Query("select * from topic")
    fun all(): Flow<List<Topic>>
}