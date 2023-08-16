package com.example.cinehub

import android.app.Application
import androidx.room.Room
import com.example.cinehub.local_data.AppDatabase
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@HiltAndroidApp
class CinehubApplication : Application() {

}