package com.danielchi0716.resume.core.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Locale

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            Spacer(Modifier.size(16.dp))
            Button(onClick = onRetry) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.error_retry))
            }
        }
    }
}

@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    onRetry: () -> Unit,
    ready: @Composable (T) -> Unit,
) {
    when (state) {
        is UiState.Loading -> LoadingState()
        is UiState.Error -> ErrorState(message = state.message, onRetry = onRetry)
        is UiState.Ready -> ready(state.data)
    }
}

@Composable
fun rememberDataLocale(): Locale {
    val configuration = LocalConfiguration.current
    return androidx.compose.runtime.remember(configuration) {
        if (configuration.locales[0].language.equals("zh", ignoreCase = true)) Locale.TraditionalChinese else Locale.English
    }
}
