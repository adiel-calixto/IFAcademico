package com.adielcalixto.ifacademico.presentation.update

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.data.remote.ReleaseInfo
import androidx.core.net.toUri

@Composable
fun UpdateDialog(
    info: ReleaseInfo,
    onSkipClick: () -> Unit,
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onSkipClick,
        icon = {
            Icon(
                imageVector = Icons.Filled.SystemUpdate,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text("${context.getString(R.string.update_available)} - ${info.latestVersion}", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = context.getString(R.string.new_version_available),
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (info.releaseNotes.isNotBlank()) {
                    HorizontalDivider()
                    Text(
                        text = info.releaseNotes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, info.releaseUrl.toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(context.getString(R.string.view_release))
            }
        },
        dismissButton = {
            TextButton(onClick = onSkipClick) {
                Text(context.getString(R.string.not_now))
            }
        }
    )
}
