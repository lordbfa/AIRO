package com.airo.app.ui.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airo.app.data.local.InventoryItemEntity
import com.airo.app.data.repository.WardrobeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SpaceDetailViewModel(
    repository: WardrobeRepository,
    spaceId: String,
) : ViewModel() {

    val items: StateFlow<List<InventoryItemEntity>> = repository.observeItemsForSpace(spaceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
