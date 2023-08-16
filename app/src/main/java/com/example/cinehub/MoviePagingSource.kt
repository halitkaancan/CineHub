package com.example.cinehub

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cinehub.service.ApiService
import com.example.cinehub.utils.Constants
import com.example.tmdbmovieapp.model.MovieItem

class MoviePagingSource(
    private val apiService: ApiService,
    private val query: String
) : PagingSource<Int, MovieItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieItem> {
        return try {
            val nextPage = params.key ?: 1
            val response = if (query.isEmpty()) {
                //buraya girmiyor
                apiService.getMovieList(page = nextPage, token = "${Constants.BEARER_TOKEN}")
            } else {

                apiService.searchMovies("movie/" + query)
            }
            val movies = response.body()?.movieItems?.filterNotNull()

            LoadResult.Page(
                data = movies ?: listOf(),
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (movies.isNullOrEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieItem>): Int? {
        return state.anchorPosition
    }
}
