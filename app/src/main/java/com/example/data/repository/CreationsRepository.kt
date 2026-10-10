package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.local.AppDatabase
import com.example.data.model.CreationEntity
import com.example.util.BitmapUtils
import kotlinx.coroutines.flow.Flow
import java.io.File

class CreationsRepository(private val context: Context) {
    private val dao = AppDatabase.getDatabase(context).creationDao()

    val allCreations: Flow<List<CreationEntity>> = dao.getAllCreations()
    val allDrafts: Flow<List<CreationEntity>> = dao.getAllDrafts()

    suspend fun saveCreation(
        bitmap: Bitmap,
        title: String,
        type: String,
        isDraft: Boolean = false,
        draftJson: String = ""
    ): CreationEntity {
        val filename = "${if (isDraft) "draft" else "art"}_${System.currentTimeMillis()}.png"
        val file = BitmapUtils.saveBitmapLocally(context, bitmap, filename)

        val entity = CreationEntity(
            title = title,
            type = type,
            filePath = file.absolutePath,
            createdAt = System.currentTimeMillis(),
            isDraft = isDraft,
            draftJson = draftJson
        )
        val id = dao.insertCreation(entity)
        return entity.copy(id = id)
    }

    suspend fun deleteCreation(creation: CreationEntity) {
        try {
            val file = File(creation.filePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dao.deleteCreation(creation)
    }

    suspend fun deleteAllDrafts() {
        dao.deleteAllDrafts()
    }
}
