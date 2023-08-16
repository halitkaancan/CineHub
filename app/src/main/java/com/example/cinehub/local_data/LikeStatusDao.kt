package com.example.cinehub.local_data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LikeStatusDao {
    @Query("SELECT * FROM like_status WHERE itemId = :itemId")
    fun getLikeStatusLiveData(itemId: String): LiveData<LikeStatus?>

    @Query("SELECT * FROM like_status WHERE itemId = :itemId")
    suspend fun getLikeStatusDirectly(itemId: String): LikeStatus?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikeStatus(likeStatus: LikeStatus)
    @Query("UPDATE like_status SET isLiked = 0 WHERE itemId = :itemId")
    suspend fun unlikeItem(itemId: String)
    @Update
    suspend fun updateLikeStatus(likeStatus: LikeStatus)
    @Query("SELECT itemId FROM like_status WHERE isLiked = 1")
    fun getAllFavoritedMovieIds(): LiveData<List<String>>
}

