package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CreationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CreationDao {

    @Query("SELECT * FROM creations WHERE isDraft = 0 ORDER BY createdAt DESC")
    fun getAllCreations(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE isDraft = 1 ORDER BY createdAt DESC")
    fun getAllDrafts(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE id = :id")
    suspend fun getCreationById(id: Long): CreationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(creation: CreationEntity): Long

    @Update
    suspend fun updateCreation(creation: CreationEntity)

    @Delete
    suspend fun deleteCreation(creation: CreationEntity)

    @Query("DELETE FROM creations WHERE isDraft = 1")
    suspend fun deleteAllDrafts()
}
