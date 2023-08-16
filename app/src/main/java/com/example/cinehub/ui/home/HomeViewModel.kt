package com.example.cinehub.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.cinehub.MoviePagingSource
import com.example.cinehub.R
import com.example.cinehub.databinding.ItemHomeRecyclerViewBinding
import com.example.cinehub.databinding.ItemHomeRecyclerViewGridBinding
import com.example.cinehub.local_data.AppDatabase
import com.example.cinehub.local_data.LikeStatus
import com.example.cinehub.service.ApiService
import com.example.cinehub.ui.detail.DetailViewModel
import com.example.cinehub.utils.Constants
import com.example.tmdbmovieapp.model.MovieItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val movieList = Pager(PagingConfig(pageSize = 20)) {
        MoviePagingSource(apiService,"")
    }.flow.cachedIn(viewModelScope)


    fun searchMovies(query: String) = Pager(PagingConfig(pageSize = 20)) {
        MoviePagingSource(apiService, query.lowercase())
    }.flow.cachedIn(viewModelScope)

    fun getLikeStatusLiveData(movieId: String): LiveData<LikeStatus?> {
        return appDatabase.likeStatusDao().getLikeStatusLiveData(movieId)
    }


    fun updateFavoriteButtonStatus(isFavorite: Boolean, holder: MovieAdapter.ViewHolder) {
        val heartIcon = if (isFavorite) R.drawable.ic_liked else R.drawable.ic_nonlike
        when (holder.binding) {
            is ItemHomeRecyclerViewBinding -> {
                val specificBinding = holder.binding as ItemHomeRecyclerViewBinding
                specificBinding.homeFavoriteButton.setImageResource(heartIcon)
            }
            is ItemHomeRecyclerViewGridBinding -> {
                val specificBinding = holder.binding as ItemHomeRecyclerViewGridBinding
                specificBinding.homeFavoriteButton.setImageResource(heartIcon)
            }
        }
    }
//    fun getFavoritedMovieIdsLiveData(): LiveData<List<String>> {
//        return appDatabase.likeStatusDao().getAllFavoritedMovieIds()
//    }




}
