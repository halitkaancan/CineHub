package com.example.cinehub.local_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class Config (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isGridViewMode: Boolean
)
