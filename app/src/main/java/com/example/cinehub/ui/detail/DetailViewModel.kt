package com.example.cinehub.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinehub.di.NetworkModule
import com.example.cinehub.local_data.AppDatabase
import com.example.cinehub.local_data.LikeStatus
import com.example.cinehub.utils.Constants
import com.example.tmdbmovieapp.model.MovieDetailResponse
import com.example.tmdbmovieapp.model.MovieItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    application: Application,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val movieResponse: MutableLiveData<MovieDetailResponse> = MutableLiveData()
    val isLoading = MutableLiveData(false)
    val errorMessage: MutableLiveData<String?> = MutableLiveData()
    private val ioDispatcher = Dispatchers.IO

    fun getMovieDetail(movieId: Int) {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = NetworkModule.getClient()
                    .getMovieDetail(movieId = movieId.toString(), token = Constants.BEARER_TOKEN)

                if (response.isSuccessful) {
                    movieResponse.postValue(response.body())
                } else {
                    if (response.message().isNullOrEmpty()) {
                        errorMessage.value = "An unknown error occurred"
                    } else {
                        errorMessage.value = response.message()
                    }
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getLikeStatusLiveData(movieId: String): LiveData<LikeStatus?> {
        return appDatabase.likeStatusDao().getLikeStatusLiveData(movieId)
    }

    private suspend fun getLikeStatus(movieId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val likeStatus = appDatabase.likeStatusDao().getLikeStatusDirectly(movieId)
                Log.d("DBCheck", "Fetched LikeStatus: $likeStatus")
                likeStatus?.isLiked ?: false
            }
        } catch (e: Exception) {
            Log.e("LikeStatus", "Error fetching like status: ${e.message}")
            false
        }
    }

    fun toggleLikeStatus(movieId: String) {

        viewModelScope.launch {
            Log.d("LikeStatus", "toggleLikeStatus fonksiyonuna girildi. movieId: $movieId")

            val isLiked = getLikeStatus(movieId)
            Log.d("LikeStatus", " like status ID: $movieId: $isLiked")
            if (isLiked) {
                unlikeItem(movieId)
            } else {
                insertLikeStatus(movieId, !isLiked)
            }
        }
    }

    private suspend fun insertLikeStatus(movieId: String, isLiked: Boolean) {
        val likeStatus = LikeStatus(movieId, isLiked)
        withContext(ioDispatcher) {
            appDatabase.likeStatusDao().insertLikeStatus(likeStatus)
        }
        Log.d("LikeStatus", "insertLikeStatus fonksiyonu kullanıldı like statusu ekle movie ID $movieId: $isLiked")
        Log.d("DBOperation", "Inserted: $likeStatus")
    }

    private suspend fun unlikeItem(movieId: String) {
        withContext(ioDispatcher) {
            appDatabase.likeStatusDao().unlikeItem(movieId)
        }
        Log.d("LikeStatus", "unlikeItem kullanıldı, like statusu güncelle ID $movieId: false")
        Log.d("DBOperation", "Unliked: $movieId")
    }


}
