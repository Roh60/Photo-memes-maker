package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_favorites", Context.MODE_PRIVATE)

    private val _favoriteTemplates = MutableStateFlow<Set<String>>(
        prefs.getStringSet("fav_templates", setOf("diwali_royal", "holi_colors", "2_side_by_side", "classic_impact")) ?: emptySet()
    )
    val favoriteTemplates: StateFlow<Set<String>> = _favoriteTemplates.asStateFlow()

    private val _favoritePresets = MutableStateFlow<Set<String>>(
        prefs.getStringSet("fav_presets", setOf("cinematic", "glow", "vintage")) ?: emptySet()
    )
    val favoritePresets: StateFlow<Set<String>> = _favoritePresets.asStateFlow()

    fun toggleFavoriteTemplate(templateId: String): Boolean {
        val current = _favoriteTemplates.value.toMutableSet()
        val isFav = if (current.contains(templateId)) {
            current.remove(templateId)
            false
        } else {
            current.add(templateId)
            true
        }
        prefs.edit().putStringSet("fav_templates", current).apply()
        _favoriteTemplates.value = current
        return isFav
    }

    fun isFavoriteTemplate(templateId: String): Boolean {
        return _favoriteTemplates.value.contains(templateId)
    }

    fun toggleFavoritePreset(presetId: String): Boolean {
        val current = _favoritePresets.value.toMutableSet()
        val isFav = if (current.contains(presetId)) {
            current.remove(presetId)
            false
        } else {
            current.add(presetId)
            true
        }
        prefs.edit().putStringSet("fav_presets", current).apply()
        _favoritePresets.value = current
        return isFav
    }

    fun isFavoritePreset(presetId: String): Boolean {
        return _favoritePresets.value.contains(presetId)
    }
}
