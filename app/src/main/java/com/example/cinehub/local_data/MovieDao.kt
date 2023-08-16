package com.example.cinehub.local_data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.tmdbmovieapp.model.MovieItem

@Dao
interface MovieDao {
    @Query("SELECT * FROM like_status WHERE itemId IN (:movieIds)")
    fun getMoviesByIds(movieIds: List<String>): LiveData<List<MovieItem>>
}
