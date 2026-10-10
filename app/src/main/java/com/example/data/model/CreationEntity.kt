package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creations")
data class CreationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String, // "photo", "festival", "two_frame", "meme"
    val filePath: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isDraft: Boolean = false,
    val draftJson: String = "" // serialized state for restoring draft
)
