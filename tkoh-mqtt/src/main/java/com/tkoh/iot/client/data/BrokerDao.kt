package com.tkoh.iot.client.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BrokerDao {

    @Insert(onConflict = REPLACE)
    fun save(broker: Broker)

    @Query("DELETE FROM broker WHERE name = :name")
    fun remove(name: String)

    @Query("SELECT * FROM broker WHERE name = :name")
    fun load(name: String): Flow<Broker>

    @Query("SELECT * FROM broker")
    fun all(): Flow<List<Broker>>
}