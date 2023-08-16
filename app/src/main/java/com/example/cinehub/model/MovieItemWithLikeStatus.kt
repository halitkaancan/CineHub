package com.example.cinehub.model

import com.example.tmdbmovieapp.model.MovieItem

data class MovieItemWithLikeStatus(
    val movieItem: MovieItem,
    var isLiked: Boolean = false
)
