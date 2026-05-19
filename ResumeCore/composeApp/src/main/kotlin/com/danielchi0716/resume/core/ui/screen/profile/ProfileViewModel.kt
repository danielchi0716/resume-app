package com.danielchi0716.resume.core.ui.screen.profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielchi0716.resume.core.ResumeService
import com.danielchi0716.resume.core.model.Education
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Language
import com.danielchi0716.resume.core.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class ProfileData(
    val header: Header,
    val about: List<String>,
    val languages: List<Language>,
    val education: List<Education>,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(private val service: ResumeService) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ProfileData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProfileData>> = _uiState.asStateFlow()

    init { load() }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = runCatching { fetch() }
                .fold(
                    onSuccess = { UiState.Ready(it) },
                    onFailure = { UiState.Error(it.message ?: it.toString()) },
                )
        }
    }

    private suspend fun fetch(): ProfileData = coroutineScope {
        val header = async { service.getHeader() }
        val about = async { service.getAbout() }
        val languages = async { service.getLanguages() }
        val education = async { service.getEducation() }
        ProfileData(
            header = header.await(),
            about = about.await(),
            languages = languages.await(),
            education = education.await(),
        )
    }
}
