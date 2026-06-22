package com.airo.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airo.app.data.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val user: StateFlow<FirebaseUser?> = authRepository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authRepository.currentUserSnapshot())

    fun updateDisplayName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { authRepository.updateDisplayName(trimmed) }
    }

    fun signOut() = authRepository.signOut()
}
