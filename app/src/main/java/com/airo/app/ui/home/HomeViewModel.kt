package com.airo.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airo.app.data.local.SpaceEntity
import com.airo.app.data.repository.WardrobeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WardrobeRepository,
    private val userId: String,
) : ViewModel() {

    val spaces: StateFlow<List<SpaceEntity>> = repository.observeSpaces(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addSpace(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.addSpace(trimmed, userId) }
    }

    fun renameSpace(spaceId: String, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.renameSpace(spaceId, trimmed) }
    }

    fun deleteSpace(spaceId: String) {
        viewModelScope.launch { repository.deleteSpace(spaceId) }
    }
}
