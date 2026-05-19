package com.danielchi0716.resume.core.ui.screen.more

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielchi0716.resume.core.ResumeService
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Meta
import com.danielchi0716.resume.core.model.SideProject
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
data class MoreData(
    val sideProjects: List<SideProject>,
    val header: Header,
    val meta: Meta,
)

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val service: ResumeService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MoreData>>(UiState.Loading)
    val uiState: StateFlow<UiState<MoreData>> = _uiState.asStateFlow()

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

    private suspend fun fetch(): MoreData = coroutineScope {
        val side = async { service.getSideProjects() }
        val header = async { service.getHeader() }
        val meta = async { service.getMeta() }
        MoreData(
            sideProjects = side.await(),
            header = header.await(),
            meta = meta.await(),
        )
    }
}
