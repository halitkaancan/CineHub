package com.example.cinehub.local_data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConfigDao {
    @Query("SELECT * FROM config")
    fun getConfig(): Config?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConfig(config: Config)
}
