package com.danielchi0716.resume.core.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Contact
import com.danielchi0716.resume.core.model.ContactType

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
fun TinyChip(
    text: String,
    background: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    leading: (@Composable () -> Unit)? = null,
) {
    Surface(
        color = background,
        contentColor = contentColor,
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (leading != null) leading()
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun ContactQuickRow(
    contacts: List<Contact>,
    modifier: Modifier = Modifier,
) {
    val uri = LocalUriHandler.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        contacts.forEach { c ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                onClick = { runCatching { uri.openUri(c.url.raw) } },
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                            Icon(
                                imageVector = iconFor(c.type),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                    Text(
                        text = labelFor(c.type, c.value),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

private fun iconFor(type: ContactType): ImageVector = when (type) {
    ContactType.EMAIL -> Icons.Filled.Email
    ContactType.PHONE -> Icons.Filled.Call
    ContactType.GITHUB -> Icons.Filled.Code
    ContactType.LINKEDIN -> Icons.Filled.Work
    else -> Icons.Filled.Link
}

@Composable
private fun labelFor(type: ContactType, fallback: String): String = when (type) {
    ContactType.EMAIL -> stringResource(R.string.label_email)
    ContactType.PHONE -> stringResource(R.string.label_phone)
    ContactType.GITHUB -> stringResource(R.string.label_github)
    ContactType.LINKEDIN -> stringResource(R.string.label_linkedin)
    else -> fallback
}
