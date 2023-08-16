package com.example.cinehub

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cinehub.local_data.AppDatabase
import com.example.tmdbmovieapp.model.MovieItem

class FavoriteMoviePagingSource(
    private val appDatabase: AppDatabase
) : PagingSource<Int, MovieItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieItem> {
        return try {
            val nextPage = params.key ?: 1
            val favoriteMovies = getFavoriteMoviesFromDatabase(nextPage)
            LoadResult.Page(
                data = favoriteMovies,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (favoriteMovies.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getFavoriteMoviesFromDatabase(page: Int): List<MovieItem> {
        return emptyList()
    }

    override fun getRefreshKey(state: PagingState<Int, MovieItem>): Int? {
        return state.anchorPosition
    }
}

