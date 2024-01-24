package com.ry05k2ulv.reversiboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ry05k2ulv.reversiboard.MainActivityUiState.*
import com.ry05k2ulv.reversiboard.data.UserDataRepository
import com.ry05k2ulv.reversiboard.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
	private val repository: UserDataRepository
) : ViewModel() {
	val uiState: StateFlow<MainActivityUiState> = repository.userData
		.map { userData ->
			Success(userData)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.Eagerly,
			initialValue = Loading
		)
}

sealed interface MainActivityUiState {
	object Loading : MainActivityUiState
	data class Success(val userData: UserData) : MainActivityUiState
}