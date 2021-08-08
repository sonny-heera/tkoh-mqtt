package com.tkoh.iot.client.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(message: SavedMessage)

    @Query("delete from saved_message where name = :name")
    fun remove(name: String)

    @Query("select * from saved_message where name = :name")
    fun get(name: String): Flow<SavedMessage>

    @Query("select * from saved_message")
    fun all(): Flow<List<SavedMessage>>
}