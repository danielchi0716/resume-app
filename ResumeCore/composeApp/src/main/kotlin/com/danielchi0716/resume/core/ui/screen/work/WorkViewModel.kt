package com.danielchi0716.resume.core.ui.screen.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielchi0716.resume.core.ResumeService
import com.danielchi0716.resume.core.model.WorkExperience
import com.danielchi0716.resume.core.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val service: ResumeService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<WorkExperience>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<WorkExperience>>> = _uiState.asStateFlow()

    init { load() }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = runCatching { service.getWorkExperience() }
                .fold(
                    onSuccess = { UiState.Ready(it) },
                    onFailure = { UiState.Error(it.message ?: it.toString()) },
                )
        }
    }
}
