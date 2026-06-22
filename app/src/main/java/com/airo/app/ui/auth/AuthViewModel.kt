package com.airo.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airo.app.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signUpWithEmail(email: String, password: String, displayName: String) {
        runAuthAction { authRepository.signUpWithEmail(email.trim(), password, displayName.trim()) }
    }

    fun signInWithEmail(email: String, password: String) {
        runAuthAction { authRepository.signInWithEmail(email.trim(), password) }
    }

    fun signInWithGoogleIdToken(idToken: String) {
        runAuthAction { authRepository.signInWithGoogleIdToken(idToken) }
    }

    fun onGoogleSignInFailed(message: String) {
        _uiState.value = AuthUiState.Error(message)
    }

    private fun runAuthAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                action()
                _uiState.value = AuthUiState.Idle
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
