package com.example.cinehub.service

import com.example.tmdbmovieapp.model.MovieDetailResponse
import com.example.tmdbmovieapp.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun getMovieList(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int,
        @Header("Authorization") token: String
    ): Response<MovieResponse>

    @GET("movie/{movieId}")
    suspend fun getMovieDetail(
        @Path("movieId") movieId: String,
        @Header("Authorization") token: String
    ): Response<MovieDetailResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String ="ea1d085b006d5cd86ac00dffc459cce0",
//       @Header("Authorization") token: String
    ): Response<MovieResponse>


}
