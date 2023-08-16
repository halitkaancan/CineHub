package com.example.cinehub.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.cinehub.databinding.ItemHomeRecyclerViewBinding
import com.example.cinehub.databinding.ItemHomeRecyclerViewGridBinding
import com.example.cinehub.utils.loadCircleImage
import com.example.cinehub.utils.loadImage
import com.example.tmdbmovieapp.model.MovieItem

interface MovieClickListener {
    fun onMovieClicked(movieId: Int?)
}

class MovieAdapter(
    private val movieClickListener: MovieClickListener,
    private val viewModel: HomeViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
) : PagingDataAdapter<MovieItem, MovieAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var isGridViewMode = false // Başlangıçta grid view modu kapalı
    private val sharedPreferences = context.getSharedPreferences("cinehub_prefs", Context.MODE_PRIVATE)

    init {
        loadGridViewMode()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem == newItem
            }
        }
        private const val VIEW_TYPE_LIST = 1
        private const val VIEW_TYPE_GRID = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridViewMode) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }


    class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = if (viewType == VIEW_TYPE_GRID) {
            ItemHomeRecyclerViewGridBinding.inflate(layoutInflater, parent, false)
        } else {
            ItemHomeRecyclerViewBinding.inflate(layoutInflater, parent, false)
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItem(position)

        when (holder.binding) {
            is ItemHomeRecyclerViewGridBinding -> {
                val specificBinding = holder.binding as ItemHomeRecyclerViewGridBinding
                specificBinding.textViewMovieTitle.textSize = 16f
                specificBinding.imageViewMovie.visibility = VISIBLE
                specificBinding.textViewMovieTitle.visibility = VISIBLE
                specificBinding.textViewMovieOverview.visibility = GONE
                specificBinding.textViewMovieVote.visibility = GONE
                specificBinding.imageView2.visibility = GONE
                holder.binding.apply {
                    textViewMovieTitle.text = movie?.title
                    textViewMovieOverview.text = movie?.overview
                    textViewMovieVote.text = movie?.voteAverage.toString()
                    imageViewMovie.loadImage(movie?.posterPath)
                    root.setOnClickListener {
                        movieClickListener.onMovieClicked(movie?.id)
                    }
                }
            }

            is ItemHomeRecyclerViewBinding -> {
                val specificBinding = holder.binding as ItemHomeRecyclerViewBinding
                specificBinding.imageViewMovie.visibility = VISIBLE
                specificBinding.textViewMovieTitle.visibility = VISIBLE
                specificBinding.textViewMovieOverview.visibility = VISIBLE
                specificBinding.textViewMovieVote.visibility = VISIBLE
                specificBinding.imageView2.visibility = VISIBLE
                holder.binding.apply {
                    textViewMovieTitle.text = movie?.title
                    textViewMovieOverview.text = movie?.overview
                    textViewMovieVote.text = movie?.voteAverage.toString()
                    imageViewMovie.loadCircleImage(movie?.posterPath)
                    root.setOnClickListener {
                        movieClickListener.onMovieClicked(movie?.id)
                    }
                }
            }
        }
        movie?.id?.let { movieId ->
            val likeStatusLiveData = viewModel.getLikeStatusLiveData(movieId.toString())
            likeStatusLiveData.observe(lifecycleOwner, Observer { likeStatus ->
                val isFavorite = likeStatus?.isLiked ?: false
                viewModel.updateFavoriteButtonStatus(isFavorite, holder)
            })
        }
    }

    //SharedPreferences ile daha önce kullanıan ViewType'ı belleğe kaydettik
    private fun saveGridViewMode(isGrid: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isGridViewMode", isGrid)
        editor.apply()
    }

    private fun loadGridViewMode() {
        isGridViewMode = sharedPreferences.getBoolean("isGridViewMode", false)
    }

    fun setGridMode(isGrid: Boolean) {
        loadGridViewMode() //
        isGridViewMode = isGrid
        saveGridViewMode(isGrid)
        notifyDataSetChanged()
    }

}