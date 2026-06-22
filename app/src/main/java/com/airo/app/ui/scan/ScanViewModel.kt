package com.airo.app.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airo.app.data.repository.WardrobeRepository
import com.airo.app.domain.model.DetectedObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScanUiState {
    data object Capturing : ScanUiState
    data object Analyzing : ScanUiState
    data class Reviewing(val items: List<DetectedObject>) : ScanUiState
    data object Saving : ScanUiState
    data object Done : ScanUiState
    data class Error(val message: String) : ScanUiState
}

class ScanViewModel(
    private val repository: WardrobeRepository,
    private val spaceId: String,
    private val spaceName: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Capturing)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun onPhotoCaptured(imageBase64: String) {
        _uiState.value = ScanUiState.Analyzing
        viewModelScope.launch {
            try {
                val items = repository.analyzeScene(imageBase64, spaceName)
                _uiState.value = ScanUiState.Reviewing(items)
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Couldn't analyze the room.")
            }
        }
    }

    fun answerClarification(itemId: String, answer: String) {
        val reviewing = _uiState.value as? ScanUiState.Reviewing ?: return
        val target = reviewing.items.firstOrNull { it.id == itemId } ?: return
        markResolving(itemId, true)
        viewModelScope.launch {
            try {
                val resolved = repository.resolvePlacement(target, spaceName, answer)
                replaceItem(resolved.copy(isResolving = false))
            } catch (e: Exception) {
                markResolving(itemId, false)
            }
        }
    }

    fun saveAndFinish() {
        val reviewing = _uiState.value as? ScanUiState.Reviewing ?: return
        _uiState.value = ScanUiState.Saving
        viewModelScope.launch {
            try {
                repository.saveItems(spaceId, reviewing.items)
                _uiState.value = ScanUiState.Done
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Couldn't save items.")
            }
        }
    }

    fun retry() {
        _uiState.value = ScanUiState.Capturing
    }

    private fun markResolving(itemId: String, resolving: Boolean) {
        val reviewing = _uiState.value as? ScanUiState.Reviewing ?: return
        _uiState.value = reviewing.copy(
            items = reviewing.items.map { if (it.id == itemId) it.copy(isResolving = resolving) else it },
        )
    }

    private fun replaceItem(updated: DetectedObject) {
        val reviewing = _uiState.value as? ScanUiState.Reviewing ?: return
        _uiState.value = reviewing.copy(
            items = reviewing.items.map { if (it.id == updated.id) updated else it },
        )
    }
}
