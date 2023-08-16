package com.example.cinehub.local_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "like_status")
data class LikeStatus(
    @PrimaryKey val itemId: String,
    var isLiked: Boolean
)

